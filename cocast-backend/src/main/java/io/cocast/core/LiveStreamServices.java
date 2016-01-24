package io.cocast.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Singleton;
import io.cocast.admin.Theme;
import io.cocast.admin.ThemeServices;
import io.cocast.util.CacheUtils;
import io.cocast.util.CoCastCallException;
import io.cocast.util.DateUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Services for live stream
 */
@Singleton
public class LiveStreamServices {

    private static Logger logger = LogManager.getLogger(LiveStreamServices.class.getName());

    private static CacheUtils cache = CacheUtils.getInstance(LiveStream.class);

    @Inject
    private NetworkServices networkServices;

    @Inject
    private NetworkRepository networkRepository;

    @Inject
    private ChannelRepository channelRepository;

    @Inject
    private StationRepository stationRepository;

    @Inject
    private ContentRepository contentRepository;

    @Inject
    private ThemeServices themeServices;

    @Inject
    private FirebaseUtils firebaseUtils;

    /**
     * Gets the stream for a station
     */
    public LiveStream getStream(String networkMnemonic, String stationMnemonic) throws Exception {
        networkServices.canRead(networkMnemonic);
        String cacheKey = generateCacheKey(networkMnemonic, stationMnemonic);

        //looks into the cache
        LiveStream liveStream = cache.get(cacheKey, new LiveStreamLoader(networkMnemonic, stationMnemonic, this));
        if (liveStream == null) {
            cache.invalidate(cacheKey);
        }

        return liveStream;
    }

    /**
     * Reloads the channel with the list of contents provided
     */
    public void reloadChannel(LiveStream liveStream, Network network, Station station, Channel channel,
                              List<Content> contents) throws Exception {

        logger.debug("Reloading contents for channel " + channel.getMnemonic());

        //Reorder the content list based on orderBy criterium
        ContentComparator comparator;
        if (Channel.ORDER_BY_AGE.equals(channel.getOrderBy())) {
            comparator = new ContentComparator(ContentComparator.DATE);
        } else if (Channel.ORDER_BY_POPULARITY.equals(channel.getOrderBy())) {
            comparator = new ContentComparator(ContentComparator.POPULARITY);
        } else {
            comparator = new ContentComparator(ContentComparator.DATE);
        }
        contents.sort(comparator);

        //Select the list of contents to this channel
        List<Content> selectedContents = new ArrayList<>();
        Integer maxContents = channel.getLimitToFirst();
        if (maxContents == null) {
            maxContents = Content.DEFAULT_LIMIT;
        }
        if (maxContents <= 0) {
            return;
        }
        for (Content content : contents) {
            logger.debug("Checking content: " + content.getTitle() + " for channel " + channel.getMnemonic());
            if (this.hasMatch(channel, content)) {
                logger.debug("Content selected!");
                selectTheme(network, station, channel, content);
                if (StringUtils.isEmpty(content.getCategory())) {
                    content.setCategory(channel.getTitle());
                }
                selectedContents.add(content);
                if (selectedContents.size() >= maxContents) {
                    break;
                }
            }
        }

        liveStream.castContents(channel, selectedContents);
    }

    /**
     * Evaluates the impact and rebuilds data based on a new content
     */
    public void onNewContentArrived(Content content) throws Exception {

        Network network = networkRepository.get(content.getNetworkMnemonic());
        if (network == null) {
            throw new ValidationException("Network not found: " + content.getNetworkMnemonic());
        }

        this.processNewContentArrivedEvent(network, content, true);
    }

    /**
     * Evaluates the impact and rebuilds data based on a new content
     */
    public void onNewContentArrivedInBulkMode(String networkMnemonic, List<Content> contents) throws Exception {

        Network network = networkRepository.get(networkMnemonic);

        if (network == null) {
            throw new ValidationException("Network not found: " + networkMnemonic);
        }

        Set<Station> stationSet = new HashSet<>();
        for (Content content : contents) {
            stationSet.addAll(this.processNewContentArrivedEvent(network, content, false));
        }

        for (Station station : stationSet) {
            this.notifyClient(networkMnemonic, station.getMnemonic());
        }
    }

