package io.cocast.configuration;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.util.APIResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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


}
