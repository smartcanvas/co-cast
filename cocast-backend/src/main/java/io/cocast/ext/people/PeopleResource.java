package io.cocast.ext.people;

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

/**
 * People resources (API)
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/ext/v1/people")
@Singleton
public class PeopleResource {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(PeopleResource.class);

    @Inject
    private PersonRepository personRepository;

    /**
     * Creates a person
     */
    @POST
    @Path("/{networkMnemonic}")
    public Response create(Person person, @PathParam("networkMnemonic") String networkMnemonic) {
        return this.save(person, networkMnemonic);
    }

    /**
     * Updates the person
     */
    @PUT
    @Path("/{networkMnemonic}")
    public Response update(Person person, @PathParam("networkMnemonic") String networkMnemonic) {
        return this.save(person, networkMnemonic);
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
     * Save a Person
     */
    private Response save(Person person, String networkMnemonic) {
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
            personRepository.save(person);
        } catch (ValidationException exc) {
            logger.error("Error saving person", exc);
            return APIResponse.badRequest(exc.getMessage()).getResponse();
        } catch (CoCastCallException exc) {
            logger.error("Error saving person", exc);
            return APIResponse.fromException(exc).getResponse();
        } catch (Exception exc) {
            logger.error("Error saving person", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created("Person saved successfully with ID: " + person.getId()).getResponse();
    }
}
