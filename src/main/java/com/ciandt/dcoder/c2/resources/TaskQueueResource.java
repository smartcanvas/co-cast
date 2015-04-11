package com.ciandt.dcoder.c2.resources;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.ciandt.dcoder.c2.service.GooglePlusConnector;
import com.ciandt.dcoder.c2.service.PeopleServices;
import com.ciandt.dcoder.c2.util.MailUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Exposed web service API for task queue operations.
 * 
 * @author Daniel Viveiros
 */
@Singleton
@Path("/tasks")
public class TaskQueueResource {

    @Inject
    private Logger logger;
    
    @Inject
	private GooglePlusConnector connector;
    
    @Inject
	private PeopleServices peopleServices;

    @POST
    @Path("/contentingestion/inbound")
    public Response ingestContent(final String publisherId) {
        logger.info("[TaskQueueResource] Content ingestion initiated. Publisher ID = " + publisherId);
        Long init = System.currentTimeMillis();
        
        try {
			connector.execute(publisherId);
		} catch (Exception exc) {
            String message = "Error ingesting content for publisher: " + publisherId;
            logger.log(Level.SEVERE, message, exc);
            try {
                MailUtils.sendErrorStatus(message, ExceptionUtils.getFullStackTrace(exc));
            } catch (Exception exc2) {
                logger.log(Level.SEVERE, "Error sending email with message: " + message, exc2);
            }
        }

        Long end = System.currentTimeMillis();
        logger.info("Content ingestion finalized for publisher = " + publisherId + ". Time taken = " + (end - init) + " msecs");

        return Response.ok().build();
    }
    
    @POST
    @Path("/personingestion/inbound")
    public Response ingestPeople(final String userId) {
        logger.info("[TaskQueueResource] Person ingestion initiated. Publisher ID = " + userId);
        Long init = System.currentTimeMillis();
        
        try {
			peopleServices.ingestPerson( userId );
		} catch (Exception exc) {
            String message = "Error ingesting person for userId: " + userId;
            logger.log(Level.SEVERE, message, exc);
            try {
                MailUtils.sendErrorStatus(message, ExceptionUtils.getFullStackTrace(exc));
            } catch (Exception exc2) {
                logger.log(Level.SEVERE, "Error sending email with message: " + message, exc2);
            }
        }

        Long end = System.currentTimeMillis();
        logger.info("Person ingestion finalized for user = " + userId + ". Time taken = " + (end - init) + " msecs");

        return Response.ok().build();
    }
}
