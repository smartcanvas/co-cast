package com.ciandt.dcoder.c2.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Card {
    
    private Long id;
    private Date createdAt;
    private String mnemonic;
    private Boolean isFeatured;
    private boolean isAutoModerated;
    private Date updated;
    private Long authorId;
    private String authorDisplayName;
    private String authorImageURL;
    private String authorEmail;
    private String providerUserId;
    private List<String> categoryNames;
    private Date expirationDate;
    private Date publishingDate;
    private Integer securityLevel;
    
    //content
    private String title;
    private String description;
    private String content;
    private String providerContentId;
    private String providerContentURL;
    private String providerId;
    private Date providerUpdated;
    private Date providerPublished;
    
    //i18n
    private List<String> languages;
    private String detectedLanguage;
    private List<String> regions;
    private String geoCode;
    private String address;
    private String placeName;
    
    private String community;
    private String communityDisplayName;
    
    private List<Attachment> attachments;
    
    public Card() {
        categoryNames = new ArrayList<String>();
        languages = new ArrayList<String>();
        regions = new ArrayList<String>();
        attachments = new ArrayList<Attachment>();
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public String getMnemonic() {
        return mnemonic;
    }
    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    public boolean isAutoModerated() {
        return isAutoModerated;
    }
    public void setAutoModerated(boolean isAutoModerated) {
        this.isAutoModerated = isAutoModerated;
    }
    public Date getUpdated() {
        return updated;
    }
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    public Long getAuthorId() {
        return authorId;
    }
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
    public String getAuthorDisplayName() {
        return authorDisplayName;
    }
    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }
    public String getAuthorImageURL() {
        return authorImageURL;
    }
    public void setAuthorImageURL(String authorImageURL) {
        this.authorImageURL = authorImageURL;
    }
    public String getAuthorEmail() {
        return authorEmail;
    }
    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }
    public String getProviderUserId() {
        return providerUserId;
    }
    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }
    public List<String> getCategoryNames() {
        return categoryNames;
    }
    public void setCategoryNames(List<String> categoryNames) {
        this.categoryNames = categoryNames;
    }
    public void addCategory( String category ) {
        this.categoryNames.add(category);
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getPublishingDate() {
        return publishingDate;
    }

    public void setPublishingDate(Date publishingDate) {
        this.publishingDate = publishingDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProviderContentId() {
        return providerContentId;
    }

    public void setProviderContentId(String providerContentId) {
        this.providerContentId = providerContentId;
    }

    public String getProviderContentURL() {
        return providerContentURL;
    }

    public void setProviderContentURL(String providerContentURL) {
        this.providerContentURL = providerContentURL;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Date getProviderUpdated() {
        return providerUpdated;
    }

    public void setProviderUpdated(Date providerUpdated) {
        this.providerUpdated = providerUpdated;
    }

    public Date getProviderPublished() {
        return providerPublished;
    }

    public void setProviderPublished(Date providerPublished) {
        this.providerPublished = providerPublished;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public String getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getCommunityDisplayName() {
        return communityDisplayName;
    }

    public void setCommunityDisplayName(String communityDisplayName) {
        this.communityDisplayName = communityDisplayName;
    }
    
    public void addLanguage( String language ) {
        languages.add(language);
    }
    
    public void addRegion( String region ) {
        regions.add( region );
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
    
    public void addAttachment( Attachment attachment ) {
        this.attachments.add( attachment );
    }

}
