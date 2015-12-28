package io.cocast.core;

import io.cocast.auth.SecurityContext;
import io.cocast.util.DateUtils;

import java.util.Date;

/**
 * Station
 */
public class Station {

    private String name;
    private String mnemonic;
    private String networkMnemonic;
    private String createdBy;
    private Date lastUpdate;
    private String theme;
    private boolean active;

    /**
     * Constructor
     */
    public Station() {
        lastUpdate = DateUtils.now();
        createdBy = SecurityContext.get().userIdentification();
        active = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", mnemonic='" + mnemonic + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", theme='" + theme + '\'' +
                ", active=" + active +
                '}';
    }
}
