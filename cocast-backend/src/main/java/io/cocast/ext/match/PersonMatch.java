package io.cocast.ext.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Person info inside a match
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonMatch implements Serializable {

    public String id;
    public String email;

    public PersonMatch() {
    }

    public PersonMatch(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
