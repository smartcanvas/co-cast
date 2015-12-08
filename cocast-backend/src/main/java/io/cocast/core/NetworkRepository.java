package io.cocast.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Singleton;
import io.cocast.admin.Configuration;
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
        firebaseUtils.save(network, "/networks/" + network.getMnemonic() + ".json");
    }

    /**
     * Lists all networks
     */
    public List<Configuration> list() throws Exception {
        return firebaseUtils.get("/networks.json", Configuration.class);
    }

}
