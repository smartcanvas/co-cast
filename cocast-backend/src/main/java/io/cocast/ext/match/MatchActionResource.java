package io.cocast.ext.match;

import com.google.inject.Singleton;
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

/**
 * Resources for match actions
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/ext/match/v1/actions")
@Singleton
public class MatchActionResource extends AbstractResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(MatchActionResource.class);

    @Inject
    private MatchActionRepository matchActionRepository;

    /**
     * Creates an action
     */
    @POST
    @Path("/{networkMnemonic}")
    public MatchActionResponse create(@Context HttpServletRequest request,
                                      @PathParam("networkMnemonic") String networkMnemonic,
                                      @QueryParam("email") String email,
                                      @QueryParam("action") String action) {
        return this.save(request, networkMnemonic, email, action);
    }

    /**
     * Updates an action
     */
    @PUT
    @Path("/{networkMnemonic}")
    public MatchActionResponse update(@Context HttpServletRequest request,
                                      @PathParam("networkMnemonic") String networkMnemonic,
                                      @QueryParam("email") String email,
                                      @QueryParam("action") String action) {
        return this.save(request, networkMnemonic, email, action);
    }


    /**
     * Saves an action
     */
    private MatchActionResponse save(HttpServletRequest request, String networkMnemonic, String email, String action) {

        long initTime = System.currentTimeMillis();

        MatchActionResponse response = new MatchActionResponse();
        boolean match = false;

        try {
            match = matchActionRepository.save(networkMnemonic, email, action);
        } catch (ValidationException exc) {
            String message = exc.getMessage();
            response.setMessage(exc.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logResult(message, request, "post", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return response;
        } catch (CoCastCallException exc) {
            String message = exc.getMessage();
            response.setMessage(exc.getMessage());
            response.setStatus(exc.getStatus());
            logResult(message, request, "post", 0, exc.getStatus(), initTime);
            return response;
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "post", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            response.setMessage(exc.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return response;
        }

        response.setMatch(match);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setMessage("Action saved successfully");

        logResult("OK", request, "post", 0, HttpServletResponse.SC_OK, initTime);

        return response;
    }

    @Override
    protected String getModuleName() {
        return "match";
    }

    @Override
    protected String getResourceName() {
        return "matchActions";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
