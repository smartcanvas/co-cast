package io.cocast.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Network resources (API).
 */
@Path("/api/v1/networks")
public class NetworkResource {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(NetworkResource.class);

    /**
     * Network Services
     */
    @Inject
    private NetworkServices networkServices;

    /**
     * Get all the resources available for this specific user
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Network> list() {
        return networkServices.list();
    }

}
