package io.cocast.configuration;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.util.APIResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Configuration APIs
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/admin/v1/configurations")
@Singleton
public class ConfigurationResource {

    private static Logger logger = LogManager.getLogger(ConfigurationResource.class.getName());

    @Inject
    private ConfigurationServices configurationServices;

    @POST
    public Response createTeam(Configuration configuration) {

        //validates and defines the createdBy based on authorization token
        if ((configuration == null) ||
                (configuration.getName() == null) ||
                (configuration.getValue() == null)) {
            return APIResponse.badRequest("Configuration name and value are required").getResponse();
        }
        configuration.setCreatedBy(SecurityContext.get().userIdentification());

        try {
            //calls the creation service
            configurationServices.create(configuration);
        } catch (Exception exc) {
            logger.error("Error creating configuration", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created(configuration.getName() + " = " + configuration.getValue()).getResponse();
    }

    @GET
    public Response listConfiguration() {
        List<Configuration> result;

        try {
            //calls the creation service
            result = configurationServices.list();
        } catch (Exception exc) {
            logger.error("Error listing configuration", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(result).build();
    }

    @GET
    @Path("/{configKey}")
    public Response getConfiguration(@PathParam("configKey") String configKey) {
        Configuration result;

        try {
            //calls the creation service
            result = configurationServices.get(configKey);
        } catch (Exception exc) {
            logger.error("Error getting configuration with config key = " + configKey, exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(result).build();
    }

    @POST
    @Path("/clean")
    public Response cleanUpConfigurationCache() {

        try {
            //calls the creation service
            configurationServices.cleanUpCache();
        } catch (Exception exc) {
            logger.error("Error cleaning up configuration cache", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok().build();
    }


}
