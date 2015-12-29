package io.cocast.core;

import io.cocast.auth.SecurityContext;
import io.cocast.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Channel
 */
public class Channel {

    /**
     * Order By AGE
     */
    public static final String ORDER_BY_AGE = "age";

    /**
     * Order By POPULARITY
     */
    public static final String ORDER_BY_POPULARITY = "popularity";

    private String mnemonic;
    private String networkMnemonic;
    private String title;
    private String theme;
    private List<String> tags;
    private Integer maxAgeInHours;
    private Integer limitToFirst;
    private String orderBy;
    private boolean isActive;
    private String createdBy;
    private Date lastUpdate;

    /**
     * Constructor
     */
    public Channel() {
        this.limitToFirst = 3;
        this.orderBy = Channel.ORDER_BY_AGE;
        this.isActive = true;
        lastUpdate = DateUtils.now();
        createdBy = SecurityContext.get().userIdentification();
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getNetworkMnemonic() {
        return networkMnemonic;
    }

    public void setNetworkMnemonic(String networkMnemonic) {
        this.networkMnemonic = networkMnemonic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getMaxAgeInHours() {
        return maxAgeInHours;
    }

    public void setMaxAgeInHours(Integer maxAgeInHours) {
        this.maxAgeInHours = maxAgeInHours;
    }

    public Integer getLimitToFirst() {
        return limitToFirst;
    }

    public void setLimitToFirst(Integer limitToFirst) {
        this.limitToFirst = limitToFirst;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
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

    @Override
    public String toString() {
        return "Channel{" +
                "mnemonic='" + mnemonic + '\'' +
                ", networkMnemonic='" + networkMnemonic + '\'' +
                ", title='" + title + '\'' +
                ", theme='" + theme + '\'' +
                ", tags=" + tags +
                ", maxAgeInHours=" + maxAgeInHours +
                ", limitToFirst=" + limitToFirst +
                ", orderBy='" + orderBy + '\'' +
                ", isActive=" + isActive +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
