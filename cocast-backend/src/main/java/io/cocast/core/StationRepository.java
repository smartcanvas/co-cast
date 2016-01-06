package io.cocast.core;

import com.google.inject.Singleton;
import io.cocast.admin.ThemeServices;
import io.cocast.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Persistence methods for Stations
 */
@Singleton
class StationRepository {

    private static Logger logger = LogManager.getLogger(StationRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    @Inject
    private NetworkServices networkServices;

    @Inject
    private ChannelServices channelServices;

    @Inject
    private ThemeServices themeServices;

    private static CacheUtils cache = CacheUtils.getInstance(Station.class);

    /**
     * Creates a new station
     */
    public void create(Station station) throws Exception {

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

        validate(station);

        //insert
        firebaseUtils.saveAsRoot(station, "/stations/" + station.getNetworkMnemonic() + "/" + station.getMnemonic() + ".json");
        cache.invalidate(station.getNetworkMnemonic());
    }

    /**
     * Lists all stations for a specific network
     */
    public List<Station> list(String networkMnemonic) throws Exception {

        networkServices.validate(networkMnemonic);

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
        networkServices.validate(networkMnemonic);

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

        station.setNetworkMnemonic(networkMnemonic);
        Station existingStation = this.get(networkMnemonic, station.getMnemonic());
        if (existingStation == null) {
            throw new CoCastCallException("Could not find station with mnemonic: " + station.getMnemonic(), 404);
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

        validate(station);

        //update
        firebaseUtils.saveAsRoot(station, "/stations/" + networkMnemonic + "/" + station.getMnemonic() + ".json");
        cache.invalidate(networkMnemonic);

        return station;
    }

    /**
     * Delete a station
     */
    public void delete(String networkMnemonic, String mnemonic) throws Exception {
        networkServices.validate(networkMnemonic);

        Station existingStation = this.get(networkMnemonic, mnemonic);
        if (existingStation == null) {
            throw new CoCastCallException("Could not find station with mnemonic: " + mnemonic, 404);
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
     * Validate
     */
    private void validate(Station station) throws Exception {

        //validate the theme
        validateTheme(station.getTheme());

        //validate the network
        networkServices.validate(station.getNetworkMnemonic());

        //validate the name
        if (station.getName() == null) {
            throw new ValidationException("Station name is required");
        }

        //validate the channels
        if (station.getChannels() != null) {
            for (String strChannel : station.getChannels()) {
                channelServices.validate(station.getNetworkMnemonic(), strChannel);
            }
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
