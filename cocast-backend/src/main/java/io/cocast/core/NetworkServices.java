package io.cocast.core;

import com.google.inject.Singleton;

import javax.inject.Inject;

/**
 * Business logic for Networks
 */
@Singleton
public class NetworkServices {

    @Inject
    private NetworkRepository networkRepository;

    /**
     * Creates a new network
     */
    public void create(Network network) throws Exception {

        //checks if the mnemonic is defined
        if (network.getMnemonic() == null) {
            network.setMnemonic(this.generateMnemonic(network.getName()));
        }
        networkRepository.create(network);
    }

    /**
     * Generates a mnemononic to this network
     */
    private String generateMnemonic(String name) {
        //TODO: refinar
        return name.toLowerCase();
    }
}
