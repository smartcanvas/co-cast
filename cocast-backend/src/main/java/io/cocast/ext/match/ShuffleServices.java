package io.cocast.ext.match;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.ext.people.Person;
import io.cocast.ext.people.PersonServices;
import io.cocast.util.CacheUtils;
import io.cocast.util.Email;
import io.cocast.util.PaginatedResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Services for shuffling cards
 */
@Singleton
public class ShuffleServices {

    private static Logger logger = LogManager.getLogger(ShuffleServices.class.getName());

    private static CacheUtils cache = CacheUtils.getInstance(List.class);

    @Inject
    private PersonServices personServices;

    @Inject
    private MatchActionRepository matchActionRepository;

    /**
     * Returns a shuffled list of persons based on some business rules
     */
    public PaginatedResponse shuffle(String networkMnemonic, Integer limit, Integer offset) throws Exception {


        String emailRequester = SecurityContext.get().email();
        Person requester = personServices.get(networkMnemonic, Person.getIdFromEmail(emailRequester));
        if (requester == null) {
            throw new ValidationException("Could not find person with ID = " + Person.getIdFromEmail(emailRequester));
        }
        String cacheKey = generateCacheKey(networkMnemonic, requester);


        //looks into the cache
        List<Person> listPerson = cache.get(cacheKey, new ShuffledListPersonLoader(networkMnemonic, requester));
        if (listPerson == null) {
            //cannot cache null
            cache.invalidate(cacheKey);
            return new PaginatedResponse(new ArrayList(), offset, 0);
        }

        if ((limit == 0) || (listPerson.size() <= offset)) {
            return new PaginatedResponse(new ArrayList(), offset, 0);
        }

        List<Person> results = new ArrayList<Person>();

        //list of previous match actions
        List<MatchAction> likes = matchActionRepository.listActions(networkMnemonic, requester.getEmail(),
                MatchAction.LIKE);
        List<MatchAction> dislikes = matchActionRepository.listActions(networkMnemonic, requester.getEmail(),
                MatchAction.DISLIKE);

        //gets the company of the user based on its email domain
        if ((requester.getEmail() == null) || (!Email.isValid(requester.getEmail()))) {
            throw new ValidationException("Requester email is null or invalid (" + requester.getEmail()
                    + "). Odd situation. Returning w/o doing anything");
        }
        int atPosition = requester.getEmail().indexOf("@");
        String companyDomain = requester.getEmail().substring(atPosition + 1);

        int count = 0;
        for (int pos = offset; pos < listPerson.size(); pos++) {
            count++;
            Person person = listPerson.get(pos);
            if (this.validate(person, companyDomain, likes, dislikes)) {
                results.add(person);
            }

            if (results.size() == limit) {
                break;
            }
        }

        return new PaginatedResponse(results, offset + count, listPerson.size());
    }

    /**
     * Validate if this person should be listed or not
     */
    private boolean validate(Person person, String companyDomain, List<MatchAction> likes,
                             List<MatchAction> dislikes) {

        //people from the same company should not see each other
        if (!StringUtils.isEmpty(person.getEmail()) && person.getEmail().endsWith(companyDomain)) {
            logger.debug("Discarding " + person.getEmail() + " because he is from the same company");
            return false;
        }

        //discard previous likes
        if (this.contains(likes, person.getId())) {
            logger.debug("Discarding " + person.getEmail() + " because there is a like already");
            return false;
        }

        //discard previous dislikes (chance = 80%)
        if (this.contains(dislikes, person.getId())) {

            if ((System.currentTimeMillis() % 10) < 2) {
                logger.debug("You are lucky! Giving another chance to like " + person.getId());
                return true;
            } else {
                logger.debug("Discarding " + person.getEmail() + " because there is a dislike already");
                return false;
            }
        }

        return true;
    }

    /**
     * Checks is the person is present in a list
     */
    private boolean contains(List<MatchAction> matchActionList, String personId) {

        for (MatchAction matchAction : matchActionList) {
            if (personId.equals(matchAction.getPersonId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Generate cache key
     */
    private String generateCacheKey(String networkMnemonic, Person requester) {
        List<String> tags = requester.getTags();
        StringBuffer key = new StringBuffer();
        key.append("shufflePersonList_");
        key.append(networkMnemonic + "_");
        for (String str : tags) {
            key.append(str + "_");
        }

        return key.toString().replace('&', '_').replace(' ', '_');
    }

    /**
     * Loads the cache for specific persons shuffled
     */
    private class ShuffledListPersonLoader implements Callable<List<Person>> {

        private String networkMnemonic;
        private Person requester;

        public ShuffledListPersonLoader(String networkMnemonic, Person requester) {
            this.networkMnemonic = networkMnemonic;
            this.requester = requester;
        }

        @Override
        public List<Person> call() throws Exception {

            List<Person> resultList = new ArrayList<Person>();

            //gets everybody
            List<Person> everybody = personServices.listAll(networkMnemonic);

            Shuffler shuffler = Shuffler.getInstance();
            everybody = shuffler.shuffle(networkMnemonic, everybody, requester);

            return everybody;
        }
    }
}
