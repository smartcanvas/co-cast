package io.cocast.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Captions to be shown on Co-Cast
 */
public class Captions {

    private String networkMnemonic;
    private String stationMnemonic;
    private String theme;
    private List<String> values;

    /**
     * Constructor
     */
    public Captions() {
        values = new ArrayList<>();
    }

    public String getNetworkMnemonic() {
        return networkMnemonic;
    }

    public void setNetworkMnemonic(String networkMnemonic) {
        this.networkMnemonic = networkMnemonic;
    }

    public String getStationMnemonic() {
        return stationMnemonic;
    }

    public void setStationMnemonic(String stationMnemonic) {
        this.stationMnemonic = stationMnemonic;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Captions{" +
                "networkMnemonic='" + networkMnemonic + '\'' +
                ", stationMnemonic='" + stationMnemonic + '\'' +
                ", theme='" + theme + '\'' +
                ", values=" + values +
                '}';
    }
}
