package io.cocast.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.util.CoCastCallException;
import io.cocast.util.DateUtils;
import io.cocast.util.ExtraStringUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
     * Lists all networks that the user has access
     */
    public List<Network> list() throws Exception {
        List<Network> result = new ArrayList<Network>();
        List<NetworkMembership> membershipList = this.listMemberships();

        for (NetworkMembership membership : membershipList) {
            Network network = this.getAsRoot(membership.getNetworkMnemonic());
            if (network != null) {
                result.add(network);
            }
        }

        return result;
    }

    /**
     * Get a specific network
     */
    public Network get(String mnemonic) throws Exception {
        List<Network> allNetworks = this.list();
        for (Network network : allNetworks) {
            if (network.getMnemonic().equals(mnemonic)) {
                return network;
            }
        }

        return null;
    }

    /**
     * Update a network
     */
    public Network update(Network network) throws Exception {
        Network existingNetwork = this.get(network.getMnemonic());
        if (existingNetwork == null) {
            throw new ValidationException("Could not find network with mnemonic: " + network.getMnemonic());
        }

        //Copy info
        existingNetwork.setName(network.getName());
        existingNetwork.setActive(network.isActive());
        existingNetwork.setColorPalette(network.getColorPalette());
        existingNetwork.setLastUpdate(DateUtils.now());

        //remove collaborators
        List<String> listCollaborators = existingNetwork.getCollaborators();
        logger.debug("Collaborators to be removed = " + listCollaborators);
        for (String strCollaborator : listCollaborators) {
            firebaseUtils.saveString("removed", "/members/" + strCollaborator + "/" + network.getMnemonic() + ".json");
        }

        //update
        firebaseUtils.save(network, "/networks/" + network.getMnemonic() + ".json");
        cache.invalidate(network.getMnemonic());

        //add new collaborators
        listCollaborators = network.getCollaborators();
        for (String strCollaborator : listCollaborators) {
            firebaseUtils.saveString("collaborator", "/members/" + strCollaborator + "/" + network.getMnemonic() + ".json");
        }

        return null;
    }

    /**
     * Get the networks the user has access
     */
    List<NetworkMembership> listMemberships() throws IOException {
        String uid = SecurityContext.get().userIdentification();

        logger.debug("Getting memberships for " + uid);
        JsonNode jsonNode = firebaseUtils.get("/members/" + uid + ".json");
        Iterator<String> iterator = jsonNode.fieldNames();
        List<NetworkMembership> membershipList = new ArrayList<NetworkMembership>();
        while (iterator.hasNext()) {
            String field = iterator.next();
            String role = jsonNode.findValue(field).asText();
            if (!"removed".equals(role)) {
                NetworkMembership networkMembership = new NetworkMembership();
                networkMembership.setNetworkMnemonic(field);
                networkMembership.setRole(role);
                membershipList.add(networkMembership);
            }
        }

        logger.debug("Returning membership = " + membershipList);

        return membershipList;
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
            if ((network != null) && (network.isActive())) {
                resultList.add(network);
            }
            return resultList;
        }
    }


}
