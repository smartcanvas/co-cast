package io.cocast.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.cocast.admin.ThemeServices;
import io.cocast.util.DateUtils;
import io.cocast.util.ExtraStringUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Persistence methods for Channels
 */
class ChannelRepository {

    private static Logger logger = LogManager.getLogger(ChannelRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    @Inject
    private NetworkServices networkServices;

    @Inject
    private ThemeServices themeServices;

    private static final Cache<String, List<Channel>> cache;

    static {
        //initializes the caches
        cache = CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterWrite(60 * 12, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Creates a new channel
     */
    public void create(Channel channel) throws Exception {

        //validate the theme and network
        validateTheme(channel.getTheme());
        networkServices.validate(channel.getNetworkMnemonic());

        //checks if the mnemonic is defined
        if (channel.getMnemonic() == null) {
            channel.setMnemonic(ExtraStringUtils.generateMnemonic(channel.getTitle()));
        }

        channel.setLastUpdate(DateUtils.now());
        validate(channel);

        //checks if exists
        Channel existingChannel = this.get(channel.getNetworkMnemonic(), channel.getMnemonic());
        logger.debug("existingChannel = " + existingChannel);
        if (existingChannel != null) {
            throw new ValidationException("Channel with mnemonic = " + channel.getMnemonic() + " already exists");
        }

        //insert
        firebaseUtils.saveAsRoot(channel, "/channels/" + channel.getNetworkMnemonic() + "/" + channel.getMnemonic() + ".json");
        cache.invalidate(channel.getNetworkMnemonic());
    }

    /**
     * Lists all channels for a specific network
     */
    public List<Channel> list(String networkMnemonic) throws Exception {

        networkServices.validate(networkMnemonic);

        //looks into the cache
        List<Channel> listChannel = cache.get(networkMnemonic, new ChannelLoader(networkMnemonic));
        if (listChannel == null) {
            //cannot cache null
            cache.invalidate(networkMnemonic);
        }

        return listChannel;
    }

    /**
     * Get a specific channel
     */
    public Channel get(String networkMnemonic, String mnemonic) throws Exception {
        networkServices.validate(networkMnemonic);

        List<Channel> allChannels = this.list(networkMnemonic);
        for (Channel channel : allChannels) {
            if (channel.getMnemonic().equals(mnemonic)) {
                return channel;
            }
        }

        return null;
    }

    /**
     * Update a channel
     */
    public Channel update(Channel channel, String networkMnemonic) throws Exception {

        networkServices.validate(networkMnemonic);
        validateTheme(channel.getTheme());

        Channel existingChannel = this.get(networkMnemonic, channel.getMnemonic());
        if (existingChannel == null) {
            throw new ValidationException("Could not find channel with mnemonic: " + channel.getMnemonic());
        }

        //update info
        channel.setLastUpdate(DateUtils.now());
        channel.setCreatedBy(existingChannel.getCreatedBy());
        if (channel.getTitle() == null) {
            channel.setTitle(existingChannel.getTitle());
        }
        if (channel.getTheme() == null) {
            channel.setTheme(existingChannel.getTheme());
        }
        if (channel.getTags() == null) {
            channel.setTags(existingChannel.getTags());
        }
        if (channel.getSource() == null) {
            channel.setSource(existingChannel.getSource());
        }
        if (channel.getMaxAgeInHours() == null) {
            channel.setMaxAgeInHours(existingChannel.getMaxAgeInHours());
        }
        if (channel.getLimitToFirst() == null) {
            channel.setLimitToFirst(existingChannel.getLimitToFirst());
        }
        if (channel.getOrderBy() == null) {
            channel.setOrderBy(existingChannel.getOrderBy());
        }
        if (channel.getOrderBy() == null) {
            channel.setOrderBy(existingChannel.getOrderBy());
        }

        //update
        firebaseUtils.saveAsRoot(channel, "/channels/" + networkMnemonic + "/" + channel.getMnemonic() + ".json");
        cache.invalidate(networkMnemonic);

        return channel;
    }

    /**
     * Delete a channel
     */
    public void delete(String networkMnemonic, String mnemonic) throws Exception {
        networkServices.validate(networkMnemonic);

        Channel existingChannel = this.get(networkMnemonic, mnemonic);
        if (existingChannel == null) {
            throw new ValidationException("Could not find channel with mnemonic: " + mnemonic);
        }

        if (!existingChannel.isActive()) {
            throw new ValidationException("Channel has been already deleted: " + mnemonic);
        }

        existingChannel.setActive(false);
        existingChannel.setLastUpdate(DateUtils.now());

        //update
        firebaseUtils.saveAsRoot(existingChannel, "/channels/" + networkMnemonic + "/" + existingChannel.getMnemonic() + ".json");
        cache.invalidate(networkMnemonic);
    }

    /**
     * Validate the channel
     */
    private void validate(Channel channel) {

        if (channel.getMnemonic() == null) {
            throw new ValidationException("Channel mnemonic is required");
        }
        if (channel.getNetworkMnemonic() == null) {
            throw new ValidationException("Channel network mnemonic is required");
        }
        if (channel.getTitle() == null) {
            throw new ValidationException("Channel title is required");
        }

    }

    /**
     * Validate the theme
     */
    private void validateTheme(String strTheme) throws Exception {
        if ((strTheme != null) && (!themeServices.exists(strTheme))) {
            throw new ValidationException("Theme doesn't exist: " + strTheme);
        }
    }

    private class ChannelLoader implements Callable<List<Channel>> {

        private String networkMnemonic;

        public ChannelLoader(String networkMnemonic) {
            this.networkMnemonic = networkMnemonic;
        }

        @Override
        public List<Channel> call() throws Exception {

            logger.debug("Populating channel cache...");
            List<Channel> resultList = new ArrayList<Channel>();

            //a list of channels
            String uri = "/channels/" + networkMnemonic + ".json";
            List<Channel> allChannels = firebaseUtils.listAsRoot(uri, Channel.class);
            for (Channel channel : allChannels) {
                if ((channel != null) && (channel.isActive())) {
                    resultList.add(channel);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Adding channel to cache " + channel);
                    }
                }
            }

            return resultList;
        }
    }
}