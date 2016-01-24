package io.cocast.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.cocast.auth.SecurityContext;
import io.cocast.util.DateUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A Network is the top-level organization structure for Co-cast. It represents a network that has many stations,
 * channels and schedules of contents that needs to be casted.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Network implements Serializable {

    private String name;
    private String mnemonic;
    private String liveToken;
    private String createdBy;
    private Date lastUpdate;
    private String theme;
    private List<String> collaborators;
    private boolean active;

    public Network() {
        lastUpdate = DateUtils.now();
        if (SecurityContext.get() != null) {
            createdBy = SecurityContext.get().userIdentification();
        }
        collaborators = new ArrayList<String>();
        SecureRandom random = new SecureRandom();
        liveToken = new BigInteger(130, random).toString(32);
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

    public List<String> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<String> collaborators) {
        this.collaborators = collaborators;
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

    public String getLiveToken() {
        return liveToken;
    }

    public void setLiveToken(String liveToken) {
        this.liveToken = liveToken;
    }

    @Override
    public String toString() {
        return "Network{" +
                "name='" + name + '\'' +
                ", mnemonic='" + mnemonic + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", theme='" + theme + '\'' +
                ", collaborators=" + collaborators +
                ", active=" + active +
                '}';
    }
}
