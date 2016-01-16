package io.cocast.ext.match;

import com.google.inject.Singleton;
import io.cocast.util.APIResponse;
import io.cocast.util.AbstractResource;
import io.cocast.util.CoCastCallException;
import io.cocast.util.PaginatedResponse;
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
 * Resource for shuffling and delivering persons
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/ext/match/v1/shuffle")
@Singleton
public class ShuffleResource extends AbstractResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(ShuffleResource.class);

    @Inject
    private ShuffleServices shuffleServices;

    /**
     * Get a list of shuffled persons for matches
     */
    @GET
    @Path("/{networkMnemonic}")
    public Response list(@Context HttpServletRequest request,
                         @PathParam("networkMnemonic") String networkMnemonic,
                         @QueryParam("limit") Integer limit,
                         @QueryParam("offset") Integer offset) {

        long initTime = System.currentTimeMillis();

        if (limit == null) {
            limit = 10;
        }
        if (offset == null) {
            offset = 0;
        }

        PaginatedResponse response;

        try {
            response = shuffleServices.shuffle(networkMnemonic, limit, offset);
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

        logResult("OK", request, "get", response.getData().size(), HttpServletResponse.SC_OK, initTime);
        return Response.ok(response).build();
    }

    @Override
    protected String getModuleName() {
        return "match";
    }

    @Override
    protected String getResourceName() {
        return "shuffle";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
