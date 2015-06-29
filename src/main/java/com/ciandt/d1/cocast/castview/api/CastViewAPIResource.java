package com.ciandt.d1.cocast.castview.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ciandt.d1.cocast.castview.CastView;
import com.ciandt.d1.cocast.castview.CastViewDAO;
import com.ciandt.d1.cocast.castview.CastViewObject;
import com.ciandt.d1.cocast.castview.CastViewServices;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Resource for all Cast View APIs implementations
 * @author Daniel Viveiros
 */
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Path("/castviews")
@Singleton
public class CastViewAPIResource {
	
	@Inject
	private CastViewServices castViewServices;
	
	@Inject
	private CastViewDAO castViewDAO;
	
	@Inject
	private Logger logger;

	@Inject
	public CastViewAPIResource() {
		super();
	}

	@GET
	@Path("/{mnemonic}")
	public List<CastViewObject> castView(@PathParam("mnemonic") final String mnemonic) {
		
		logger.info("Executing cast view API with mnemonic: " + mnemonic );
		List<CastViewObject> listObjects = null;
		
		try {
			listObjects = castViewServices.getCastViewObjects(mnemonic);
			if (listObjects == null) {
				listObjects = new ArrayList<CastViewObject>();
			}
			logger.info("Returning " + listObjects.size() + " itens: " );
		} catch ( Exception exc ) {
			logger.log(Level.SEVERE, "Error executing cast view API", exc);
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).build());
		}
		
		return listObjects;
	}
	
	@PUT
	public CastView createCastView( @QueryParam("mnemonic") String mnemonic, 
	        @QueryParam("title") String title,
	        @QueryParam("nextCastViewMnemonic") String nextCastViewMnemonic, 
	        @QueryParam("headerBackgroundColor") String headerBackgroundColor,
	        @QueryParam("headerColor") String headerColor, 
	        @QueryParam("progressContainerColor") String progressContainerColor,
	        @QueryParam("activeProgressColor") String activeProgressColor, 
	        @QueryParam("maxResults") Integer maxResults,
	        @QueryParam("maxAgeInHours") Integer maxAgeInHours, 
	        @QueryParam("categoryFilter") String categoryFilter,
	        @QueryParam("orderBy") String orderBy, 
	        @QueryParam("isDefault") Boolean isDefault,
	        @QueryParam("strategy") String strategy ) {
	    
        CastView castView = null;
        
        try {
            castView = new CastView();
            castView.setMnemonic(mnemonic);
            castView.setTitle(title);
            castView.setNextCastViewMnemonic(nextCastViewMnemonic);
            castView.setHeaderBackgroundColor(headerBackgroundColor);
            castView.setHeaderColor(headerColor);
            castView.setProgressContainerColor(progressContainerColor);
            castView.setActiveProgressColor(activeProgressColor);
            castView.setMaxResults(maxResults);
            castView.setMaxAgeInHours(maxAgeInHours);
            castView.setCategoryFilter(categoryFilter);
            castView.setOrderBy(orderBy);
            castView.setIsDefault(isDefault);
            castView.setStrategy(strategy);
            castViewDAO.save(castView);
        } catch ( Exception exc ) {
            logger.log(Level.SEVERE, "Error executing cast view API (PUT)", exc);
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).build());
        }
        
        return castView;
	}
	

}
