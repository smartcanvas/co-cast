package io.cocast.ext.people;

import com.google.inject.Singleton;
import io.cocast.core.NetworkServices;
import io.cocast.util.CacheUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.util.concurrent.Callable;

/**
 * Persistence methods for Persons
 */
@Singleton
class PersonRepository {

    private static Logger logger = LogManager.getLogger(PersonRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    @Inject
    private NetworkServices networkServices;

    private static CacheUtils cache = CacheUtils.getInstance(Person.class);

    /**
     * Create or update a new person
     */
    public void save(Person person) throws Exception {

        networkServices.validate(person.getNetworkMnemonic());

        //validate the person
        this.validate(person);

        //insert
        firebaseUtils.saveAsRoot(person, "/persons/" + person.getNetworkMnemonic() + "/" + person.getId() + ".json");
        cache.set(generateCacheKey(person.getNetworkMnemonic(), person.getId()), person);
    }

    /**
     * Get a specific person
     */
    public Person get(String networkMnemonic, String id) throws Exception {
        networkServices.validate(networkMnemonic);
        String cacheKey = generateCacheKey(networkMnemonic, id);

        //looks into the cache
        Person person = cache.get(cacheKey, new PersonLoader(networkMnemonic, id));
        if (person == null) {
            //cannot cache null
            cache.invalidate(cacheKey);
        }

        return person;
    }


    /**
     * Validate if the person is ready to be created or updated
     */
    private void validate(Person person) {

        if (person.getEmail() == null) {
            throw new ValidationException("Email is required for persons");
        }
        if (person.getDisplayName() == null) {
            throw new ValidationException("Display name is required for persons");
        }
        if (person.getSource() == null) {
            throw new ValidationException("Source is required for persons");
        }
    }

    /**
     * Generates the key for person cache
     */
    private String generateCacheKey(String networkMnemonic, String id) {
        return networkMnemonic + "_" + id;
    }

    /**
     * Loads the cache for specific persons
     */
    private class PersonLoader implements Callable<Person> {

        private String networkMnemonic;
        private String id;

        public PersonLoader(String networkMnemonic, String id) {
            this.networkMnemonic = networkMnemonic;
            this.id = id;
        }

        @Override
        public Person call() throws Exception {

            if (logger.isDebugEnabled()) {
                logger.debug("Populating person cache for ID = " + id + ", and network = " + networkMnemonic);
            }

            //a list of persons
            String uri = "/persons/" + networkMnemonic + "/" + id + ".json";
            Person person = firebaseUtils.getAsRoot(uri, Person.class);
            if ((person == null) || (!person.isActive())) {
                return null;
            } else {
                return person;
            }
        }
    }
}
