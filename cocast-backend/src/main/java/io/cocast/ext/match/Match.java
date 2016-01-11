package io.cocast.ext.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.cocast.ext.people.Person;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a match inside Co-Cast
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Match implements Serializable {

    private Person person;
    private Date timestamp;

    public Match() {
    }

    public Match(Person person, Date timestamp) {
        this();
        this.person = person;
        this.timestamp = timestamp;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Match{" +
                "person=" + person +
                ", timestamp=" + timestamp +
                '}';
    }
}
