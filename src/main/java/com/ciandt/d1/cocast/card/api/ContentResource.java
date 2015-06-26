package com.ciandt.d1.cocast.card.api;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ciandt.d1.cocast.castview.CastViewServices;
import com.ciandt.d1.cocast.configuration.ConfigurationUtils;
import com.ciandt.d1.cocast.util.Constants;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Servlet to ingest content coming from google plus inside Smart Canvas
 * 
 * @author Daniel Viveiros
 */
@Singleton
@Path("/content")
public class ContentResource {
	
	@Inject
	private Logger logger;
	
	@Inject
	private CastViewServices castServices;
	
	private static ConfigurationUtils configurationServices = ConfigurationUtils.getInstance();

	
	/**
	 * Read content from Smart Canvas and populates the cache
	 */
	@GET
	@Path("/fetch")
	public Response fetchContent() {
		try {
			castServices.fetchContent();
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error fetching content", exc);
		}
		
		return Response.ok().build();
	}
	
	/**
	 * Read content from Smart Canvas and populates the cache
	 */
	@GET
	@Path("/load")
	public Response loadContent() {
		try {
			castServices.loadContent();
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error fetching content", exc);
		}
		
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
