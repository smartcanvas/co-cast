package io.cocast.core;

import io.cocast.auth.SecurityContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;

/**
 * Services for Networks
 */
public class NetworkServices {

    private Logger logger = LogManager.getLogger(NetworkServices.class.getName());

    @Inject
    private NetworkRepository networkRepository;

    /**
     * Validates if a network exists or not. If not, throws a ValidateException
     */
    public void validate(String networkMnemonic) throws Exception {
        if (networkMnemonic == null) {
            throw new ValidationException("Network mnemonic cannot be null");
        }

        Network network = networkRepository.get(networkMnemonic);
        if (network == null) {
            throw new ValidationException("The user doesn't have access to network " + networkMnemonic);
        }
    }

    /**
     * Validates also considering the authentication process
     */
    public void validateWithIssuer(String networkMnemonic) throws Exception {
        if (networkMnemonic == null) {
            throw new ValidationException("Network mnemonic cannot be null");
        }

        if (networkMnemonic.equals(SecurityContext.get().issuer())) {
            logger.debug("Network validated because of the issuer: " + SecurityContext.get().issuer());
            return;
        }

        this.validate(networkMnemonic);
    }
}
