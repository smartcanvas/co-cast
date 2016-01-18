package io.cocast.ext.people;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.core.NetworkServices;
import io.cocast.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
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
        if (existingPerson != null) {
            throw new ValidationException("Person with ID = " + person.getId() + " already exists. Name = "
                    + existingPerson.getDisplayName());
        }

        //validate the person
        this.validate(person);

        person.suppressDeviceRepetitions();

        //insert
        firebaseUtils.saveAsRoot(person, "/persons/" + person.getNetworkMnemonic() + "/" + person.getId() + ".json");

        //update the cache for specified person
        cache.set(generateCacheKey(person.getNetworkMnemonic(), person.getId()), person);

        //updates the anonymous list cache
        String listCacheKey = generateCacheListKey(person.getNetworkMnemonic(), null);
        List<Person> cachedListPerson = cacheList.get(listCacheKey);
        if (cachedListPerson != null) {
            cachedListPerson.add(person);
            cacheList.set(listCacheKey, cachedListPerson);
        }
    }

    /**
     * Update an existing person
     */
    public void update(Person person) throws Exception {

        try {
            networkServices.validate(person.getNetworkMnemonic());
        } catch (CoCastCallException exc) {
            //check if the person is valid with issuer
            networkServices.validateWithIssuer(person.getNetworkMnemonic());

            //check if the person is trying to update his own data
            if (!person.getEmail().equals(SecurityContext.get().email())) {
                throw new CoCastCallException("User " + SecurityContext.get().email() + " doesn't have access "
                        + "to update this person (" + person.getEmail() + ")", HttpServletResponse.SC_UNAUTHORIZED);
            }
        }

        //checks if exists
        Person existingPerson = this.get(person.getNetworkMnemonic(), person.getId());
        if (existingPerson == null) {
            throw new CoCastCallException("Person with ID = " + person.getId()
                    + " doens't exist. Use POST to create persons", 404);
        }

        person.setCreatedBy(existingPerson.getCreatedBy());
        person.setLastUpdate(DateUtils.now());
        person.merge(existingPerson);

        //validate the person
        this.validate(person);

        person.suppressDeviceRepetitions();

        //insert
        firebaseUtils.saveAsRoot(person, "/persons/" + person.getNetworkMnemonic() + "/" + person.getId() + ".json");

        //update the cache for specified person
        cache.set(generateCacheKey(person.getNetworkMnemonic(), person.getId()), person);

        //updates the anonymous list cache
        String listCacheKey = generateCacheListKey(person.getNetworkMnemonic(), null);
        List<Person> cachedListPerson = cacheList.get(listCacheKey);
        if (cachedListPerson != null) {
            int count = 0;
            for (Person cachedPerson : cachedListPerson) {
                if (cachedPerson.getId().equals(person.getId())) {
                    cachedListPerson.remove(count);
                    break;
                }
                count++;
            }
            cachedListPerson.add(person);
            cacheList.set(listCacheKey, cachedListPerson);
        }

        //clears the nominated cache list
        cacheList.invalidate(generateCacheListKey(person.getNetworkMnemonic(), person.getId()));
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
     * List all persons w/o pages
     */
    public List<Person> listAll(String networkMnemonic) throws Exception {
        networkServices.validateWithIssuer(networkMnemonic);

        String cacheKey = generateCacheListKey(networkMnemonic, null);

        //looks into the cache
        List<Person> listPerson = cacheList.get(cacheKey, new ListPersonLoader(networkMnemonic, null));
        if (listPerson == null) {
            //cannot cache null
            cache.invalidate(cacheKey);
            return new ArrayList<Person>();
        }

        return listPerson;
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
    public void delete(String networkMnemonic, String personId) throws Exception {
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

        //invalidate the cache for specified person
        cache.invalidate(generateCacheKey(networkMnemonic, personId));

        //clears the nominated cache list
        cacheList.invalidate(generateCacheListKey(networkMnemonic, personId));

        //updates the anonymous list cache
        String listCacheKey = generateCacheListKey(networkMnemonic, null);
        List<Person> cachedListPerson = cacheList.get(listCacheKey);
        if (cachedListPerson != null) {
            int count = 0;
            for (Person cachedPerson : cachedListPerson) {
                if (cachedPerson.getId().equals(personId)) {
                    cachedListPerson.remove(count);
                    break;
                }
                count++;
            }
            cacheList.set(listCacheKey, cachedListPerson);
        }
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

            List<Person> resultList = new ArrayList<Person>();

            //a list of persons
            String uri;
            if (StringUtils.isEmpty(this.email)) {
                uri = "/persons/" + networkMnemonic + ".json";
            } else {
                uri = "/persons/" + networkMnemonic + ".json?orderBy=" + URLEncoder.encode("\"email\"", "UTF-8")
                        + "&equalTo=" + URLEncoder.encode("\"" + this.email + "\"", "UTF-8");
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
