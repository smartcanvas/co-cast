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
 * Resources for settings (API)
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/core/v1/settings")
@Singleton
public class SettingsResource extends AbstractResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(SettingsResource.class);

    @Inject
    private SettingsRepository settingsRepository;

    /**
     * Creates a setting
     */
    @POST
    @Path("/{networkMnemonic}")
    public Response create(@Context HttpServletRequest request, Settings settings,
                           @PathParam("networkMnemonic") String networkMnemonic) {

        long initTime = System.currentTimeMillis();

        //validates the network mnemonic
        if (StringUtils.isEmpty(networkMnemonic) && StringUtils.isEmpty(settings.getNetworkMnemonic())) {
            logResult("Network mnemonic is required", request, "post", 0,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.badRequest("Network mnemonic is required").getResponse();
        }
        if (!StringUtils.isEmpty(networkMnemonic)) {
            settings.setNetworkMnemonic(networkMnemonic);
        }

        settings.setCreatedBy(SecurityContext.get().userIdentification());

        try {
            //calls the creation service
            settingsRepository.create(settings);
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
        return APIResponse.created("Settings created successfully. Name = " + settings.getName() +
                ", value = " + settings.getValue()).getResponse();
    }

    /**
     * Get a list of settings
     */
    @GET
    @Path("/{networkMnemonic}")
    public Response list(@Context HttpServletRequest request,
                         @PathParam("networkMnemonic") String networkMnemonic) {
        long initTime = System.currentTimeMillis();

        List<Settings> settingsList;

        try {
            settingsList = settingsRepository.list(networkMnemonic);
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

        logResult("OK", request, "get", settingsList.size(), HttpServletResponse.SC_OK, initTime);
        return Response.ok(settingsList).build();
    }

    /**
     * Get a specific settings
     */
    @GET
    @Path("/{networkMnemonic}/{name}")
    public Response get(@Context HttpServletRequest request,
                        @PathParam("networkMnemonic") String networkMnemonic,
                        @PathParam("name") String name) {
        long initTime = System.currentTimeMillis();

        Settings settings;

        try {
            settings = settingsRepository.get(networkMnemonic, name);
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
        return Response.ok(settings).build();
    }

    /**
     * Updates a settings
     */
    @PUT
    @Path("/{networkMnemonic}/{name}")
    public Response update(@Context HttpServletRequest request,
                           Settings settings,
                           @PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("name") String name) {
        long initTime = System.currentTimeMillis();

        Settings response;

        if (StringUtils.isEmpty(name)) {
            logResult("Settings mnemonic is required as a path param", request,
                    "put", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.badRequest("Settings mnemonic is required as a path param").getResponse();
        } else {
            settings.setName(name);
        }

        try {
            response = settingsRepository.update(settings, networkMnemonic);
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
        return APIResponse.updated("Settings updated. Name = " + response.getName() + ", value = "
                + response.getValue()).getResponse();
    }

    /**
     * Delete a settings
     */
    @DELETE
    @Path("/{networkMnemonic}/{name}")
    public Response delete(@Context HttpServletRequest request,
                           @PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("name") String name) {
        long initTime = System.currentTimeMillis();

        try {
            settingsRepository.delete(networkMnemonic, name);
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
        return APIResponse.deleted("Settings deleted. Name: " + name).getResponse();
    }

    @Override
    protected String getModuleName() {
        return "core";
    }

    @Override
    protected String getResourceName() {
        return "settings";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
