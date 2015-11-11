package com.ciandt.d1.cocast.content.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ciandt.d1.cocast.castview.CastViewServices;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

/**
 * APIs for populating Co-cast with content coming from Smart Canvas
 * 
 * @author Daniel Viveiros
 */
@Singleton
@Path("/api/contents")
public class ContentResource {
	
	@Inject
	private Logger logger;
	
	@Inject
	private CastViewServices castServices;
	
	/**
	 * Read content from Smart Canvas and populates the cache
	 */
	@GET
	@Path("/fetch")
	public Response fetchContent() {
		try {

			//put the date in a task queue
			Queue queue = QueueFactory.getQueue( "fetch-content" );
			queue.add(withUrl("/api/tasks/fetch/inbound").method(TaskOptions.Method.POST));
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error fetching content", exc);
			return Response.serverError().build();
		}
		
		return Response.ok().build();
	}
	
	/**
	 * Read content from Smart Canvas and populates the cache
	 */
	@GET
	@Path("/reload")
	public Response loadContent() {
		try {
			castServices.reload();
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error fetching content", exc);
			return Response.serverError().build();
		}
		
		return Response.ok().build();
	}

}
