package io.cocast.ext.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.ValidationException;
import java.io.Serializable;

/**
 * Represents a like or dislike
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchAction implements Serializable {

    public static final String LIKE = "like";
    public static final String DISLIKE = "dislike";


    public String personId;
    public String action;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        if (!LIKE.equals(action) && !DISLIKE.equals(action)) {
            throw new ValidationException("Invalid action: " + action + ". Supported are " + LIKE + ", " + DISLIKE);
        }
        this.action = action;
    }

    @Override
    public String toString() {
        return "MatchAction{" +
                "personId='" + personId + '\'' +
                ", action='" + action + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchAction that = (MatchAction) o;

        return personId != null ? personId.equals(that.personId) : that.personId == null;

    }

    @Override
    public int hashCode() {
        return personId != null ? personId.hashCode() : 0;
    }
}
