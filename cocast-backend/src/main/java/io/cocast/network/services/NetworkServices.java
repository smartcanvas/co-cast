package io.cocast.network.services;

import io.cocast.network.model.Network;

import java.util.List;

/**
 * Business logic for Networks
 */
public interface NetworkServices {

    /**
     * Say hello
     */
    String sayHello();

    /**
     * List the existing networks available for a specific user
     */
    List<Network> list();
}
