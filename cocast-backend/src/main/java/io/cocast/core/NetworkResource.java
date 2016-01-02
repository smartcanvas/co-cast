package io.cocast.core;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.util.APIResponse;
import io.cocast.util.CoCastCallException;
import org.apache.commons.lang3.StringUtils;
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

        network.setCreatedBy(SecurityContext.get().userIdentification());

        try {
            //calls the creation service
            networkRepository.create(network);
        } catch (ValidationException exc) {
            logger.error(exc.getMessage());
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

        List<Network> networkList;

        try {
            networkList = networkRepository.list();
        } catch (CoCastCallException exc) {
            logger.error("Error listing network", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error(exc.getMessage());
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error listing networks", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(networkList).build();
    }

    /**
     * Get a specific networks
     */
    @GET
    @Path("/{mnemonic}")
    public Response get(@PathParam("mnemonic") String mnemonic) {

        Network network;

        try {
            network = networkRepository.get(mnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error getting network", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error(exc.getMessage());
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error getting network", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(network).build();
    }

    /**
     * Updates a network
     */
    @PUT
    @Path("/{mnemonic}")
    public Response update(Network network, @PathParam("mnemonic") String mnemonic) {

        Network response;

        if (StringUtils.isEmpty(mnemonic)) {
            return APIResponse.badRequest("Network mnemonic is required as a path param").getResponse();
        } else {
            network.setMnemonic(mnemonic);
        }

        try {
            response = networkRepository.update(network);
        } catch (CoCastCallException exc) {
            logger.error("Error updating network", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error(exc.getMessage());
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error updating network", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.updated("Network updated. Mnemonic: " + response.getMnemonic()).getResponse();
    }

    /**
     * Delete a network
     */
    @DELETE
    @Path("/{mnemonic}")
    public Response delete(@PathParam("mnemonic") String mnemonic) {

        try {
            networkRepository.delete(mnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error getting network", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error(exc.getMessage());
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error getting network", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.deleted("Network deleted. Mnemonic: " + mnemonic).getResponse();
    }

}
