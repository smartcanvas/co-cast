package io.cocast.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.cocast.auth.SecurityContext;
import io.cocast.util.DateUtils;
import io.cocast.util.ExtraStringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Entity that represents an object that will be shown in a channel
 *
 * @author Daniel Viveiros
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Content implements Serializable {

    /* Basic Info */
    private String id;
    private String networkMnemonic;
    private String createdBy;
    private Date lastUpdated;
    private boolean isActive;

    private Date date;
    private String title;
    private String summary;
    private List<String> tags;
    private String type;

    /* Author */
    private String authorId;
    private String authorDisplayName;
    private String authorImageURL;

    /* Image */
    private String imageURL;
    private Integer imageWidth;
    private Integer imageHeight;

    /* Source Info */
    private String source;
    private String sourceContentURL;

    /* Counters */
    private Integer likeCounter = 0;

    /**
     * Constructor
     */
    public Content() {
        this.lastUpdated = DateUtils.now();
        if ((SecurityContext.get() != null) && (SecurityContext.get().userIdentification() != null)) {
            this.setCreatedBy(SecurityContext.get().userIdentification());
        }
        this.isActive = true;
    }

    public String getId() {
        if (id != null) {
            return id;
        } else {
            if (this.getTitle() != null) {
                return this.getSource() + "_" + ExtraStringUtils.generateMnemonic(this.getTitle());
            } else {
                return getSource() + "_" + System.currentTimeMillis();
            }
        }
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceContentURL() {
        return sourceContentURL;
    }

    public void setSourceContentURL(String sourceContentURL) {
        this.sourceContentURL = sourceContentURL;
    }

    public Integer getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(Integer likeCounter) {
        this.likeCounter = likeCounter;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", tags=" + tags +
                ", type='" + type + '\'' +
                ", authorId='" + authorId + '\'' +
                ", authorDisplayName='" + authorDisplayName + '\'' +
                ", authorImageURL='" + authorImageURL + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", source='" + source + '\'' +
                ", sourceContentURL='" + sourceContentURL + '\'' +
                ", likeCounter=" + likeCounter +
                '}';
    }
}

