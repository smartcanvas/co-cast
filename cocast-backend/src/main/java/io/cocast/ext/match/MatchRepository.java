package io.cocast.ext.match;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.core.NetworkServices;
import io.cocast.ext.people.Person;
import io.cocast.util.CacheUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Persistence methods for matches
 */
@Singleton
public class MatchRepository {

    private static Logger logger = LogManager.getLogger(MatchRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    @Inject
    private NetworkServices networkServices;

    private static CacheUtils cache = CacheUtils.getInstance(Match.class);

    /**
     * Generate the key
     */
    private String generateKey(String networkMnemonic, String personId) {
        return "matches_" + networkMnemonic + "_" + personId;
    }

    /**
     * Saves a match
     */
    public void save(String networkMnemonic, Match match) throws Exception {
        networkServices.validate(networkMnemonic);

        if ((match.getPersonMatch1() == null) || (match.getPersonMatch2() == null)) {
            throw new ValidationException("A match must have two not-null persons: person1 = " + match.getPersonMatch1()
                    + ", person2 = " + match.getPersonMatch2());
        }

        SaveMatchThread saveMatchThread = new SaveMatchThread(networkMnemonic, match);
        saveMatchThread.start();
    }

    /**
     * Updates the cache
     */
    private void updatesCache(String networkMnemonic, String personId, Match match) {
        String cacheKey = generateKey(networkMnemonic, personId);

        //updates the cache
        List<Match> cachedMatchList = cache.get(cacheKey);
        if (cachedMatchList != null) {
            cachedMatchList.add(match);
            cache.set(cacheKey, cachedMatchList);
        }
    }

    /**
     * Lists all matches for a specific network and person
     */
    public List<Match> list(String networkMnemonic) throws Exception {

        networkServices.validate(networkMnemonic);
        String personId = Person.getIdFromEmail(SecurityContext.get().email());
        if (personId == null) {
            throw new ValidationException("Person ID cannot be null to list matches");
        }

        //looks into the cache
        String cacheKey = generateKey(networkMnemonic, personId);
        List<Match> matchList = cache.get(cacheKey, new MatchLoader(networkMnemonic, personId));
        if (matchList == null) {
            //cannot cache null
            cache.invalidate(cacheKey);
        }

        return matchList;
    }

    /**
     * Loads the cache for specific matches
     */
    private class MatchLoader implements Callable<List<Match>> {

        private String networkMnemonic;
        private String personId;

        public MatchLoader(String networkMnemonic, String personId) {
            this.networkMnemonic = networkMnemonic;
            this.personId = personId;
        }

        @Override
        public List<Match> call() throws Exception {

            if (logger.isDebugEnabled()) {
                logger.debug("Populating match cache for person ID = " + personId
                        + ", and network = " + networkMnemonic);
            }

            //a list of match actions
            String uri = "/matches/" + networkMnemonic + "/" + personId + ".json";
            List<Match> matches = firebaseUtils.listAsRoot(uri, Match.class);

            return matches;
        }
    }

    /**
     * Thread to save a match
     */
    public class SaveMatchThread extends Thread {

        private String networkMnemonic;
        private Match match;

        public SaveMatchThread(String networkMnemonic, Match match) {
            this.networkMnemonic = networkMnemonic;
            this.match = match;
        }

        @Override
        public void run() {

            try {
                //insert on both ends
                firebaseUtils.saveAsRoot(match, "/matches/" + networkMnemonic + "/" + match.getPersonMatch1().getId() + "/" +
                        match.getPersonMatch2().getId() + ".json");
                firebaseUtils.saveAsRoot(match.reverse(), "/matches/" + networkMnemonic + "/" + match.getPersonMatch2().getId() + "/" +
                        match.getPersonMatch1().getId() + ".json");

                updatesCache(networkMnemonic, match.getPersonMatch1().getId(), match);
                updatesCache(networkMnemonic, match.getPersonMatch2().getId(), match.reverse());
            } catch (Exception exc) {
                logger.error("Error saving match: " + match, exc);
            }
        }
    }
}
