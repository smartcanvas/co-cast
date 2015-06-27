package com.ciandt.d1.cocast.content.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.ciandt.d1.cocast.castview.CastViewServices;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * APIs for populating Co-cast with content coming from Smart Canvas
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
