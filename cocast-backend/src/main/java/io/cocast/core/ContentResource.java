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

/**
 * Content resources (API)
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/core/v1/contents")
@Singleton
public class ContentResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(ContentResource.class);

    @Inject
    private ContentRepository contentRepository;

    /**
     * Creates a content
     */
    @POST
    @Path("/{networkMnemonic}")
    public Response create(Content content, @PathParam("networkMnemonic") String networkMnemonic) {
        return this.save(content, networkMnemonic);
    }

    /**
     * Updates the content
     */
    @PUT
    @Path("/{networkMnemonic}")
    public Response update(Content content, @PathParam("networkMnemonic") String networkMnemonic) {
        return this.save(content, networkMnemonic);
    }

    /**
     * Returns a specific content
     */
    @GET
    @Path("/{networkMnemonic}/{id}")
    public Response get(@PathParam("networkMnemonic") String networkMnemonic,
                        @PathParam("id") String id) {

        Content content;

        try {
            content = contentRepository.get(networkMnemonic, id);
        } catch (CoCastCallException exc) {
            logger.error("Error retriving content", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error retriving content", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error retriving content", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(content).build();
    }

    /**
     * Save a content
     */
    private Response save(Content content, String networkMnemonic) {
        //validates the ID
        if ((content == null) ||
                (content.getId() == null)) {
            return APIResponse.badRequest("Content ID is required").getResponse();
        }

        //validates the network mnemonic
        if (StringUtils.isEmpty(networkMnemonic) && StringUtils.isEmpty(content.getNetworkMnemonic())) {
            return APIResponse.badRequest("Network mnemonic is required").getResponse();
        }
        if (!StringUtils.isEmpty(networkMnemonic)) {
            content.setNetworkMnemonic(networkMnemonic);
        }

        if ((SecurityContext.get() != null) && (SecurityContext.get().userIdentification() != null)) {
            content.setCreatedBy(SecurityContext.get().userIdentification());
        }

        try {
            contentRepository.save(content);
        } catch (ValidationException exc) {
            logger.error("Error saving content", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (CoCastCallException exc) {
            logger.error("Error saving content", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (Exception exc) {
            logger.error("Error saving content", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created("Content saved successfully with ID: " + content.getId()).getResponse();
    }
}
