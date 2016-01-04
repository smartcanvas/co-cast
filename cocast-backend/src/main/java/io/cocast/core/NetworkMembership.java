package io.cocast.core;

import java.io.Serializable;

/**
 * Network membership of this user
 */
public class NetworkMembership implements Serializable {

    private String networkMnemonic;
    private String role;

    public String getNetworkMnemonic() {
        return networkMnemonic;
    }

    public void setNetworkMnemonic(String networkMnemonic) {
        this.networkMnemonic = networkMnemonic;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "NetworkMembership{" +
                "networkMnemonic='" + networkMnemonic + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
