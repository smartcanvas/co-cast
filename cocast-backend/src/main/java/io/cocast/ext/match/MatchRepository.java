package io.cocast.ext.match;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.core.NetworkServices;
import io.cocast.ext.people.Person;
import io.cocast.ext.people.PersonServices;
import io.cocast.util.CacheUtils;
import io.cocast.util.CoCastCallException;
import io.cocast.util.DateUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.util.Date;
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

    @Inject
    private PersonServices personServices;

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
    public void save(String networkMnemonic, String personEmail1, String personEmail2) throws Exception {
        networkServices.validateWithIssuer(networkMnemonic);

        Person person1 = personServices.get(networkMnemonic, personEmail1);
        if (person1 == null) {
            throw new CoCastCallException("Person not found: " + personEmail1, HttpServletResponse.SC_NOT_FOUND);
        }

        Person person2 = personServices.get(networkMnemonic, personEmail2);
        if (person2 == null) {
            throw new CoCastCallException("Person not found: " + personEmail1, HttpServletResponse.SC_NOT_FOUND);
        }

        SaveMatchThread saveMatchThread = new SaveMatchThread(networkMnemonic, person1, person2);
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
            int count = 0;
            for (Match cachedMatch : cachedMatchList) {
                if (cachedMatch.getPerson().getId().equals(match.getPerson().getId())) {
                    cachedMatchList.remove(count);
                    break;
                }
                count++;
            }

            cachedMatchList.add(match);
            cache.set(cacheKey, cachedMatchList);
        }
    }

    /**
     * Lists all matches for a specific network and person
     */
    public List<Match> list(String networkMnemonic) throws Exception {

        networkServices.validateWithIssuer(networkMnemonic);
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
        private Person person1;
        private Person person2;

        public SaveMatchThread(String networkMnemonic, Person person1, Person person2) {
            this.networkMnemonic = networkMnemonic;
            this.person1 = person1;
            this.person2 = person2;
        }

        @Override
        public void run() {

            try {
                Date timestamp = DateUtils.now();

                //insert on both ends
                Match match1 = new Match(person1, timestamp);
                Match match2 = new Match(person2, timestamp);

                firebaseUtils.saveAsRoot(match1, "/matches/" + networkMnemonic + "/" + person2.getId() + "/" +
                        match1.getPerson().getId() + ".json");
                firebaseUtils.saveAsRoot(match2, "/matches/" + networkMnemonic + "/" + person1.getId() + "/" +
                        match2.getPerson().getId() + ".json");

                updatesCache(networkMnemonic, match1.getPerson().getId(), match2);
                updatesCache(networkMnemonic, match2.getPerson().getId(), match1);
            } catch (Exception exc) {
                logger.error("Error saving match between " + person1.getEmail() + " and " +
                        person2.getEmail(), exc);
            }
        }
    }
}
