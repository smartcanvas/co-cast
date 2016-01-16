package io.cocast.core;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.util.APIResponse;
import io.cocast.util.AbstractResource;
import io.cocast.util.CoCastCallException;
import io.cocast.util.log.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Network resources (API).
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/core/v1/networks")
@Singleton
public class NetworkResource extends AbstractResource {

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
    public Response create(@Context HttpServletRequest request, Network network) {
        long initTime = System.currentTimeMillis();

        network.setCreatedBy(SecurityContext.get().userIdentification());

        try {
            //calls the creation service
            networkRepository.create(network);
        } catch (ValidationException exc) {
            String message = exc.getMessage();
            logResult(message, request, "post", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (CoCastCallException exc) {
            String message = exc.getMessage();
            logResult(message, request, "post", 0, exc.getStatus(), initTime);
            return APIResponse.fromException(exc).getResponse();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "post", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "post", 0, HttpServletResponse.SC_CREATED, initTime);
        return APIResponse.created("Network created successfully with mnemonic: " + network.getMnemonic()).getResponse();
    }

    /**
     * Get a list of networks
     */
    @GET
    public Response list(@Context HttpServletRequest request) {
        long initTime = System.currentTimeMillis();

        List<Network> networkList;

        try {
            networkList = networkRepository.list();
        } catch (CoCastCallException exc) {
            String message = exc.getMessage();
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            String message = exc.getMessage();
            logResult(message, request, "get", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "get", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "get", networkList.size(), HttpServletResponse.SC_OK, initTime);
        return Response.ok(networkList).build();
    }

    /**
     * Get a specific networks
     */
    @GET
    @Path("/{mnemonic}")
    public Response get(@Context HttpServletRequest request,
                        @PathParam("mnemonic") String mnemonic) {
        long initTime = System.currentTimeMillis();

        Network network;

        try {
            network = networkRepository.get(mnemonic);
        } catch (CoCastCallException exc) {
            String message = exc.getMessage();
            logResult(message, request, "get", 0, exc.getStatus(), initTime);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            String message = exc.getMessage();
            logResult(message, request, "get", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "get", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "get", 1, HttpServletResponse.SC_OK, initTime);
        return Response.ok(network).build();
    }

    /**
     * Updates a network
     */
    @PUT
    @Path("/{mnemonic}")
    public Response update(@Context HttpServletRequest request,
                           Network network, @PathParam("mnemonic") String mnemonic) {
        long initTime = System.currentTimeMillis();

        Network response;

        if (StringUtils.isEmpty(mnemonic)) {
            logResult("Network mnemonic is required as a path param", request, "put", 0,
                    HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest("Network mnemonic is required as a path param").getResponse();
        } else {
            network.setMnemonic(mnemonic);
        }

        try {
            response = networkRepository.update(network);
        } catch (CoCastCallException exc) {
            String message = exc.getMessage();
            logResult(message, request, "put", 0, exc.getStatus(), initTime);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            String message = exc.getMessage();
            logResult(message, request, "put", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "put", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "put", 0, HttpServletResponse.SC_OK, initTime);
        return APIResponse.updated("Network updated. Mnemonic: " + response.getMnemonic()).getResponse();
    }

    /**
     * Delete a network
     */
    @DELETE
    @Path("/{mnemonic}")
    public Response delete(@Context HttpServletRequest request,
                           @PathParam("mnemonic") String mnemonic) {
        long initTime = System.currentTimeMillis();

        try {
            networkRepository.delete(mnemonic);
        } catch (CoCastCallException exc) {
            String message = exc.getMessage();
            logResult(message, request, "delete", 0, exc.getStatus(), initTime);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            String message = exc.getMessage();
            logResult(message, request, "delete", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "delete", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "delete", 0, HttpServletResponse.SC_NO_CONTENT, initTime);
        return APIResponse.deleted("Network deleted. Mnemonic: " + mnemonic).getResponse();
    }

    @Override
    protected String getModuleName() {
        return "core";
    }

    @Override
    protected String getResourceName() {
        return "networks";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
