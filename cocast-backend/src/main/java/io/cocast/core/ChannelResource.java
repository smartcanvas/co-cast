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
 * Resources for Channels (APIs)
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/core/v1/channels")
@Singleton
public class ChannelResource {

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
    public Response create(Channel channel, @PathParam("networkMnemonic") String networkMnemonic) {

        //validates the network mnemonic
        if (StringUtils.isEmpty(networkMnemonic) && StringUtils.isEmpty(channel.getNetworkMnemonic())) {
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
            logger.error(exc.getMessage());
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (CoCastCallException exc) {
            logger.error("Error creating channel", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (Exception exc) {
            logger.error("Error creating channel", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created("Channel created successfully with mnemonic: " + channel.getMnemonic()).getResponse();
    }

    /**
     * Get a list of channels
     */
    @GET
    @Path("/{networkMnemonic}")
    public Response list(@PathParam("networkMnemonic") String networkMnemonic) {

        List<Channel> channelList;

        try {
            channelList = channelRepository.list(networkMnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error listing channels", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error(exc.getMessage());
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error listing channels", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(channelList).build();
    }

    /**
     * Get a specific channel
     */
    @GET
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response get(@PathParam("networkMnemonic") String networkMnemonic,
                        @PathParam("mnemonic") String mnemonic) {

        Channel channel;

        try {
            channel = channelRepository.get(networkMnemonic, mnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error getting channel", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error(exc.getMessage());
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error getting channel", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(channel).build();
    }

    /**
     * Updates a channel
     */
    @PUT
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response update(Channel channel,
                           @PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("mnemonic") String mnemonic) {

        Channel response;

        if (StringUtils.isEmpty(mnemonic)) {
            return APIResponse.badRequest("Channel mnemonic is required as a path param").getResponse();
        } else {
            channel.setMnemonic(mnemonic);
        }

        try {
            response = channelRepository.update(channel, networkMnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error updating channel", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error updating channel", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error updating channel", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.updated("Channel updated. Mnemonic: " + response.getMnemonic()).getResponse();
    }

    /**
     * Delete a channel
     */
    @DELETE
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response delete(@PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("mnemonic") String mnemonic) {

        try {
            channelRepository.delete(networkMnemonic, mnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error getting channel", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error getting channel", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error getting channel", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.deleted("Channel deleted. Mnemonic: " + mnemonic).getResponse();
    }
}
