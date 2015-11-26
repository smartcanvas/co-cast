package io.cocast.core;

import java.util.Date;

/**
 * A Network is the top-level organization structure for Co-cast. It represents a network that has many stations,
 * channels and schedules of contents that needs to be casted.
 */
public class Network {

    /**
     * Network name
     */
    private String name;

    /**
     * Owner
     */
    private String owner;

    /**
     * Date created
     */
    private Date createdDate;

    public Network() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
