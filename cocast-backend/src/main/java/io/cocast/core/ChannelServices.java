package io.cocast.core;

import javax.inject.Inject;
import javax.validation.ValidationException;

/**
 * Services for channels
 */
public class ChannelServices {

    @Inject
    private ChannelRepository channelRepository;

    /**
     * Validates if the channel exists
     */
    public void validate(String networkMnemonic, String channelMnemonic) throws Exception {
        if (networkMnemonic == null) {
            throw new ValidationException("Network mnemonic cannot be null");
        }
        if (channelMnemonic == null) {
            throw new ValidationException("Channel mnemonic cannot be null");
        }

        Channel channel = channelRepository.get(networkMnemonic, channelMnemonic);
        if (channel == null) {
            throw new ValidationException("Channel doesn't exist: " + channelMnemonic + " (network = "
                    + networkMnemonic);
        }
    }
}