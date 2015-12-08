package io.cocast.admin;

import com.google.inject.Singleton;
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
    private ConfigurationRepository configurationRepository;

    @POST
    public Response create(Configuration configuration) {

        //validate the parameter
        if ((configuration == null) ||
                (configuration.getName() == null) ||
                (configuration.getValue() == null)) {
            return APIResponse.badRequest("Configuration name and value are required").getResponse();
        }

        try {
            //calls the creation service
            configurationRepository.create(configuration);
        } catch (Exception exc) {
            logger.error("Error creating configuration", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created(configuration.getName() + " = " + configuration.getValue()).getResponse();
    }

    @GET
    public Response list() {
        List<Configuration> result;

        try {
            //calls the service
            result = configurationRepository.list();
        } catch (Exception exc) {
            logger.error("Error listing configuration", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(result).build();
    }

    @GET
    @Path("/{configKey}")
    public Response get(@PathParam("configKey") String configKey) {
        Configuration result;

        try {
            //calls the service
            result = configurationRepository.get(configKey);
        } catch (Exception exc) {
            logger.error("Error getting configuration with config key = " + configKey, exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(result).build();
    }

    @POST
    @Path("/clean")
    public Response cleanUpCache() {

        try {
            //calls the clean service
            configurationRepository.cleanUpCache();
        } catch (Exception exc) {
            logger.error("Error cleaning up configuration cache", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok().build();
    }


}
