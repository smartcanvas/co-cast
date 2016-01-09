package io.cocast.ext.people;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.util.APIResponse;
import io.cocast.util.CoCastCallException;
import io.cocast.util.PaginatedResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Person resources (API)
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/ext/people/v1/persons")
@Singleton
public class PersonResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(PersonResource.class);

    @Inject
    private PersonRepository personRepository;

    @Inject
    private PersonServices personServices;

    /**
     * Creates a person
     */
    @POST
    @Path("/{networkMnemonic}")
    public Response create(Person person, @PathParam("networkMnemonic") String networkMnemonic) {
        //validates the ID
        if ((person == null) ||
                (person.getId() == null)) {
            return APIResponse.badRequest("Person ID is required").getResponse();
        }

        //validates the network mnemonic
        if (StringUtils.isEmpty(networkMnemonic) && StringUtils.isEmpty(person.getNetworkMnemonic())) {
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
            logger.error("Error creating person", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (CoCastCallException exc) {
            logger.error("Error creating person", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (Exception exc) {
            logger.error("Error creating person", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created("Person created successfully with ID: " + person.getId()).getResponse();
    }

    /**
     * Updates the person
     */
    @PUT
    @Path("/{networkMnemonic}")
    public Response update(Person person, @PathParam("networkMnemonic") String networkMnemonic) {
        //validates the ID
        if ((person == null) ||
                (person.getId() == null)) {
            return APIResponse.badRequest("Person ID is required").getResponse();
        }

        //validates the network mnemonic
        if (StringUtils.isEmpty(networkMnemonic) && StringUtils.isEmpty(person.getNetworkMnemonic())) {
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
            logger.error("Error updating person", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (CoCastCallException exc) {
            logger.error("Error updating person", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (Exception exc) {
            logger.error("Error updating person", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created("Person updated successfully with ID: " + person.getId()).getResponse();
    }

    /**
     * Returns a specific person
     */
    @GET
    @Path("/{networkMnemonic}/{id}")
    public Response get(@PathParam("networkMnemonic") String networkMnemonic,
                        @PathParam("id") String id) {

        Person person;

        try {
            person = personRepository.get(networkMnemonic, id);
        } catch (CoCastCallException exc) {
            logger.error("Error retriving person", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error retriving person", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error retriving person", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(person).build();
    }

    /**
     * Get a list of persons
     */
    @GET
    @Path("/{networkMnemonic}")
    public Response list(@PathParam("networkMnemonic") String networkMnemonic,
                         @QueryParam("email") String email,
                         @QueryParam("limit") Integer limit,
                         @QueryParam("offset") Integer offset) {

        if (limit == null) {
            limit = 10;
        }
        if (offset == null) {
            offset = 0;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Persons.list(): email = " + email + ", limit = " + limit + ", offset = " + offset);
        }

        PaginatedResponse response;

        try {
            response = personRepository.list(networkMnemonic, email, limit, offset);
        } catch (CoCastCallException exc) {
            logger.error("Error listing persons", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error listing persons", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error listing persons", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(response).build();
    }

    /**
     * Get a list of persons shuffled
     */
    @GET
    @Path("/{networkMnemonic}/shuffle")
    public Response shuffle(@PathParam("networkMnemonic") String networkMnemonic,
                            @QueryParam("email") String email,
                            @QueryParam("limit") Integer limit,
                            @QueryParam("offset") Integer offset) {

        if (limit == null) {
            limit = 10;
        }
        if (offset == null) {
            offset = 0;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Persons.list(): email = " + email + ", limit = " + limit + ", offset = " + offset);
        }

        PaginatedResponse response;

        try {
            response = personServices.shuffle(networkMnemonic, email, limit, offset);
        } catch (CoCastCallException exc) {
            logger.error("Error listing persons", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error listing persons", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error listing persons", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(response).build();
    }

    /**
     * Delete a station
     */
    @DELETE
    @Path("/{networkMnemonic}/{id}")
    public Response delete(@PathParam("networkMnemonic") String networkMnemonic,
                           @PathParam("id") String id) {

        try {
            personRepository.delete(networkMnemonic, id);
        } catch (CoCastCallException exc) {
            logger.error("Error deleting person", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (ValidationException exc) {
            logger.error("Error deleting person", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (Exception exc) {
            logger.error("Error deleting person", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.deleted("Person deleted. ID: " + id).getResponse();
    }


}
