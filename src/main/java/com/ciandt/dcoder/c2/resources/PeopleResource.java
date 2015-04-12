package com.ciandt.dcoder.c2.resources;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ciandt.dcoder.c2.util.ConfigurationUtils;
import com.ciandt.dcoder.c2.util.Constants;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Servlet to create Person and Profile entities inside Smart Canvas
 * 
 * @author Daniel Viveiros
 */
@Singleton
@Path("/people")
public class PeopleResource {
	
	@Inject
	private Logger logger;
	
    private static ConfigurationUtils configurationServices = ConfigurationUtils .getInstance();
	
	/**
	 * Ingest people into Smart Canvas
	 */
    @GET
    @Path("/ingest")
	public Response ingestPeople() {
		
		long initTime = System.currentTimeMillis();
		logger.info( "Executing people ingestion" );
	
		//gets the publishers
        List<String> publisherIds = getPublishersIds();
        if ( publisherIds != null ) {
            logger.info("We found " + publisherIds.size() + " publisher(s)");
            for (String publisherId: publisherIds) {
		
				//put the date in a task queue
				Queue queue = QueueFactory.getQueue(Constants.PERSON_QUEUE);
				queue.add(withUrl("/tasks/personingestion/inbound")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN).method(TaskOptions.Method.POST)
				        .payload(publisherId));
            }
        }
		
		long endTime = System.currentTimeMillis();
		logger.info( "Process finalized in " + (endTime - initTime) + " msecs");
		
		return Response.ok().build();
	}
	
	/**
     * Get the user id for all the publishers
     */
    private List<String> getPublishersIds() {
        List<String> result = configurationServices.getValues("publisher_ids");
        return result;
    }

}
