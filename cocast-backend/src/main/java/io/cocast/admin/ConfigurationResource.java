package io.cocast.admin;

import io.cocast.util.APIResponse;
import io.cocast.util.AbstractResource;
import io.cocast.util.log.LogUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Configuration APIs
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/admin/v1/configurations")
public class ConfigurationResource extends AbstractResource {

    private static Logger logger = LogManager.getLogger(ConfigurationResource.class.getName());

    @Inject
    private ConfigurationRepository configurationRepository;

    @POST
    public Response create(@Context HttpServletRequest request, Configuration configuration) {
        long initTime = System.currentTimeMillis();

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
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "post", 0,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "post", 0, HttpServletResponse.SC_CREATED, initTime);
        return APIResponse.created(configuration.getName() + " = " + configuration.getValue()).getResponse();
    }

    @GET
    public Response list(@Context HttpServletRequest request) {
        long initTime = System.currentTimeMillis();
        List<Configuration> result;

        try {
            //calls the service
            result = configurationRepository.list();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "get", 0,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "get", result.size(), HttpServletResponse.SC_OK, initTime);
        return Response.ok(result).build();
    }

    @GET
    @Path("/{configKey}")
    public Response get(@Context HttpServletRequest request, @PathParam("configKey") String configKey) {
        long initTime = System.currentTimeMillis();
        Configuration result;

        try {
            //calls the service
            result = configurationRepository.get(configKey);
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "get", 0,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "get", 1, HttpServletResponse.SC_OK, initTime);
        return Response.ok(result).build();
    }

    @POST
    @Path("/clean")
    public Response cleanUpCache(@Context HttpServletRequest request) {
        long initTime = System.currentTimeMillis();

        try {
            //calls the clean service
            configurationRepository.cleanUpCache();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "post", 0,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "post", 0, HttpServletResponse.SC_OK, initTime);
        return Response.ok().build();
    }

    @Override
    protected String getModuleName() {
        return "admin";
    }

    @Override
    protected String getResourceName() {
        return "configurations";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
