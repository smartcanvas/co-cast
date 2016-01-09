package io.cocast.ext.people;

import com.google.inject.Singleton;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

/**
 * Services for persons
 */
@Singleton
public class PersonServices {

    private static Logger logger = LogManager.getLogger(PersonServices.class.getName());

    @Inject
    private PersonRepository personRepository;

    /**
     * Returns all persons from a specific network
     */
    public List<Person> listAll(String networkMnemonic) throws Exception {
        return personRepository.listAll(networkMnemonic);
    }

    /**
     * Gets a person based on its email
     */
    public Person get(String networkMnemonic, String email) throws Exception {
        return personRepository.get(networkMnemonic, Person.getIdFromEmail(email));
    }

}
