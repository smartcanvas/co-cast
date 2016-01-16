package io.cocast.ext.people;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.util.APIResponse;
import io.cocast.util.AbstractResource;
import io.cocast.util.CoCastCallException;
import io.cocast.util.PaginatedResponse;
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

/**
 * Person resources (API)
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/ext/people/v1/persons")
@Singleton
public class PersonResource extends AbstractResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(PersonResource.class);

    @Inject
    private PersonRepository personRepository;

    /**
     * Creates a person
     */
    @POST
    @Path("/{networkMnemonic}")
    public Response create(@Context HttpServletRequest request,
                           Person person, @PathParam("networkMnemonic") String networkMnemonic) {
        long initTime = System.currentTimeMillis();

        //validates the ID
        if ((person == null) ||
                (person.getId() == null)) {
            logResult("Person ID is required", request, "post", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest("Person ID is required").getResponse();
        }

        //validates the network mnemonic
        if (StringUtils.isEmpty(networkMnemonic) && StringUtils.isEmpty(person.getNetworkMnemonic())) {
            logResult("Network mnemonic is required", request, "post", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest("Network mnemonic is required").getResponse();
        }
        if (!StringUtils.isEmpty(networkMnemonic)) {
            person.setNetworkMnemonic(networkMnemonic);
        }

        if ((SecurityContext.get() != null) && (SecurityContext.get().userIdentification() != null)) {
            person.setCreatedBy(SecurityContext.get().userIdentification());
        }

        try {
            personRepository.create(person);
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
        return APIResponse.created("Person created successfully with ID: " + person.getId()).getResponse();
    }

    /**
     * Updates the person
     */
    @PUT
    @Path("/{networkMnemonic}")
    public Response update(@Context HttpServletRequest request,
                           Person person, @PathParam("networkMnemonic") String networkMnemonic) {
        long initTime = System.currentTimeMillis();


        //validates the ID
        if ((person == null) ||
                (person.getId() == null)) {
            logResult("Person ID is required", request, "put", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest("Person ID is required").getResponse();
        }

        //validates the network mnemonic
        if (StringUtils.isEmpty(networkMnemonic) && StringUtils.isEmpty(person.getNetworkMnemonic())) {
            logResult("Network mnemonic is required", request, "put", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest("Network mnemonic is required").getResponse();
        }
        if (!StringUtils.isEmpty(networkMnemonic)) {
            person.setNetworkMnemonic(networkMnemonic);
        }

        if ((SecurityContext.get() != null) && (SecurityContext.get().userIdentification() != null)) {
            person.setCreatedBy(SecurityContext.get().userIdentification());
        }

        try {
            personRepository.update(person);
        } catch (ValidationException exc) {
            String message = exc.getMessage();
            logResult(message, request, "put", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (CoCastCallException exc) {
            String message = exc.getMessage();
            logResult(message, request, "put", 0, exc.getStatus(), initTime);
            return APIResponse.fromException(exc).getResponse();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "put", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "put", 0, HttpServletResponse.SC_OK, initTime);
        return APIResponse.created("Person updated successfully with ID: " + person.getId()).getResponse();
    }

    /**
     * Returns a specific person
     */
    @GET
    @Path("/{networkMnemonic}/{id}")
    public Response get(@Context HttpServletRequest request,
                        @PathParam("networkMnemonic") String networkMnemonic,
                        @PathParam("id") String id) {
        long initTime = System.currentTimeMillis();

        Person person;

        try {
            person = personRepository.get(networkMnemonic, id);
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
        return Response.ok(person).build();
    }

    /**
     * Get a list of persons
     */
    @GET
    @Path("/{networkMnemonic}")
    public Response list(@Context HttpServletRequest request,
                         @PathParam("networkMnemonic") String networkMnemonic,
                         @QueryParam("email") String email,
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
            response = personRepository.list(networkMnemonic, email, limit, offset);
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

    /**
     * Delete a person
     */
    @DELETE
    @Path("/{networkMnemonic}/{id}")
    public Response delete(@Context HttpServletRequest request,
                           @PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("id") String id) {
        long initTime = System.currentTimeMillis();

        try {
            personRepository.delete(networkMnemonic, id);
        } catch (CoCastCallException exc) {
            String message = exc.getMessage();
            logResult("OK", request, "delete", 0, exc.getStatus(), initTime);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            String message = exc.getMessage();
            logResult("OK", request, "delete", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "delete", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "delete", 0, HttpServletResponse.SC_NO_CONTENT, initTime);
        return APIResponse.deleted("Person deleted. ID: " + id).getResponse();
    }

    @Override
    protected String getModuleName() {
        return "people";
    }

    @Override
    protected String getResourceName() {
        return "persons";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