    /**
     * Evalutes a new contents
     */
    private List<Station> processNewContentArrivedEvent(Network network, Content content, boolean commit) throws Exception {

        //impacted live streams
        List<Station> result = new ArrayList<>();

        List<Station> stations = stationRepository.list(content.getNetworkMnemonic());
        boolean hasChanged = false;
        for (Station station : stations) {
            LiveStream liveStream = this.getStream(network.getMnemonic(), station.getMnemonic());

            List<String> strChannelList = station.getChannels();
            if (strChannelList != null) {
                for (String strChannel : strChannelList) {
                    Channel channel = channelRepository.get(content.getNetworkMnemonic(), strChannel);
                    if (channel != null) {
                        if (processNewContent(content, liveStream, network, station, channel)) {
                            logger.debug("Content " + content.getTitle() + " changed the live stream");
                            hasChanged = true;
                        }
                    }
                }
            }

            if (hasChanged) {
                result.add(station);
                liveStream.rebuildContents(station);
                cache.set(generateCacheKey(liveStream.getNetworkMnemonic(), liveStream.getStationMnemonic()),
                        liveStream);
                if (commit) {
                    this.notifyClient(liveStream.getNetworkMnemonic(), liveStream.getStationMnemonic());
                }
            }
        }

        return result;
    }

    /**
     * Notify the client that the content has changed
     */
    private void notifyClient(String networkMnemonic, String stationMnemonic) throws JsonProcessingException {
        String strLastUpdated = DateUtils.now().toString();
        firebaseUtils.saveStringAsRoot(strLastUpdated, "/live/" + networkMnemonic + "/" + stationMnemonic
                + "/serverStatus/lastUpdated.json");
    }

    /**
     * Process the impact of a new content into a livestream
     *
     * @return If the content was selected and changed the channel configuration
     */
    private boolean processNewContent(Content content, LiveStream liveStream, Network network, Station station,
                                      Channel channel) throws Exception {

        logger.debug("Evaluating the impact of content " + content.getTitle()
                + " into channel " + channel.getMnemonic());

        if (!hasMatch(channel, content)) {
            return false;
        } else {

            List<Content> channelContents = liveStream.listContentsByChannel(channel.getMnemonic());
            if ((channelContents == null) || (channelContents.size() == 0)) {
                this.selectTheme(network, station, channel, content);
                if (StringUtils.isEmpty(content.getCategory())) {
                    content.setCategory(channel.getTitle());
                }
                channelContents = new ArrayList<>();
                channelContents.add(content);
                liveStream.castContents(channel, channelContents);
                return true;
            }

            //gets the last element of the list
            Content lastElement = channelContents.get(channelContents.size() - 1);
            ContentComparator comparator;
            if (Channel.ORDER_BY_AGE.equals(channel.getOrderBy())) {
                comparator = new ContentComparator(ContentComparator.DATE);
            } else if (Channel.ORDER_BY_POPULARITY.equals(channel.getOrderBy())) {
                comparator = new ContentComparator(ContentComparator.POPULARITY);
            } else {
                throw new ValidationException("Invalid order by: " + channel.getOrderBy());
            }

            if (comparator.compare(content, lastElement) < 0) {
                this.selectTheme(network, station, channel, content);
                if (StringUtils.isEmpty(content.getCategory())) {
                    content.setCategory(channel.getTitle());
                }
                if (channel.getLimitToFirst() == channelContents.size()) {
                    channelContents.remove(channelContents.size() - 1);
                }
                channelContents.add(content);
                Collections.sort(channelContents, comparator);
                liveStream.castContents(channel, channelContents);
                return true;
            } else {
                return false;
            }
        }
    }

