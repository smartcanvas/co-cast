package io.cocast.core;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.util.APIResponse;
import io.cocast.util.CoCastCallException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

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

    @Inject
    private NetworkRepository networkRepository;

    /**
     * Creates a network
     */
    @POST
    public Response create(Network network) {

        //validates and defines the createdBy based on authorization token
        if ((network == null) ||
                (network.getName() == null)) {
            return APIResponse.badRequest("Network name is required").getResponse();
        }
        network.setCreatedBy(SecurityContext.get().userIdentification());

        try {
            //calls the creation service
            networkRepository.create(network);
        } catch (ValidationException exc) {
            logger.error("Error creating network", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (CoCastCallException exc) {
            logger.error("Error creating network", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (Exception exc) {
            logger.error("Error creating network", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created("Network created successfully with mnemonic: " + network.getMnemonic()).getResponse();
    }

    /**
     * Get a list of networks
     */
    @GET
    public Response list() {

        List<Network> networkList = null;

        try {
            networkList = networkRepository.list();
        } catch (ValidationException exc) {
            logger.error("Error listing networks", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error listing networks", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(networkList).build();
    }

}
