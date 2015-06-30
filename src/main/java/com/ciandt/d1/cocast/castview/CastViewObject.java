package com.ciandt.d1.cocast.castview;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Entity that represents an object that will be shown in a cast view by C2
 * 
 * @author Daniel Viveiros
 */
@SuppressWarnings("serial")
@Entity
public class CastViewObject implements Serializable {
		
	/* Basic Info */
	@Id
	private String mnemonic;
	
	private Date date;
	private String title;
	private String summary;
	private String content;
	private Boolean isCasted;
	private String categoryNames;
	private String type;
	
	/* Author */
	private String authorId;
	private String authorDisplayName;
	private String authorImageURL;
	
	/* Content */
	private String contentImageURL;
	private Integer contentImageWidth;
	private Integer contentImageHeight;
	
	/* Provider Info */
	private String providerId;
	private String providerUserId;
	private String providerContentURL;
	
	/* Counters */
	private Integer likeCounter = 0;
	private Integer shareCounter = 0;
	private Integer pinCounter = 0;
	
	public String getMnemonic() {
		return mnemonic;
	}
	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Boolean getIsCasted() {
		return isCasted;
	}
	public void setIsCasted(Boolean isCasted) {
		this.isCasted = isCasted;
	}
	public String getCategoryNames() {
		return categoryNames;
	}
	public void setCategoryNames(String categoryNames) {
		this.categoryNames = categoryNames;
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
	public String getContentImageURL() {
		return contentImageURL;
	}
	public void setContentImageURL(String contentImageURL) {
		this.contentImageURL = contentImageURL;
	}
	public Integer getContentImageWidth() {
		return contentImageWidth;
	}
	public void setContentImageWidth(Integer contentImageWidth) {
		this.contentImageWidth = contentImageWidth;
	}
	public Integer getContentImageHeight() {
		return contentImageHeight;
	}
	public void setContentImageHeight(Integer contentImageHeight) {
		this.contentImageHeight = contentImageHeight;
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
	public String getProviderContentURL() {
		return providerContentURL;
	}
	public void setProviderContentURL(String providerContentURL) {
		this.providerContentURL = providerContentURL;
	}
	public Integer getLikeCounter() {
		return likeCounter;
	}
	public void setLikeCounter(Integer likeCounter) {
		this.likeCounter = likeCounter;
	}
	public Integer getShareCounter() {
		return shareCounter;
	}
	public void setShareCounter(Integer shareCounter) {
		this.shareCounter = shareCounter;
	}
	public Integer getPinCounter() {
		return pinCounter;
	}
	public void setPinCounter(Integer pinCounter) {
		this.pinCounter = pinCounter;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getPopularity() {
		return getLikeCounter() + getShareCounter();
	}
	
    @Override
    public String toString() {
        return "CastViewObject [mnemonic=" + mnemonic + ", date=" + date + ", title=" + title + ", summary=" + summary
                + ", content=" + content + ", isCasted=" + isCasted + ", categoryNames=" + categoryNames + ", type="
                + type + ", authorId=" + authorId + ", authorDisplayName=" + authorDisplayName + ", authorImageURL="
                + authorImageURL + ", contentImageURL=" + contentImageURL + ", contentImageWidth=" + contentImageWidth
                + ", contentImageHeight=" + contentImageHeight + ", providerId=" + providerId + ", providerUserId="
                + providerUserId + ", providerContentURL=" + providerContentURL + ", likeCounter=" + likeCounter
                + ", shareCounter=" + shareCounter + ", pinCounter=" + pinCounter + "]";
    }
	
	
}
