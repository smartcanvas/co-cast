package io.cocast.core;

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

/**
 * Resource for live events
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/core/v1/live")
public class LiveStreamResource extends AbstractResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(LiveStreamResource.class);

    @Inject
    private LiveStreamServices liveStreamServices;

    /**
     * Returns a specific content
     */
    @GET
    @Path("/{networkMnemonic}/{stationMnemonic}")
    public Response getLiveContents(@Context HttpServletRequest request,
                                    @PathParam("networkMnemonic") String networkMnemonic,
                                    @PathParam("stationMnemonic") String stationMnemonic) {

        long initTime = System.currentTimeMillis();

        LiveStream liveStream;

        try {
            liveStream = liveStreamServices.getStream(networkMnemonic, stationMnemonic);
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

        logResult("OK", request, "get", liveStream.getContents().size(), HttpServletResponse.SC_OK, initTime);
        return Response.ok(liveStream).build();
    }

    @Override
    protected String getModuleName() {
        return "core";
    }

    @Override
    protected String getResourceName() {
        return "live";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
