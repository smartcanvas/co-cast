package io.cocast.ext.people;

import com.google.inject.Singleton;
import io.cocast.core.NetworkServices;
import io.cocast.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Persistence methods for Persons
 */
@Singleton
public class PersonRepository {

    private static Logger logger = LogManager.getLogger(PersonRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    @Inject
    private NetworkServices networkServices;

    private static CacheUtils cache = CacheUtils.getInstance(Person.class);
    private static CacheUtils cacheList = CacheUtils.getInstance(List.class);

    /**
     * Create a new person
     */
    public void create(Person person) throws Exception {

        networkServices.validate(person.getNetworkMnemonic());

        //checks if exists
        Person existingPerson = this.get(person.getNetworkMnemonic(), person.getId());
        logger.debug("existingPerson = " + existingPerson);
        if (existingPerson != null) {
            throw new ValidationException("Person with ID = " + person.getId() + " already exists. Name = "
                    + existingPerson.getDisplayName());
        }

        //validate the person
        this.validate(person);

        //insert
        firebaseUtils.saveAsRoot(person, "/persons/" + person.getNetworkMnemonic() + "/" + person.getId() + ".json");

        //update the cache for current user
        cache.set(generateCacheKey(person.getNetworkMnemonic(), person.getId()), person);

        //clears the cache list
        cacheList.invalidate(generateCacheListKey(person.getNetworkMnemonic(), person.getId()));
        cacheList.invalidate(generateCacheListKey(person.getNetworkMnemonic(), null));
    }

    /**
     * Update an existing person
     */
    public void update(Person person) throws Exception {

        networkServices.validate(person.getNetworkMnemonic());

        //checks if exists
        Person existingPerson = this.get(person.getNetworkMnemonic(), person.getId());
        logger.debug("existingPerson = " + existingPerson);
        if (existingPerson == null) {
            throw new CoCastCallException("Person with ID = " + person.getId()
                    + " doens't exist. Use POST to create persons", 404);
        }

        person.setCreatedBy(existingPerson.getCreatedBy());
        person.setLastUpdate(DateUtils.now());
        person.merge(existingPerson);

        //validate the person
        this.validate(person);

        //insert
        firebaseUtils.saveAsRoot(person, "/persons/" + person.getNetworkMnemonic() + "/" + person.getId() + ".json");

        //update the cache for current user
        cache.set(generateCacheKey(person.getNetworkMnemonic(), person.getId()), person);

        //clears the cache list
        cacheList.invalidate(generateCacheListKey(person.getNetworkMnemonic(), person.getId()));
        cacheList.invalidate(generateCacheListKey(person.getNetworkMnemonic(), null));
    }

    /**
     * List persons
     */
    public PaginatedResponse list(String networkMnemonic, String email, Integer limit, Integer offset) throws Exception {
        networkServices.validateWithIssuer(networkMnemonic);

        String cacheKey = generateCacheListKey(networkMnemonic, Person.getIdFromEmail(email));

        //looks into the cache
        List<Person> listPerson = cacheList.get(cacheKey, new ListPersonLoader(networkMnemonic, email));
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
            if (StringUtils.isEmpty(email)) {
                results.add(person);
            } else {
                if (email.equals(person.getEmail())) {
                    results.add(person);
                }
            }

            if (results.size() == limit) {
                break;
            }
        }

        return new PaginatedResponse(results, offset + results.size(), listPerson.size());
    }

    /**
     * Get a specific person
     */
    public Person get(String networkMnemonic, String id) throws Exception {
        networkServices.validateWithIssuer(networkMnemonic);
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
     * Deletes a person
     */
    public void delete(String personId, String networkMnemonic) throws Exception {
        networkServices.validate(networkMnemonic);

        Person existingPerson = this.get(networkMnemonic, personId);
        if (existingPerson == null) {
            throw new CoCastCallException("Could not find person with ID: " + personId, 404);
        }

        if (!existingPerson.isActive()) {
            throw new ValidationException("Person has been already deleted: " + personId);
        }

        existingPerson.setActive(false);
        existingPerson.setLastUpdate(DateUtils.now());

        //update
        firebaseUtils.saveAsRoot(existingPerson, "/persons/" + networkMnemonic + "/" + existingPerson.getId() + ".json");

        //invaliadte the cache for current user
        cache.invalidate(generateCacheKey(networkMnemonic, personId));

        //clears the cache list
        cacheList.invalidate(generateCacheListKey(networkMnemonic, personId));
        cacheList.invalidate(generateCacheListKey(networkMnemonic, null));
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
     * Generates the key for person list cache
     */
    private String generateCacheListKey(String networkMnemonic, String id) {
        return "personList_" + networkMnemonic + "_" + id;
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

    /**
     * Loads the cache for specific persons
     */
    private class ListPersonLoader implements Callable<List<Person>> {

        private String networkMnemonic;
        private String email;

        public ListPersonLoader(String networkMnemonic, String email) {
            this.networkMnemonic = networkMnemonic;
            this.email = email;
        }

        @Override
        public List<Person> call() throws Exception {

            if (logger.isDebugEnabled()) {
                logger.debug("Populating list person cache for network = " + networkMnemonic);
            }

            List<Person> resultList = new ArrayList<Person>();

            //a list of persons
            String uri;
            if (StringUtils.isEmpty(this.email)) {
                uri = "/persons/" + networkMnemonic + ".json";
            } else {
                uri = "/persons/" + networkMnemonic + ".json?orderBy=" + URLEncoder.encode("\"email\"", "UTF-8")
                        + "&equalTo=" + URLEncoder.encode("\"" + this.email + "\"", "UTF-8");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("URI = " + uri);
            }

            List<Person> allPersons = firebaseUtils.listAsRoot(uri, Person.class);
            for (Person person : allPersons) {
                if ((person != null) && (person.isActive())) {
                    resultList.add(person);
                }
            }

            return resultList;
        }
    }
}
