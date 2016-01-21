package io.cocast.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.jsonwebtoken.lang.Collections;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Content to be shown live on Co-Cast
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveStream implements Serializable {

    private static Logger logger = LogManager.getLogger(LiveStream.class.getName());

    private String networkMnemonic;
    private String stationMnemonic;
    private Captions captions;
    private List<Content> contents;

    @JsonIgnore
    private Map<String, List<Content>> channelContentMap;

    /**
     * Constructor
     */
    public LiveStream() {
        contents = new ArrayList<>();
        channelContentMap = new HashMap<>();
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

    public Captions getCaptions() {
        return captions;
    }

    public void setCaptions(Captions captions) {
        this.captions = captions;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void castContents(Channel channel, List<Content> contents) {
        if (logger.isDebugEnabled()) {
            logger.debug("Casting " + contents.size() + " contents to channel " + channel.getMnemonic());
        }
        channelContentMap.put(channel.getMnemonic(), contents);
    }

    /**
     * Rebuild the list of contents
     */
    public void rebuildContents(Station station) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug("Rebuilding contents for stations " + station);
        }

        this.contents = new ArrayList<>();

        List<String> channels = station.getChannels();
        if (logger.isDebugEnabled()) {
            logger.debug("Channels are " + channels);
        }

        for (String strChannel : channels) {
            List<Content> channelContents = channelContentMap.get(strChannel);
            if (logger.isDebugEnabled()) {
                logger.debug("Contents for channel " + strChannel + " are " + channelContents);
            }

            if (!Collections.isEmpty(channelContents)) {
                contents.addAll(channelContents);
                if (logger.isDebugEnabled()) {
                    logger.debug(channelContents.size() + " contents added to list of contents");
                }
            }
        }
    }

    /**
     * Return the list of contents for a specific channel
     */
    public List<Content> listContentsByChannel(String channelMnemonic) {
        return channelContentMap.get(channelMnemonic);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiveStream that = (LiveStream) o;

        if (!networkMnemonic.equals(that.networkMnemonic)) return false;
        return stationMnemonic.equals(that.stationMnemonic);

    }

    @Override
    public int hashCode() {
        int result = networkMnemonic.hashCode();
        result = 31 * result + stationMnemonic.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LiveStream{" +
                "stationMnemonic='" + stationMnemonic + '\'' +
                ", captions=" + captions +
                ", contents=" + contents +
                '}';
    }
}
