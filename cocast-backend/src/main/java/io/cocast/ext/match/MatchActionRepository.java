package io.cocast.ext.match;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.core.NetworkServices;
import io.cocast.ext.people.Person;
import io.cocast.ext.people.PersonServices;
import io.cocast.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Persistence methods for match actions
 */
@Singleton
public class MatchActionRepository {

    private static Logger logger = LogManager.getLogger(MatchActionRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    @Inject
    private NetworkServices networkServices;

    @Inject
    private PersonServices personServices;

    @Inject
    private MatchServices matchServices;


    private static CacheUtils cache = CacheUtils.getInstance(MatchAction.class);

    /**
     * Saves a match action
     *
     * @return If a match has occured or not
     */
    public boolean save(String networkMnemonic, String email, String action) throws Exception {
        networkServices.validateWithIssuer(networkMnemonic);

        String personId = Person.getIdFromEmail(SecurityContext.get().email());
        if (personId == null) {
            throw new ValidationException("Person ID cannot be null to list match actions");
        }
        if (email == null) {
            throw new ValidationException("You must inform the email of the targeted action");
        }

        MatchAction matchAction = new MatchAction();
        matchAction.setAction(action);
        matchAction.setPersonId(Person.getIdFromEmail(email));

        SaveMatchActionsThread saveMatchActionsThread = new SaveMatchActionsThread(networkMnemonic,
                matchAction, personId, email);
        ExecutorUtils.execute(saveMatchActionsThread);

        //evaluates a match
        if (MatchAction.LIKE.equals(action)) {
            return matchServices.evaluatesMatch(networkMnemonic, personId, email);
        } else {
            return false;
        }
    }

    /**
     * Gets all likes from someone
     */
    public List<MatchAction> listActions(String networkMnemonic, String email, String action) throws Exception {

        networkServices.validateWithIssuer(networkMnemonic);
        String personId = Person.getIdFromEmail(email);
        if (personId == null) {
            throw new ValidationException("Person ID cannot be null to list likes");
        }

        Person person = personServices.get(networkMnemonic, email);
        if (person == null) {
            throw new CoCastCallException("Person doesn't exist: " + email, HttpServletResponse.SC_NOT_FOUND);
        }

        //looks into the cache
        String cacheKey = generateKey(networkMnemonic, personId);
        List<MatchAction> matchActionList = cache.get(cacheKey, new MatchActionLoader(networkMnemonic, personId));
        List<MatchAction> results = new ArrayList<MatchAction>();
        if (matchActionList != null) {
            for (MatchAction matchAction : matchActionList) {
                if (action.equals(matchAction.getAction())) {
                    results.add(matchAction);
                }
            }
        }

        return results;
    }


    /**
     * Lists all match actions for a specific network and person
     */
    public List<MatchAction> list(String networkMnemonic, Person person) throws Exception {

        networkServices.validateWithIssuer(networkMnemonic);


        String personId;
        if (person != null) {
            personId = person.getId();
        } else {
            personId = Person.getIdFromEmail(SecurityContext.get().email());
        }
        if (personId == null) {
            throw new ValidationException("Person ID cannot be null to list match actions");
        }

        //looks into the cache
        String cacheKey = generateKey(networkMnemonic, personId);
        List<MatchAction> matchActionList = cache.get(cacheKey, new MatchActionLoader(networkMnemonic, personId));
        if (matchActionList == null) {
            //cannot cache null
            cache.invalidate(cacheKey);
        }

        return matchActionList;
    }

    /**
     * Generate the key
     */
    private String generateKey(String networkMnemonic, String personId) {
        return "matchActions_" + networkMnemonic + "_" + personId;
    }


    /**
     * Loads the cache for specific persons
     */
    private class MatchActionLoader implements Callable<List<MatchAction>> {

        private String networkMnemonic;
        private String personId;

        public MatchActionLoader(String networkMnemonic, String personId) {
            this.networkMnemonic = networkMnemonic;
            this.personId = personId;
        }


        @Override
        public List<MatchAction> call() throws Exception {

            //a list of match actions
            String uri = "/matchActions/" + networkMnemonic + "/" + personId + ".json";
            List<MatchAction> matchActions = firebaseUtils.listAsRoot(uri, MatchAction.class);
            return matchActions;
        }
    }

    /**
     * Thread to save actions in background
     */
    private class SaveMatchActionsThread extends AbstractRunnable {

        private MatchAction matchAction;
        private String personId;
        private String email;

        public SaveMatchActionsThread(String networkMnemonic, MatchAction matchAction,
                                      String personId, String email) {
            super(networkMnemonic);
            this.matchAction = matchAction;
            this.personId = personId;
            this.email = email;
        }

        @Override
        public void execute() throws Exception {

            String cacheKey = generateKey(getNetworkMnemonic(), personId);

            //updates the cache
            List<MatchAction> cachedMatchActionList = cache.get(cacheKey);
            if (cachedMatchActionList != null) {
                int count = 0;
                for (MatchAction actionInCache : cachedMatchActionList) {
                    if (actionInCache.getPersonId().equals(matchAction.getPersonId())) {
                        cachedMatchActionList.remove(count);
                        break;
                    }
                    count++;
                }

                cachedMatchActionList.add(matchAction);
                cache.set(cacheKey, cachedMatchActionList);
            }

            //insert
            firebaseUtils.saveAsRoot(matchAction, "/matchActions/" + getNetworkMnemonic() + "/" + personId + "/" +
                    Person.getIdFromEmail(email) + ".json");
        }

        @Override
        public String getJobName() {
            return "Save Match Actions Job";
        }
    }


}
