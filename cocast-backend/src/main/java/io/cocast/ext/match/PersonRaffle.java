package io.cocast.ext.match;

import io.cocast.ext.people.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Executes a raffle with person
 */
public class PersonRaffle {

    public static final Integer HIGH_ODDS = 6;
    public static final Integer MID_ODDS = 2;
    public static final Integer LOW_ODDS = 1;

    private List<Person> personList;

    /**
     * Constructor
     */
    public PersonRaffle() {
        personList = new ArrayList<Person>();
    }

    /**
     * Adds a person for the raffling
     */
    public void add(Person person, Integer odds) {
        int times = 0;
        if (odds == null) {
            odds = LOW_ODDS;
        }

        for (int i = 0; i < odds; i++) {
            personList.add(person);
        }
    }

    /**
     * Returns the result of the raffle
     */
    public List<Person> raffle() {
        Collections.shuffle(personList);
        return new ArrayList<Person>(new LinkedHashSet<Person>(personList));
    }

}
