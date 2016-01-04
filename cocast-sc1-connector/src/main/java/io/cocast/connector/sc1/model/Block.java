package io.cocast.connector.sc1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Smart Canvas block
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Block {

    //type = content
    private String type;
    private String title;
    private String summary;
    private String content;
    private String providerContentURL;

    //type = author
    private String authorDisplayName;
    private String authorAlias;
    private String authorId;
    private String authorImageURL;
    private String providerUserId;
    private Date publishDate;

    //type = photo
    private String imageURL;
    private String imageType;
    private Integer imageHeight;
    private Integer imageWidth;
    private String originalImageURL;

    //type = userActivity
    private boolean like;
    private boolean dislike;
    private boolean pin;
    private Integer likeCounter;
    private Integer dislikeCounter;
    private Integer facebookCounter;
    private Integer googleplusCounter;
    private Integer linkedinCounter;
    private Integer twitterCounter;
    private Integer totalCounter;
    private Integer pinCounter;
    private Integer commentCounter;

    //type = article
    private String contentURL;
    private String displayName;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProviderContentURL() {
        return providerContentURL;
    }

    public void setProviderContentURL(String providerContentURL) {
        this.providerContentURL = providerContentURL;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    public String getAuthorAlias() {
        return authorAlias;
    }

    public void setAuthorAlias(String authorAlias) {
        this.authorAlias = authorAlias;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorImageURL() {
        return authorImageURL;
    }

    public void setAuthorImageURL(String authorImageURL) {
        this.authorImageURL = authorImageURL;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getOriginalImageURL() {
        return originalImageURL;
    }

    public void setOriginalImageURL(String originalImageURL) {
        this.originalImageURL = originalImageURL;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isDislike() {
        return dislike;
    }

    public void setDislike(boolean dislike) {
        this.dislike = dislike;
    }

    public boolean isPin() {
        return pin;
    }

    public void setPin(boolean pin) {
        this.pin = pin;
    }

    public Integer getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(Integer likeCounter) {
        this.likeCounter = likeCounter;
    }

    public Integer getDislikeCounter() {
        return dislikeCounter;
    }

    public void setDislikeCounter(Integer dislikeCounter) {
        this.dislikeCounter = dislikeCounter;
    }

    public Integer getFacebookCounter() {
        return facebookCounter;
    }

    public void setFacebookCounter(Integer facebookCounter) {
        this.facebookCounter = facebookCounter;
    }

    public Integer getGoogleplusCounter() {
        return googleplusCounter;
    }

    public void setGoogleplusCounter(Integer googleplusCounter) {
        this.googleplusCounter = googleplusCounter;
    }

    public Integer getLinkedinCounter() {
        return linkedinCounter;
    }

    public void setLinkedinCounter(Integer linkedinCounter) {
        this.linkedinCounter = linkedinCounter;
    }

    public Integer getTwitterCounter() {
        return twitterCounter;
    }

    public void setTwitterCounter(Integer twitterCounter) {
        this.twitterCounter = twitterCounter;
    }

    public Integer getTotalCounter() {
        return totalCounter;
    }

    public void setTotalCounter(Integer totalCounter) {
        this.totalCounter = totalCounter;
    }

    public Integer getCommentCounter() {
        return commentCounter;
    }

    public void setCommentCounter(Integer commentCounter) {
        this.commentCounter = commentCounter;
    }

    public String getContentURL() {
        return contentURL;
    }

    public void setContentURL(String contentURL) {
        this.contentURL = contentURL;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getPinCounter() {
        return pinCounter;
    }

    public void setPinCounter(Integer pinCounter) {
        this.pinCounter = pinCounter;
    }

    @Override
    public String toString() {
        return "Block{" +
                "type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", providerContentURL='" + providerContentURL + '\'' +
                ", authorDisplayName='" + authorDisplayName + '\'' +
                ", authorAlias='" + authorAlias + '\'' +
                ", authorId='" + authorId + '\'' +
                ", authorImageURL='" + authorImageURL + '\'' +
                ", providerUserId='" + providerUserId + '\'' +
                ", publishDate=" + publishDate +
                ", imageURL='" + imageURL + '\'' +
                ", imageType='" + imageType + '\'' +
                ", imageHeight='" + imageHeight + '\'' +
                ", imageWidth='" + imageWidth + '\'' +
                ", originalImageURL='" + originalImageURL + '\'' +
                ", like=" + like +
                ", dislike=" + dislike +
                ", pin=" + pin +
                ", likeCounter=" + likeCounter +
                ", dislikeCounter=" + dislikeCounter +
                ", facebookCounter=" + facebookCounter +
                ", googleplusCounter=" + googleplusCounter +
                ", linkedinCounter=" + linkedinCounter +
                ", twitterCounter=" + twitterCounter +
                ", totalCounter=" + totalCounter +
                ", commentCounter=" + commentCounter +
                ", contentURL='" + contentURL + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