    private void selectTheme(Network network, Station station, Channel channel, Content content) throws Exception {

        String themeMnemonic;
        if (!StringUtils.isEmpty(content.getThemeMnemonic())) {
            themeMnemonic = content.getThemeMnemonic();
        } else if (!StringUtils.isEmpty(channel.getTheme())) {
            themeMnemonic = channel.getTheme();
        } else if (!StringUtils.isEmpty(station.getTheme())) {
            themeMnemonic = station.getTheme();
        } else if (!StringUtils.isEmpty(network.getTheme())) {
            themeMnemonic = network.getTheme();
        } else {
            throw new ValidationException("Could not find a theme for content " + content);
        }

        Theme theme = themeServices.get(themeMnemonic);
        content.setTheme(theme);
    }

    /**
     * Checks if a content matches with the channel criteria
     */
    private boolean hasMatch(Channel channel, Content content) {

        //date of the content
        if (channel.getMaxAgeInHours() != null) {
            Date contentDate = content.getDate();
            Long ageInHours = (System.currentTimeMillis() - contentDate.getTime()) / (1000 * 60 * 60);
            if (ageInHours > channel.getMaxAgeInHours()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Discarding: content is too old... ageInHours = " + ageInHours + " and the max is " +
                            channel.getMaxAgeInHours());
                }
                return false;
            }
        }

        //content source
        if (!StringUtils.isEmpty(channel.getSource())) {
            if (!channel.getSource().equalsIgnoreCase(content.getSource())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Discarding: content is not from expected source = " + channel.getSource()
                            + ". It is from " + channel.getSource());
                }
                return false;
            }
        }

        //tags... must be contained into content tags
        if (channel.getTags() != null) {
            if (content.getTags() == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Discarding: content tags is null and channel requires " + channel.getTags());
                }
                return false;
            } else {
                if (!content.getTags().containsAll(channel.getTags())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Discarding: content tags " + content.getTags()
                                + " does not contains channel tags " + channel.getTags());
                    }
                    return false;
                }
            }
        }

        //author
        if (!StringUtils.isEmpty(channel.getAuthor())) {
            if (!channel.getAuthor().equals(content.getAuthorId())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Discarding: author must be " + channel.getAuthor()
                            + " but another author found " + content.getAuthorId());
                }
                return false;
            }
        }

        return true;
    }

    /**
     * Generate the cache key
     */
    private String generateCacheKey(String networkMnemonic, String stationMnemonic) {
        return "liveStream_" + networkMnemonic + "_" + stationMnemonic;
    }

    private class LiveStreamLoader implements Callable<LiveStream> {

        private String networkMnemonic;
        private String stationMnemonic;
        private LiveStreamServices liveStreamServices;

        public LiveStreamLoader(String networkMnemonic, String stationMnemonic, LiveStreamServices liveStreamServices) {
            this.networkMnemonic = networkMnemonic;
            this.stationMnemonic = stationMnemonic;
            this.liveStreamServices = liveStreamServices;
        }

        @Override
        public LiveStream call() throws Exception {
            LiveStream liveStream = new LiveStream();
            liveStream.setNetworkMnemonic(networkMnemonic);
            liveStream.setStationMnemonic(stationMnemonic);

            //get the network
            Network network = networkRepository.get(networkMnemonic);
            if (network == null) {
                throw new ValidationException("Network cannot be null: mnemonic = " + networkMnemonic);
            }

            //get the station
            Station station = stationRepository.get(networkMnemonic, stationMnemonic);
            if (station == null) {
                throw new CoCastCallException("Station not found: " + stationMnemonic
                        , HttpServletResponse.SC_BAD_REQUEST);
            }

            //get network contents
            List<Content> contentList = contentRepository.list(networkMnemonic);

            List<String> channelList = station.getChannels();
            for (String strChannel : channelList) {
                Channel channel = channelRepository.get(networkMnemonic, strChannel);
                if (channel == null) {
                    throw new Exception("Channel doesn't exist: " + strChannel + ". Database inconsistent?");
                } else {
                    liveStreamServices.reloadChannel(liveStream, network, station, channel, contentList);
                }
            }

            liveStream.rebuildContents(station);

            return liveStream;
        }
    }
}
