package com.ciandt.d1.cocast.castview;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Meta information for a specific cast view
 * 
 * @author <a href="mailto:viveiros@ciandt.com">Daniel Viveiros</a>
 */
@SuppressWarnings("serial")
@Entity
public class CastView implements Serializable {
    
    @Id
    private String mnemonic;
    private String title;
    private String nextCastViewMnemonic;
    private String headerBackgroundColor;
    private String headerColor;
    private String progressContainerColor;
    private String activeProgressColor;
    private Integer maxResults;
    private Integer maxAgeInHours;
    private String categoryFilter;
    private String orderBy;
    private Boolean isDefault;
    private String strategy;
    
    public String getMnemonic() {
        return mnemonic;
    }
    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getNextCastViewMnemonic() {
        return nextCastViewMnemonic;
    }
    public void setNextCastViewMnemonic(String nextCastViewMnemonic) {
        this.nextCastViewMnemonic = nextCastViewMnemonic;
    }
    public String getHeaderBackgroundColor() {
        return headerBackgroundColor;
    }
    public void setHeaderBackgroundColor(String headerBackgroundColor) {
        this.headerBackgroundColor = headerBackgroundColor;
    }
    public String getHeaderColor() {
        return headerColor;
    }
    public void setHeaderColor(String headerColor) {
        this.headerColor = headerColor;
    }
    public String getProgressContainerColor() {
        return progressContainerColor;
    }
    public void setProgressContainerColor(String progressContainerColor) {
        this.progressContainerColor = progressContainerColor;
    }
    public String getActiveProgressColor() {
        return activeProgressColor;
    }
    public void setActiveProgressColor(String activeProgressColor) {
        this.activeProgressColor = activeProgressColor;
    }
    public Integer getMaxResults() {
        return maxResults;
    }
    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }
    public Integer getMaxAgeInHours() {
        return maxAgeInHours;
    }
    public void setMaxAgeInHours(Integer maxAgeInHours) {
        this.maxAgeInHours = maxAgeInHours;
    }
    public String getCategoryFilter() {
        return categoryFilter;
    }
    public void setCategoryFilter(String categoryFilter) {
        this.categoryFilter = categoryFilter;
    }
    public String getOrderBy() {
        return orderBy;
    }
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
    public Boolean getIsDefault() {
        return isDefault;
    }
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    public String getStrategy() {
        return strategy;
    }
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
    
    @Override
    public String toString() {
        return "CastView [mnemonic=" + mnemonic + ", title=" + title + ", nextCastViewMnemonic=" + nextCastViewMnemonic
                + ", headerBackgroundColor=" + headerBackgroundColor + ", headerColor=" + headerColor
                + ", progressContainerColor=" + progressContainerColor + ", activeProgressColor=" + activeProgressColor
                + ", maxResults=" + maxResults + ", maxAgeInHours=" + maxAgeInHours + ", categoryFilter="
                + categoryFilter + ", orderBy=" + orderBy + ", isDefault=" + isDefault + "]";
    }
    
    

}
