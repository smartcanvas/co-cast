package io.cocast.core;

import io.cocast.auth.SecurityContext;
import io.cocast.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A Network is the top-level organization structure for Co-cast. It represents a network that has many stations,
 * channels and schedules of contents that needs to be casted.
 */
public class Network {

    private String name;
    private String mnemonic;
    private String createdBy;
    private Date lastUpdate;
    private String colorPalette;
    private List<String> collaborators;

    public Network() {
        lastUpdate = DateUtils.now();
        createdBy = SecurityContext.get().userIdentification();
        collaborators = new ArrayList<String>();
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

    public String getColorPalette() {
        return colorPalette;
    }

    public void setColorPalette(String colorPalette) {
        this.colorPalette = colorPalette;
    }
}
