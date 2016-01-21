package io.cocast.core;

import io.cocast.auth.SecurityContext;
import io.cocast.util.APIResponse;
import io.cocast.util.AbstractResource;
import io.cocast.util.CoCastCallException;
import io.cocast.util.log.LogUtils;
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
 * Content resources (API)
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/core/v1/contents")
public class ContentResource extends AbstractResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(ContentResource.class);

    @Inject
    private ContentRepository contentRepository;

    @Inject
    private ContentServices contentServices;

    /**
     * Creates a content
     */
    @POST
    @Path("/{networkMnemonic}")
    public Response create(@Context HttpServletRequest request, Content content, @PathParam("networkMnemonic") String networkMnemonic) {
        return this.save(request, content, networkMnemonic);
    }

    /**
     * Updates the content
     */
    @PUT
    @Path("/{networkMnemonic}")
    public Response update(@Context HttpServletRequest request, Content content, @PathParam("networkMnemonic") String networkMnemonic) {
        return this.save(request, content, networkMnemonic);
    }

    /**
     * Creates a content
     */
    @POST
    @Path("/bulk/{networkMnemonic}")
    public Response createBulk(@Context HttpServletRequest request, List<Content> contents, @PathParam("networkMnemonic") String networkMnemonic) {
        return this.bulkSave(request, contents, networkMnemonic);
    }

    /**
     * Updates the content
     */
    @PUT
    @Path("/bulk/{networkMnemonic}")
    public Response updateBulk(@Context HttpServletRequest request, List<Content> contents, @PathParam("networkMnemonic") String networkMnemonic) {
        return this.bulkSave(request, contents, networkMnemonic);
    }

    /**
     * Returns a specific content
     */
    @GET
    @Path("/{networkMnemonic}/{id}")
    public Response get(@Context HttpServletRequest request,
                        @PathParam("networkMnemonic") String networkMnemonic,
                        @PathParam("id") String id) {

        long initTime = System.currentTimeMillis();

        Content content;

        try {
            content = contentRepository.get(networkMnemonic, id);
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
        return Response.ok(content).build();
    }

    /**
     * Save a content
     */
    private Response save(HttpServletRequest request, Content content, String networkMnemonic) {
        long initTime = System.currentTimeMillis();


        content.setNetworkMnemonic(networkMnemonic);

        if ((SecurityContext.get() != null) && (SecurityContext.get().userIdentification() != null)) {
            content.setCreatedBy(SecurityContext.get().userIdentification());
        }

        try {
            contentServices.save(content);
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
        return APIResponse.created("Content saved successfully with ID: " + content.getId()).getResponse();
    }

    /**
     * Bulk saves a content
     */
    private Response bulkSave(HttpServletRequest request, List<Content> contents, String networkMnemonic) {
        long initTime = System.currentTimeMillis();


        for (Content content : contents) {
            content.setNetworkMnemonic(networkMnemonic);
            if ((SecurityContext.get() != null) && (SecurityContext.get().userIdentification() != null)) {
                content.setCreatedBy(SecurityContext.get().userIdentification());
            }
        }

        try {
            contentServices.bulkSave(networkMnemonic, contents);
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

        logResult("OK", request, "post", contents.size(), HttpServletResponse.SC_CREATED, initTime);
        return APIResponse.created(contents.size() + " contents saved successfully").getResponse();
    }


    @Override
    protected String getModuleName() {
        return "core";
    }

    @Override
    protected String getResourceName() {
        return "contents";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
