package io.cocast.core;

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
 * Resources for Channels (APIs)
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/core/v1/channels")
public class ChannelResource extends AbstractResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(ChannelResource.class);

    @Inject
    private ChannelRepository channelRepository;

    /**
     * Creates a channel
     */
    @POST
    @Path("/{networkMnemonic}")
    public Response create(@Context HttpServletRequest request, Channel channel, @PathParam("networkMnemonic") String networkMnemonic) {

        long initTime = System.currentTimeMillis();

        //validates the network mnemonic
        if (StringUtils.isEmpty(networkMnemonic) && StringUtils.isEmpty(channel.getNetworkMnemonic())) {
            logResult("Network mnemonic is required", request, "post", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest("Network mnemonic is required").getResponse();
        }
        if (!StringUtils.isEmpty(networkMnemonic)) {
            channel.setNetworkMnemonic(networkMnemonic);
        }
        channel.setCreatedBy(SecurityContext.get().userIdentification());

        try {
            //calls the creation service
            channelRepository.create(channel);
        } catch (ValidationException exc) {
            logResult(exc.getMessage(), request, "post", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
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

        logResult("OK", request, "post", 0, HttpServletResponse.SC_OK, initTime);
        return APIResponse.created("Channel created successfully with mnemonic: " + channel.getMnemonic()).getResponse();
    }

    /**
     * Get a list of channels
     */
    @GET
    @Path("/{networkMnemonic}")
    public Response list(@Context HttpServletRequest request, @PathParam("networkMnemonic") String networkMnemonic) {

        long initTime = System.currentTimeMillis();

        List<Channel> channelList;

        try {
            channelList = channelRepository.list(networkMnemonic);
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

        logResult("OK", request, "get", channelList.size(), HttpServletResponse.SC_OK, initTime);
        return Response.ok(channelList).build();
    }

    /**
     * Get a specific channel
     */
    @GET
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response get(@Context HttpServletRequest request,
                        @PathParam("networkMnemonic") String networkMnemonic,
                        @PathParam("mnemonic") String mnemonic) {

        long initTime = System.currentTimeMillis();

        Channel channel;

        try {
            channel = channelRepository.get(networkMnemonic, mnemonic);
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
        return Response.ok(channel).build();
    }

    /**
     * Updates a channel
     */
    @PUT
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response update(@Context HttpServletRequest request,
                           Channel channel,
                           @PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("mnemonic") String mnemonic) {

        long initTime = System.currentTimeMillis();

        Channel response;

        if (StringUtils.isEmpty(mnemonic)) {
            logResult("Channel mnemonic is required as a path param", request, "put", 0,
                    HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest("Channel mnemonic is required as a path param").getResponse();
        } else {
            channel.setMnemonic(mnemonic);
        }

        try {
            response = channelRepository.update(channel, networkMnemonic);
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
        return APIResponse.updated("Channel updated. Mnemonic: " + response.getMnemonic()).getResponse();
    }

    /**
     * Delete a channel
     */
    @DELETE
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response delete(@Context HttpServletRequest request,
                           @PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("mnemonic") String mnemonic) {

        long initTime = System.currentTimeMillis();

        try {
            channelRepository.delete(networkMnemonic, mnemonic);
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
        return APIResponse.deleted("Channel deleted. Mnemonic: " + mnemonic).getResponse();
    }

    @Override
    protected String getModuleName() {
        return "core";
    }

    @Override
    protected String getResourceName() {
        return "channels";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
