package io.cocast.ext.match;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.ext.people.Person;
import io.cocast.ext.people.PersonServices;
import io.cocast.util.CacheUtils;
import io.cocast.util.PaginatedResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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

        for (int pos = offset; pos < listPerson.size(); pos++) {
            Person person = listPerson.get(pos);
            results.add(person);

            if (results.size() == limit) {
                break;
            }
        }

        return new PaginatedResponse(results, offset + results.size(), listPerson.size());
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

            if (logger.isDebugEnabled()) {
                logger.debug("Populating cache of shuffled persons for network = " + networkMnemonic);
            }

            List<Person> resultList = new ArrayList<Person>();

            //gets everybody
            List<Person> everybody = personServices.listAll(networkMnemonic);

            //TODO: implementar lógica aqui

            return everybody;
        }
    }
}
