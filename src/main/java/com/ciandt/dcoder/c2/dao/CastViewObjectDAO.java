package com.ciandt.dcoder.c2.dao;

import java.util.logging.Logger;

import static com.ciandt.dcoder.c2.dao.OfyService.ofy;

import com.ciandt.dcoder.c2.entity.CastViewObject;
import com.google.inject.Inject;

/**
 * Persists cast view objects
 * 
 * @author Daniel Viveiros
 */
public class CastViewObjectDAO {
	
	@Inject
	private Logger logger;
	
	/**
	 * Saves a cast view object into datastore
	 * @param castViewObject Cast View Object to be saved
	 */
	public void save( CastViewObject castViewObject ) {
		logger.info("Saving cast view object with id = " + castViewObject.getId() );
		ofy().save().entity(castViewObject).now();
	}

}
