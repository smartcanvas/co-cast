package io.cocast.core;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.util.APIResponse;
import io.cocast.util.CoCastCallException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Station resources (API)
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/core/v1/stations")
@Singleton
public class StationResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(StationResource.class);

    @Inject
    private StationRepository stationRepository;

    /**
     * Creates a station
     */
    @POST
    @Path("/{networkMnemonic}")
    public Response create(Station station, @PathParam("networkMnemonic") String networkMnemonic) {

        //validates and defines the createdBy based on authorization token
        if ((station == null) ||
                (station.getName() == null)) {
            return APIResponse.badRequest("Station name is required").getResponse();
        }

        //validates the network mnemonic
        if (StringUtils.isEmpty(networkMnemonic) && StringUtils.isEmpty(station.getNetworkMnemonic())) {
            return APIResponse.badRequest("Network mnemonic is required").getResponse();
        }
        if (!StringUtils.isEmpty(networkMnemonic)) {
            station.setNetworkMnemonic(networkMnemonic);
        }

        station.setCreatedBy(SecurityContext.get().userIdentification());

        try {
            //calls the creation service
            stationRepository.create(station);
        } catch (ValidationException exc) {
            logger.error("Error creating station", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (CoCastCallException exc) {
            logger.error("Error creating station", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (Exception exc) {
            logger.error("Error creating station", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created("Station created successfully with mnemonic: " + station.getMnemonic()).getResponse();
    }

    /**
     * Get a list of stations
     */
    @GET
    @Path("/{networkMnemonic}")
    public Response list(@PathParam("networkMnemonic") String networkMnemonic) {

        List<Station> stationList;

        try {
            stationList = stationRepository.list(networkMnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error listing stations", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error listing stations", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error listing stations", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(stationList).build();
    }

    /**
     * Get a specific station
     */
    @GET
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response get(@PathParam("networkMnemonic") String networkMnemonic,
                        @PathParam("mnemonic") String mnemonic) {

        Station station;

        try {
            station = stationRepository.get(networkMnemonic, mnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error getting station", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error getting station", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error getting station", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(station).build();
    }

    /**
     * Updates a station
     */
    @PUT
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response update(Station station,
                           @PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("mnemonic") String mnemonic) {

        Station response;

        if (StringUtils.isEmpty(mnemonic)) {
            return APIResponse.badRequest("Station mnemonic is required as a path param").getResponse();
        } else {
            station.setMnemonic(mnemonic);
        }

        try {
            response = stationRepository.update(station, networkMnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error updating station", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error updating station", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error updating station", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.updated("Station updated. Mnemonic: " + response.getMnemonic()).getResponse();
    }

    /**
     * Delete a station
     */
    @DELETE
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response delete(@PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("mnemonic") String mnemonic) {

        try {
            stationRepository.delete(networkMnemonic, mnemonic);
        } catch (CoCastCallException exc) {
            logger.error("Error getting station", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error getting station", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error getting station", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.deleted("Station deleted. Mnemonic: " + mnemonic).getResponse();
    }

}
