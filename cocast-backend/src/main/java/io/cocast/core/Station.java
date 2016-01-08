package io.cocast.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.cocast.auth.SecurityContext;
import io.cocast.util.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Station
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Station implements Serializable {

    private String name;
    private String mnemonic;
    private String networkMnemonic;
    private String location;
    private String theme;
    private List<String> channels;
    private String createdBy;
    private Date lastUpdate;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
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
                ", networkMnemonic='" + networkMnemonic + '\'' +
                ", location='" + location + '\'' +
                ", theme='" + theme + '\'' +
                ", channels=" + channels +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", active=" + active +
                '}';
    }
}
