package io.cocast.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import io.cocast.admin.ThemeServices;
import io.cocast.util.DateUtils;
import io.cocast.util.ExtraStringUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Persistence methods for Stations
 */
@Singleton
class StationRepository {

    private static Logger logger = LogManager.getLogger(StationRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    @Inject
    private NetworkRepository networkRepository;

    @Inject
    private ThemeServices themeServices;

    private static final Cache<String, List<Station>> cache;

    static {
        //initializes the caches
        cache = CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }


    /**
     * Creates a new station
     */
    public void create(Station station) throws Exception {

        //validate the theme
        validateTheme(station.getTheme());

        validateNetwork(station.getNetworkMnemonic());

        //checks if the mnemonic is defined
        if (station.getMnemonic() == null) {
            station.setMnemonic(ExtraStringUtils.generateMnemonic(station.getName()));
        }

        //checks if exists
        Station existingStation = this.getAsRoot(station.getNetworkMnemonic(), station.getMnemonic());
        logger.debug("existingStation = " + existingStation);
        if (existingStation != null) {
            throw new ValidationException("Station with mnemonic = " + station.getMnemonic() + " already exists");
        }

        //insert
        firebaseUtils.saveAsRoot(station, "/stations/" + station.getNetworkMnemonic() + "/" + station.getMnemonic() + ".json");
        invalidateCache(station.getNetworkMnemonic(), station.getMnemonic());
    }

    /**
     * Lists all stations for a specific network
     */
    public List<Station> list(String networkMnemonic) throws Exception {

        validateNetwork(networkMnemonic);
        String cacheKey = "_all_|" + networkMnemonic;

        //looks into the cache
        List<Station> listStation = cache.get(cacheKey, new StationLoader(networkMnemonic, null));
        if (listStation == null) {
            //cannot cache null
            cache.invalidate(cacheKey);
        }

        return listStation;
    }

    /**
     * Get a specific station
     */
    public Station get(String networkMnemonic, String mnemonic) throws Exception {
        validateNetwork(networkMnemonic);

        List<Station> allStations = this.list(networkMnemonic);
        for (Station station : allStations) {
            if (station.getMnemonic().equals(mnemonic)) {
                return station;
            }
        }

        return null;
    }


    /**
     * Update a station
     */
    public Station update(Station station, String networkMnemonic) throws Exception {

        validateNetwork(networkMnemonic);
        validateTheme(station.getTheme());

        Station existingStation = this.get(networkMnemonic, station.getMnemonic());
        if (existingStation == null) {
            throw new ValidationException("Could not find station with mnemonic: " + station.getMnemonic());
        }

        //update info
        station.setLastUpdate(DateUtils.now());
        station.setCreatedBy(existingStation.getCreatedBy());
        if (station.getName() == null) {
            station.setName(existingStation.getName());
        }
        if (station.getTheme() == null) {
            station.setTheme(existingStation.getTheme());
        }
        if (station.getLocation() == null) {
            station.setLocation(existingStation.getLocation());
        }

        //update
        firebaseUtils.saveAsRoot(station, "/stations/" + networkMnemonic + "/" + station.getMnemonic() + ".json");
        invalidateCache(station.getNetworkMnemonic(), station.getMnemonic());

        return station;
    }

    /**
     * Checks if the user has access to the specific network
     */
    private void validateNetwork(String networkMnemonic) throws Exception {
        if (networkMnemonic == null) {
            throw new ValidationException("Network mnemonic cannot be null");
        }

        Network network = networkRepository.get(networkMnemonic);
        if (network == null) {
            throw new ValidationException("The user doesn't have access to network " + networkMnemonic);
        }
    }

    /**
     * Get a list of stations
     */
    private Station getAsRoot(String networkMnemonic, String mnemonic) throws IOException, ExecutionException {

        String cacheKey = mnemonic + "|" + networkMnemonic;

        //looks into the cache
        List<Station> listStation = cache.get(cacheKey, new StationLoader(networkMnemonic, mnemonic));
        if ((listStation != null) && (listStation.size() > 0)) {
            return listStation.get(0);
        } else {
            //cannot cache null
            cache.invalidate(mnemonic);
            return null;
        }
    }

    private class StationLoader implements Callable<List<Station>> {

        private String mnemonic;
        private String networkMnemonic;

        public StationLoader(String networkMnemonic, String mnemonic) {
            this.mnemonic = mnemonic;
            this.networkMnemonic = networkMnemonic;
        }

        @Override
        public List<Station> call() throws Exception {

            logger.debug("Populating station cache...");
            List<Station> resultList;

            if (mnemonic != null) {
                //just one station
                String uri = "/stations/" + networkMnemonic + "/" + mnemonic + ".json";
                Station station = firebaseUtils.getAsRoot(uri, Station.class);
                resultList = new ArrayList<Station>();
                if ((station != null) && (station.isActive())) {
                    resultList.add(station);
                }
            } else {
                //a list of stations
                String uri = "/stations/" + networkMnemonic + ".json";
                resultList = firebaseUtils.listAsRoot(uri, Station.class);
            }


            return resultList;
        }
    }

    /**
     * Invalidate the caches
     */
    private void invalidateCache(String networkMnemonic, String mnemonic) {
        String cacheAllKey = "_all_|" + networkMnemonic;
        cache.invalidate(cacheAllKey);
        cache.invalidate(mnemonic + "|" + networkMnemonic);
    }

    /**
     * Validate the theme
     */
    private void validateTheme(String strTheme) throws Exception {
        if ((strTheme != null) && (!themeServices.exists(strTheme))) {
            throw new ValidationException("Theme doesn't exist: " + strTheme);
        }
    }

}
