package io.cocast.core;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.util.APIResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Network resources (API).
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/core/v1/networks")
@Singleton
public class NetworkResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(NetworkResource.class);

    /**
     * Network Services
     */
    @Inject
    private NetworkServices networkServices;

    /**
     * Creates a network
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNetwork(Network network) {

        //validates and defines the createdBy based on authorization token
        if ((network == null) ||
                (network.getName() == null)) {
            return APIResponse.badRequest("Network name is required").getResponse();
        }
        network.setCreatedBy(SecurityContext.get().userIdentification());

        try {
            //calls the creation service
            networkServices.create(network);
        } catch (Exception exc) {
            logger.error("Error creating configuration", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created(network.getMnemonic()).getResponse();
    }

}
