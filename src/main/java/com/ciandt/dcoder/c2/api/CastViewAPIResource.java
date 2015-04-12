package com.ciandt.dcoder.c2.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ciandt.dcoder.c2.entity.CastViewObject;
import com.ciandt.dcoder.c2.service.CastViewServices;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Resource for all Cast View APIs implementations
 * @author Daniel Viveiros
 */
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Path("/api")
@Singleton
public class CastViewAPIResource {
	
	@Inject
	private CastViewServices castViewServices;
	
	@Inject
	private Logger logger;

	@Inject
	public CastViewAPIResource() {
		super();
	}

	@GET
	@Path("/castview/{mnemonic}")
	public List<CastViewObject> castView(@PathParam("mnemonic") final String mnemonic) {
		
		logger.info("Executing cast view API with mnemonic: " + mnemonic );
		List<CastViewObject> listObjects = null;
		
		try {
			listObjects = castViewServices.getCastViewObjects(mnemonic);
			if (listObjects == null) {
				listObjects = new ArrayList<CastViewObject>();
			}
			logger.info("Returning " + listObjects.size() + " itens: " );
			for ( CastViewObject obj: listObjects ) {
				logger.info( "Mnemonic: " + obj.getMnemonic() + ", Update date = " + obj.getUpdateDate() 
						+ ", Popularity = " + obj.getPopularity() );
			}
		} catch ( Exception exc ) {
			logger.log(Level.SEVERE, "Error executing cast view API", exc);
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).build());
		}
		
		return listObjects;
	}
	

}
