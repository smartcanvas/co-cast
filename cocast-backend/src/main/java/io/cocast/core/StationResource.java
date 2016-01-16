package io.cocast.core;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.util.APIResponse;
import io.cocast.util.AbstractResource;
import io.cocast.util.CoCastCallException;
import io.cocast.util.log.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Station resources (API)
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/core/v1/stations")
@Singleton
public class StationResource extends AbstractResource {

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
    public Response create(@Context HttpServletRequest request,
                           Station station, @PathParam("networkMnemonic") String networkMnemonic) {

        long initTime = System.currentTimeMillis();

        //validates and defines the createdBy based on authorization token
        if ((station == null) ||
                (station.getName() == null)) {
            logResult("Station name is required", request, "post", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest("Station name is required").getResponse();
        }

        //validates the network mnemonic
        if (StringUtils.isEmpty(networkMnemonic) && StringUtils.isEmpty(station.getNetworkMnemonic())) {
            logResult("Network mnemonic is required", request, "post", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
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
            String message = exc.getMessage();
            logResult(message, request, "post", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (CoCastCallException exc) {
            String message = exc.getMessage();
            logResult(message, request, "post", 0, exc.getStatus(), initTime);
            return APIResponse.fromException(exc).getResponse();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "post", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "post", 0, HttpServletResponse.SC_CREATED, initTime);
        return APIResponse.created("Station created successfully with mnemonic: " + station.getMnemonic()).getResponse();
    }

    /**
     * Get a list of stations
     */
    @GET
    @Path("/{networkMnemonic}")
    public Response list(@Context HttpServletRequest request,
                         @PathParam("networkMnemonic") String networkMnemonic) {
        long initTime = System.currentTimeMillis();

        List<Station> stationList;

        try {
            stationList = stationRepository.list(networkMnemonic);
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

        logResult("OK", request, "get", stationList.size(), HttpServletResponse.SC_OK, initTime);
        return Response.ok(stationList).build();
    }

    /**
     * Get a specific station
     */
    @GET
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response get(@Context HttpServletRequest request,
                        @PathParam("networkMnemonic") String networkMnemonic,
                        @PathParam("mnemonic") String mnemonic) {
        long initTime = System.currentTimeMillis();

        Station station;

        try {
            station = stationRepository.get(networkMnemonic, mnemonic);
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

        logResult("OK", request, "get", 1, HttpServletResponse.SC_OK, initTime);
        return Response.ok(station).build();
    }

    /**
     * Updates a station
     */
    @PUT
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response update(@Context HttpServletRequest request,
                           Station station,
                           @PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("mnemonic") String mnemonic) {
        long initTime = System.currentTimeMillis();

        Station response;

        if (StringUtils.isEmpty(mnemonic)) {
            logResult("Station mnemonic is required as a path param", request,
                    "put", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest("Station mnemonic is required as a path param").getResponse();
        } else {
            station.setMnemonic(mnemonic);
        }

        try {
            response = stationRepository.update(station, networkMnemonic);
        } catch (CoCastCallException exc) {
            String message = exc.getMessage();
            logResult(message, request, "put", 0, exc.getStatus(), initTime);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            String message = exc.getMessage();
            logResult(message, request, "put", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "put", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "put", 0, HttpServletResponse.SC_OK, initTime);

        return APIResponse.updated("Station updated. Mnemonic: " + response.getMnemonic()).getResponse();
    }

    /**
     * Delete a station
     */
    @DELETE
    @Path("/{networkMnemonic}/{mnemonic}")
    public Response delete(@Context HttpServletRequest request,
                           @PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("mnemonic") String mnemonic) {
        long initTime = System.currentTimeMillis();

        try {
            stationRepository.delete(networkMnemonic, mnemonic);
        } catch (CoCastCallException exc) {
            String message = exc.getMessage();
            logResult(message, request, "delete", 0, exc.getStatus(), initTime);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            String message = exc.getMessage();
            logResult(message, request, "delete", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "delete", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "delete", 0, HttpServletResponse.SC_NO_CONTENT, initTime);
        return APIResponse.deleted("Station deleted. Mnemonic: " + mnemonic).getResponse();
    }

    @Override
    protected String getModuleName() {
        return "core";
    }

    @Override
    protected String getResourceName() {
        return "stations";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
