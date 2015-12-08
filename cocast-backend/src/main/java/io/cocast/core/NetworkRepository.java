package io.cocast.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import io.cocast.util.CoCastCallException;
import io.cocast.util.ExtraStringUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Persistence methods for networks
 */
@Singleton
class NetworkRepository {

    private static Logger logger = LogManager.getLogger(NetworkRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    private static final Cache<String, List<Network>> cache;

    static {
        //initializes the caches
        cache = CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Creates a new network
     */
    public void create(Network network) throws IOException, CoCastCallException, ExecutionException {
        //checks if the mnemonic is defined
        if (network.getMnemonic() == null) {
            network.setMnemonic(ExtraStringUtils.generateMnemonic(network.getName()));
        }

        //checks if exists
        Network existingNetwork = this.getAsRoot(network.getMnemonic());
        logger.debug("existingNetwork = " + existingNetwork);
        if (existingNetwork != null) {
            throw new ValidationException("Network with mnemonic = " + network.getMnemonic() + " already exists");
        }

        //insert
        firebaseUtils.save(network, "/networks/" + network.getMnemonic() + ".json");
        cache.invalidate(network.getMnemonic());

        //insert collaborators and owners
        firebaseUtils.saveString("owner", "/members/" + network.getCreatedBy() + "/" + network.getMnemonic() + ".json");
        List<String> listCollaborators = network.getCollaborators();
        for (String strCollaborator : listCollaborators) {
            firebaseUtils.saveString("collaborator", "/members/" + strCollaborator + "/" + network.getMnemonic() + ".json");
        }
    }

    /**
     * Lists all networks
     */
    public List<Network> list() throws Exception {
        return firebaseUtils.get("/networks.json", Network.class);
    }

    /**
     * Get by mnemonic as root
     */
    private Network getAsRoot(String mnemonic) throws IOException, ExecutionException {

        //looks into the cache
        List<Network> listNetwork = cache.get(mnemonic, new NetworkLoader(mnemonic));
        if ((listNetwork != null) && (listNetwork.size() > 0)) {
            return listNetwork.get(0);
        } else {
            //cannot cache null
            cache.invalidate(mnemonic);
            return null;
        }
    }

    private class NetworkLoader implements Callable<List<Network>> {

        private String mnemonic;

        public NetworkLoader(String mnemonic) {
            this.mnemonic = mnemonic;
        }

        @Override
        public List<Network> call() throws Exception {
            logger.debug("Populating network cache...");
            Network network = firebaseUtils.getAsRoot("/networks/" + mnemonic + ".json", Network.class);
            List<Network> resultList = new ArrayList<Network>();
            resultList.add(network);
            return resultList;
        }
    }

}
