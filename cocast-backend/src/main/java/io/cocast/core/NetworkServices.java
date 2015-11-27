package io.cocast.core;

import java.util.List;

/**
 * Business logic for Networks
 */
public interface NetworkServices {

    /**
     * List the existing networks available for a specific user
     */
    List<Network> list();
}
