package io.cocast.ext.people;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.cocast.auth.SecurityContext;
import io.cocast.util.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Person in CoCast
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person implements Serializable {

    private String id;
    private String networkMnemonic;
    private String displayName;
    private String email;
    private String position;
    private String companyName;
    private String alias;
    private String department;
    private String imageURL;
    private String backgroundImageURL;
    private String source;
    private String phoneNumber;
    private List<String> tags;
    private boolean isActive;
    private String createdBy;
    private Date lastUpdate;

    public Person() {
        tags = new ArrayList<String>();
        this.isActive = true;
        lastUpdate = DateUtils.now();
        createdBy = SecurityContext.get().userIdentification();
    }

    public String getId() {
        if (this.id == null) {
            id = Person.getIdFromEmail(this.email);
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNetworkMnemonic() {
        return networkMnemonic;
    }

    public void setNetworkMnemonic(String networkMnemonic) {
        this.networkMnemonic = networkMnemonic;
    }

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBackgroundImageURL() {
        return backgroundImageURL;
    }

    public void setBackgroundImageURL(String backgroundImageURL) {
        this.backgroundImageURL = backgroundImageURL;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Get ID from email
     */
    public static String getIdFromEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.replace('.', '-');
    }

    /**
     * Merge information of this person with another one
     */
    public void merge(Person anotherPerson) {
        //merge info
        if (this.getDisplayName() == null) {
            this.setDisplayName(anotherPerson.getDisplayName());
        }
        if (this.getEmail() == null) {
            this.setEmail(anotherPerson.getEmail());
        }
        if (this.getPosition() == null) {
            this.setPosition(anotherPerson.getPosition());
        }
        if (this.getCompanyName() == null) {
            this.setCompanyName(anotherPerson.getCompanyName());
        }
        if (this.getAlias() == null) {
            this.setAlias(anotherPerson.getAlias());
        }
        if (this.getBackgroundImageURL() == null) {
            this.setBackgroundImageURL(anotherPerson.getBackgroundImageURL());
        }
        if (this.getImageURL() == null) {
            this.setImageURL(anotherPerson.getImageURL());
        }
        if (this.getSource() == null) {
            this.setSource(anotherPerson.getSource());
        }
        if ((this.getTags() == null) || (this.getTags().size() == 0)) {
            this.setTags(anotherPerson.getTags());
        }
        if (this.getDepartment() == null) {
            this.setDepartment(anotherPerson.getDepartment());
        }
        if (this.getPhoneNumber() == null) {
            this.setPhoneNumber(anotherPerson.getPhoneNumber());
        }

    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", companyName='" + companyName + '\'' +
                ", alias='" + alias + '\'' +
                ", department='" + department + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", backgroundImageURL='" + backgroundImageURL + '\'' +
                ", source='" + source + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", tags=" + tags +
                ", isActive=" + isActive +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}