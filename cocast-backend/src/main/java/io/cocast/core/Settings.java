package io.cocast.core;

import io.cocast.auth.SecurityContext;
import io.cocast.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Settings for a specific network
 */
public class Settings implements Serializable {

    private String networkMnemonic;
    private String name;
    private String value;
    private String description;
    private String createdBy;
    private Date lastUpdate;

    /**
     * Constructor
     */
    public Settings() {
        lastUpdate = DateUtils.now();
        createdBy = SecurityContext.get().userIdentification();
    }

    public String getNetworkMnemonic() {
        return networkMnemonic;
    }

    public void setNetworkMnemonic(String networkMnemonic) {
        this.networkMnemonic = networkMnemonic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "Settings{" +
                "networkMnemonic='" + networkMnemonic + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }

}
