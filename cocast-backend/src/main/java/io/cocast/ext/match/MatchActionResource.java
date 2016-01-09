package io.cocast.ext.match;

import com.google.inject.Singleton;
import io.cocast.util.CoCastCallException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import javax.ws.rs.*;

/**
 * Resources for match actions
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/ext/match/v1/actions")
@Singleton
public class MatchActionResource {

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
    public MatchActionResponse create(@PathParam("networkMnemonic") String networkMnemonic,
                                      @QueryParam("email") String email,
                                      @QueryParam("action") String action) {
        return this.save(networkMnemonic, email, action);
    }

    /**
     * Updates an action
     */
    @PUT
    @Path("/{networkMnemonic}")
    public MatchActionResponse update(@PathParam("networkMnemonic") String networkMnemonic,
                                      @QueryParam("email") String email,
                                      @QueryParam("action") String action) {
        return this.save(networkMnemonic, email, action);
    }


    /**
     * Saves an action
     */
    private MatchActionResponse save(String networkMnemonic, String email, String action) {

        MatchActionResponse response = new MatchActionResponse();
        boolean match = false;

        try {
            match = matchActionRepository.save(networkMnemonic, email, action);
        } catch (ValidationException exc) {
            logger.error("Error saving action", exc);
            response.setMessage(exc.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return response;
        } catch (CoCastCallException exc) {
            logger.error("Error saving action", exc);
            response.setMessage(exc.getMessage());
            response.setStatus(exc.getStatus());
            return response;
        } catch (Exception exc) {
            logger.error("Error saving action", exc);
            response.setMessage(exc.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return response;
        }

        response.setMatch(match);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setMessage("Action saved successfully");

        return response;
    }

}
