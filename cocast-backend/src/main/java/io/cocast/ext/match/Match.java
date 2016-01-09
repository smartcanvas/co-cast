package io.cocast.ext.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Represents a match inside Co-Cast
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Match implements Serializable {

    private PersonMatch personMatch1;
    private PersonMatch personMatch2;

    public Match() {
    }

    public Match(PersonMatch personMatch1, PersonMatch personMatch2) {
        this.personMatch1 = personMatch1;
        this.personMatch2 = personMatch2;
    }

    public PersonMatch getPersonMatch1() {
        return personMatch1;
    }

    public void setPersonMatch1(PersonMatch personMatch1) {
        this.personMatch1 = personMatch1;
    }

    public PersonMatch getPersonMatch2() {
        return personMatch2;
    }

    public void setPersonMatch2(PersonMatch personMatch2) {
        this.personMatch2 = personMatch2;
    }

    /**
     * Returns the reversed match
     */
    public Match reverse() {
        return new Match(personMatch2, personMatch1);
    }
}
