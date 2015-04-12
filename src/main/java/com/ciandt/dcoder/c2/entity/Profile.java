package com.ciandt.dcoder.c2.entity;

import java.util.Date;

public class Profile {

    private Long id;
    private String providerId;
    private String providerUserId;
    private String email;
    private String username;
    private String profileURL;
    private String coverURL;
    private String imageURL;
    private String displayName;
    private String tagLine;
    private String introduction;
    private String braggingRights;
    private String employerName;
    private String jobTitle;
    private String locale;
    private String gender;
    private String maritalStatus;
    private Date birthdate;
    private String position;
    private String manager;
    private String coach;
    private Long personId;
    private Date lastUpdated;
    private Integer securityLevel = 0;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getProviderId() {
        return providerId;
    }
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
    public String getProviderUserId() {
        return providerUserId;
    }
    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getProfileURL() {
        return profileURL;
    }
    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }
    public String getCoverURL() {
        return coverURL;
    }
    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getTagLine() {
        return tagLine;
    }
    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }
    public String getIntroduction() {
        return introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    public String getBraggingRights() {
        return braggingRights;
    }
    public void setBraggingRights(String braggingRights) {
        this.braggingRights = braggingRights;
    }
    public String getEmployerName() {
        return employerName;
    }
    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }
    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getMaritalStatus() {
        return maritalStatus;
    }
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    public Date getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public String getManager() {
        return manager;
    }
    public void setManager(String manager) {
        this.manager = manager;
    }
    public String getCoach() {
        return coach;
    }
    public void setCoach(String coach) {
        this.coach = coach;
    }
    public Long getPersonId() {
        return personId;
    }
    public void setPersonId(Long personId) {
        this.personId = personId;
    }
    public Date getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    public Integer getSecurityLevel() {
        return securityLevel;
    }
    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }   
    
}
