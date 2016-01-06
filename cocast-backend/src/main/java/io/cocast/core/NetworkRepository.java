package io.cocast.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Singleton;
import io.cocast.admin.ThemeServices;
import io.cocast.auth.SecurityContext;
import io.cocast.util.*;
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

/**
 * Persistence methods for networks
 */
@Singleton
class NetworkRepository {

    private static Logger logger = LogManager.getLogger(NetworkRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    @Inject
    private ThemeServices themeServices;

    private CacheUtils cache = CacheUtils.getInstance(Network.class);
    private CacheUtils cacheMembership = CacheUtils.getInstance(NetworkMembership.class);

    /**
     * Creates a new network
     */
    public void create(Network network) throws Exception {
        //checks if the mnemonic is defined
        if (network.getMnemonic() == null) {
            network.setMnemonic(ExtraStringUtils.generateMnemonic(network.getName()));
        }

        //validate the theme
        validateTheme(network.getTheme());

        validate(network);

        //checks if exists
        Network existingNetwork = this.getAsRoot(network.getMnemonic());
        logger.debug("existingNetwork = " + existingNetwork);
        if (existingNetwork != null) {
            throw new ValidationException("Network with mnemonic = " + network.getMnemonic() + " already exists");
        }

        //insert
        firebaseUtils.saveAsRoot(network, "/networks/" + network.getMnemonic() + ".json");
        cache.invalidate(network.getMnemonic());

        //insert collaborators and owners
        firebaseUtils.saveString("owner", "/members/" + network.getCreatedBy() + "/" + network.getMnemonic() + ".json");
        List<String> listCollaborators = network.getCollaborators();
        for (String strCollaborator : listCollaborators) {
            firebaseUtils.saveString("collaborator", "/members/" + strCollaborator + "/" + network.getMnemonic() + ".json");
        }

        cacheMembership.invalidate(SecurityContext.get().userIdentification());
    }

    /**
     * Lists all networks that the user has access
     */
    public List<Network> list() throws Exception {
        List<Network> result = new ArrayList<Network>();
        List<NetworkMembership> membershipList = this.listMemberships();

        for (NetworkMembership membership : membershipList) {
            Network network = this.getAsRoot(membership.getNetworkMnemonic());
            if (logger.isDebugEnabled()) {
                logger.debug("Found network to list: " + network);
            }
            if (network != null) {
                result.add(network);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Network.list: returning " + result.size() + " entries: " + result);
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
            throw new CoCastCallException("Could not find network with mnemonic: " + network.getMnemonic(), 404);
        }

        validateTheme(network.getTheme());

        //update info
        network.setLastUpdate(DateUtils.now());
        network.setCreatedBy(existingNetwork.getCreatedBy());
        if (network.getName() == null) {
            network.setName(existingNetwork.getName());
        }
        if (network.getTheme() == null) {
            network.setTheme(existingNetwork.getTheme());
        }

        validate(network);

        if (network.getCollaborators() == null) {
            network.setCollaborators(existingNetwork.getCollaborators());
        } else {
            //remove collaborators
            List<String> listCollaborators = existingNetwork.getCollaborators();
            logger.debug("Collaborators to be removed = " + listCollaborators);
            for (String strCollaborator : listCollaborators) {
                firebaseUtils.saveString("removed", "/members/" + strCollaborator + "/" + network.getMnemonic() + ".json");
            }

            //add new collaborators
            listCollaborators = network.getCollaborators();
            for (String strCollaborator : listCollaborators) {
                firebaseUtils.saveString("collaborator", "/members/" + strCollaborator + "/" + network.getMnemonic() + ".json");
            }
        }

        //update
        firebaseUtils.saveAsRoot(network, "/networks/" + network.getMnemonic() + ".json");
        cache.invalidate(network.getMnemonic());
        cacheMembership.invalidate(SecurityContext.get().userIdentification());

        return network;
    }

    /**
     * Delete a network
     */
    public void delete(String mnemonic) throws Exception {
        Network existingNetwork = this.get(mnemonic);
        if (existingNetwork == null) {
            throw new CoCastCallException("Could not find network with mnemonic: " + mnemonic, 404);
        }

        if (!existingNetwork.isActive()) {
            throw new ValidationException("Network has been already deleted: " + mnemonic);
        }

        existingNetwork.setActive(false);
        existingNetwork.setLastUpdate(DateUtils.now());

        //update
        firebaseUtils.saveAsRoot(existingNetwork, "/networks/" + existingNetwork.getMnemonic() + ".json");
        cache.invalidate(existingNetwork.getMnemonic());

        //remove collaborators
        List<String> listCollaborators = existingNetwork.getCollaborators();
        logger.debug("Collaborators to be removed = " + listCollaborators);
        for (String strCollaborator : listCollaborators) {
            firebaseUtils.saveString("removed", "/members/" + strCollaborator + "/" + mnemonic + ".json");
        }

        //remove owner
        firebaseUtils.saveString("removed", "/members/" + existingNetwork.getCreatedBy() + "/" + mnemonic + ".json");

        cacheMembership.invalidate(SecurityContext.get().userIdentification());
    }

    /**
     * Get the networks the user has access
     */
    List<NetworkMembership> listMemberships() throws IOException, ExecutionException {
        //looks into the cache
        String uid = SecurityContext.get().userIdentification();
        return cacheMembership.get(uid, new NetworkMembershipLoader(uid));
    }

    /**
     * Validate the object for creation or update
     */
    private void validate(Network network) {
        if ((network == null) ||
                (network.getName() == null)) {
            throw new ValidationException("Network name is required");
        }
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

    private class NetworkMembershipLoader implements Callable<List<NetworkMembership>> {

        private String uid;

        public NetworkMembershipLoader(String uid) {
            this.uid = uid;
        }

        @Override
        public List<NetworkMembership> call() throws Exception {

            logger.debug("Getting memberships for " + uid);
            JsonNode jsonNode = firebaseUtils.listAsJsonNode("/members/" + uid + ".json");
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
    }

    /**
     * Validate the theme
     */
    private void validateTheme(String strTheme) throws Exception {
        if ((strTheme != null) && (!themeServices.exists(strTheme))) {
            throw new ValidationException("Theme doesn't exist: " + strTheme);
        }
    }


}
