package io.cocast.ext.match;

import com.google.inject.Singleton;
import io.cocast.util.APIResponse;
import io.cocast.util.CoCastCallException;
import io.cocast.util.PaginatedResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Resource for shuffling and delivering persons
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/ext/match/v1/shuffle")
@Singleton
public class ShuffleResource {

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
    public Response list(@PathParam("networkMnemonic") String networkMnemonic,
                         @QueryParam("limit") Integer limit,
                         @QueryParam("offset") Integer offset) {

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
            logger.error("Error shuffling persons", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error shuffling persons", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error shuffling persons", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(response).build();
    }
}
