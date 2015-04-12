package com.ciandt.dcoder.c2.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.concurrent.ThreadSafe;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ciandt.dcoder.c2.entity.CastViewObject;
import com.ciandt.dcoder.c2.service.CastView;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Path("/api")
@ThreadSafe
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Singleton
public class CommonResource {
	
	@Inject
	private Logger logger;

	@Inject
	public CommonResource() {
		super();
	}

	@GET
	@Path("/castview/{mnemonic}")
	public Response castView(@PathParam("mnemonic") final String mnemonic) {
		
		logger.info("Executing cast view API with mnemonic: " + mnemonic );
		GenericEntity<List<CastViewObject>> entity;
		List<CastViewObject> result = null;
		
		try {
			CastView castView = CastView.getCastView(mnemonic);
			result = castView.castObjects();
			if (result == null) {
				result = new ArrayList<CastViewObject>();
			}
			logger.info("Returning " + result.size() + " itens: " );
			for ( CastViewObject obj: result ) {
				logger.info( "Mnemonic: " + obj.getMnemonic() + ", Update date = " + obj.getUpdateDate() 
						+ ", Popularity = " + obj.getPopularity() );
			}
		} catch ( Exception exc ) {
			logger.log(Level.SEVERE, "Error executing cast view API", exc);
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(mnemonic).build());
		}
		
		entity = new GenericEntity<List<CastViewObject>> (result) {};
		return Response.ok(entity).build();
	}

}
