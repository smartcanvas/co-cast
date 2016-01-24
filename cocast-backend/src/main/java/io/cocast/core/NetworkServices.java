package io.cocast.core;

import io.cocast.auth.SecurityContext;
import io.cocast.util.CoCastCallException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.util.List;

/**
 * Services for Networks
 */
public class NetworkServices {

    private Logger logger = LogManager.getLogger(NetworkServices.class.getName());

    @Inject
    private NetworkRepository networkRepository;

    /**
     * Return the Network based on the live token
     */
    public Network getFromLiveToken(String liveToken) throws Exception {
        if (liveToken == null) {
            return null;
        }

        List<Network> networkList = networkRepository.rawList();
        for (Network network : networkList) {
            if (liveToken.equals(network.getLiveToken())) {
                return network;
            }
        }
        return null;
    }

    /**
     * Validates if a user can write info to this network
     */
    public void canWrite(String networkMnemonic) throws Exception {

        if (SecurityContext.get().isRoot()) {
            return;
        }

        if (networkMnemonic == null) {
            throw new ValidationException("Network mnemonic cannot be null");
        }

        Network network = networkRepository.get(networkMnemonic);
        if (network == null) {
            throw new CoCastCallException("The user doesn't have access to network " + networkMnemonic,
                    HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * Validates if user can read info from this network
     */
    public void canRead(String networkMnemonic) throws Exception {

        if (networkMnemonic == null) {
            throw new ValidationException("Network mnemonic cannot be null");
        }

        //root can read all networks
        if (SecurityContext.get().isRoot()) {
            return;
        }

        //live tokens has email = <network>@cocast.io and can read data from this network
        if (SecurityContext.get().email() != null) {
            if (SecurityContext.get().email().equals(networkMnemonic + "@cocast.io")) {
                return;
            }
        }

        if (networkMnemonic.equals(SecurityContext.get().issuer())) {
            return;
        }

        this.canWrite(networkMnemonic);
    }
}
