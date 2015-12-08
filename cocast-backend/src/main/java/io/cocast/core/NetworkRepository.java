package io.cocast.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Singleton;
import io.cocast.util.FirebaseException;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

/**
 * Persistence methods for networks
 */
@Singleton
public class NetworkRepository {

    private static Logger logger = LogManager.getLogger(NetworkRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    /**
     * Creates a new network
     */
    public void create(Network network) throws JsonProcessingException, FirebaseException {
        //checks if the mnemonic is defined
        if (network.getMnemonic() == null) {
            network.setMnemonic(this.generateMnemonic(network.getName()));
        }

        firebaseUtils.save(network, "/networks/" + network.getMnemonic() + ".json");
    }

    /**
     * Lists all networks
     */
    public List<Network> list() throws Exception {
        return firebaseUtils.get("/networks.json", Network.class);
    }

    /**
     * Generates a mnemononic to this network
     */
    private String generateMnemonic(String name) {
        //TODO: refinar
        return name.toLowerCase();
    }

}
