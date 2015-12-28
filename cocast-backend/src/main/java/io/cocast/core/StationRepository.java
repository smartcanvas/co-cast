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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
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
        Station existingStation = this.get(station.getNetworkMnemonic(), station.getMnemonic());
        logger.debug("existingStation = " + existingStation);
        if (existingStation != null) {
            throw new ValidationException("Station with mnemonic = " + station.getMnemonic() + " already exists");
        }

        //insert
        firebaseUtils.saveAsRoot(station, "/stations/" + station.getNetworkMnemonic() + "/" + station.getMnemonic() + ".json");
        cache.invalidate(station.getNetworkMnemonic());
    }

    /**
     * Lists all stations for a specific network
     */
    public List<Station> list(String networkMnemonic) throws Exception {

        validateNetwork(networkMnemonic);

        //looks into the cache
        List<Station> listStation = cache.get(networkMnemonic, new StationLoader(networkMnemonic));
        if (listStation == null) {
            //cannot cache null
            cache.invalidate(networkMnemonic);
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
        cache.invalidate(networkMnemonic);

        return station;
    }

    /**
     * Delete a station
     */
    public void delete(String networkMnemonic, String mnemonic) throws Exception {
        validateNetwork(networkMnemonic);

        Station existingStation = this.get(networkMnemonic, mnemonic);
        if (existingStation == null) {
            throw new ValidationException("Could not find station with mnemonic: " + mnemonic);
        }

        if (!existingStation.isActive()) {
            throw new ValidationException("Station has been already deleted: " + mnemonic);
        }

        existingStation.setActive(false);
        existingStation.setLastUpdate(DateUtils.now());

        //update
        firebaseUtils.saveAsRoot(existingStation, "/stations/" + networkMnemonic + "/" + existingStation.getMnemonic() + ".json");
        cache.invalidate(networkMnemonic);
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

    private class StationLoader implements Callable<List<Station>> {

        private String networkMnemonic;

        public StationLoader(String networkMnemonic) {
            this.networkMnemonic = networkMnemonic;
        }

        @Override
        public List<Station> call() throws Exception {

            logger.debug("Populating station cache...");
            List<Station> resultList = new ArrayList<Station>();

            //a list of stations
            String uri = "/stations/" + networkMnemonic + ".json";
            List<Station> allStations = firebaseUtils.listAsRoot(uri, Station.class);
            for (Station station : allStations) {
                if ((station != null) && (station.isActive())) {
                    resultList.add(station);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Adding station to cache " + station);
                    }
                }
            }

            return resultList;
        }
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
