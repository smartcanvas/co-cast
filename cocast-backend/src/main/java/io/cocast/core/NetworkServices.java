package io.cocast.core;

import javax.inject.Inject;
import javax.validation.ValidationException;

/**
 * Services for Networks
 */
public class NetworkServices {

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
}
