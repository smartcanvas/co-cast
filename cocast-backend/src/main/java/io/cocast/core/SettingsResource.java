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
 * Resources for settings (API)
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/core/v1/settings")
@Singleton
public class SettingsResource {

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
    public Response create(Settings settings, @PathParam("networkMnemonic") String networkMnemonic) {

        //validates the network mnemonic
        if (StringUtils.isEmpty(networkMnemonic) && StringUtils.isEmpty(settings.getNetworkMnemonic())) {
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
            logger.error("Error creating settings", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (CoCastCallException exc) {
            logger.error("Error creating settings", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (Exception exc) {
            logger.error("Error creating settings", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created("Settings created successfully. Name = " + settings.getName() +
                ", value = " + settings.getValue()).getResponse();
    }

    /**
     * Get a list of settings
     */
    @GET
    @Path("/{networkMnemonic}")
    public Response list(@PathParam("networkMnemonic") String networkMnemonic) {

        List<Settings> settingsList;

        try {
            settingsList = settingsRepository.list(networkMnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error listing settings", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error listing settings", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error listing settings", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(settingsList).build();
    }

    /**
     * Get a specific settings
     */
    @GET
    @Path("/{networkMnemonic}/{name}")
    public Response get(@PathParam("networkMnemonic") String networkMnemonic,
                        @PathParam("name") String name) {

        Settings settings;

        try {
            settings = settingsRepository.get(networkMnemonic, name);
        } catch (CoCastCallException exc) {
            logger.error("Error getting settings", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error getting settings", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error getting settings", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(settings).build();
    }

    /**
     * Updates a settings
     */
    @PUT
    @Path("/{networkMnemonic}/{name}")
    public Response update(Settings settings,
                           @PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("name") String name) {

        Settings response;

        if (StringUtils.isEmpty(name)) {
            return APIResponse.badRequest("Settings mnemonic is required as a path param").getResponse();
        } else {
            settings.setName(name);
        }

        try {
            response = settingsRepository.update(settings, networkMnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error updating settings", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error updating settings", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error updating settings", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.updated("Settings updated. Name = " + response.getName() + ", value = "
                + response.getValue()).getResponse();
    }

    /**
     * Delete a settings
     */
    @DELETE
    @Path("/{networkMnemonic}/{name}")
    public Response delete(@PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("name") String name) {

        try {
            settingsRepository.delete(networkMnemonic, name);
        } catch (CoCastCallException exc) {
            logger.error("Error deleting settings", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error deleting settings", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error deleting settings", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.deleted("Settings deleted. Name: " + name).getResponse();
    }
}
