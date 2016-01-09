package io.cocast.ext.match;

import com.google.inject.Singleton;
import io.cocast.util.APIResponse;
import io.cocast.util.CoCastCallException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Resource for matches
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/ext/match/v1/matches")
@Singleton
public class MatchResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(MatchResource.class);

    @Inject
    private MatchRepository matchRepository;

    /**
     * Get a list of my matches
     */
    @GET
    @Path("/{networkMnemonic}")
    public Response list(@PathParam("networkMnemonic") String networkMnemonic) {

        List<Match> matchList;

        try {
            matchList = matchRepository.list(networkMnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error listing matches", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error listing matches", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error listing matches", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(matchList).build();
    }
}
