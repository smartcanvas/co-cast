package io.cocast.core;

/**
 * Network membership of this user
 */
public class NetworkMembership {

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
