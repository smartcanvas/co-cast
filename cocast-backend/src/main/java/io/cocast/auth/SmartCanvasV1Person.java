package io.cocast.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Person representation from Smart Canvas V1
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class SmartCanvasV1Person implements Serializable {

    private String displayName;
    private String email;
    private String imageURL;
    private String coverImageURL;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCoverImageURL() {
        return coverImageURL;
    }

    public void setCoverImageURL(String coverImageURL) {
        this.coverImageURL = coverImageURL;
    }

    @Override
    public String toString() {
        return "SmartCanvasV1Person{" +
                "displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", coverImageURL='" + coverImageURL + '\'' +
                '}';
    }
}
