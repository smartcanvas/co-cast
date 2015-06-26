package com.ciandt.d1.cocast.castview;

import static com.ciandt.d1.cocast.util.OfyService.ofy;

import java.util.List;
import java.util.logging.Logger;

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
	 * Return all cast views saved on the datastore
	 * @param castViewObject Cast View Object to be saved
	 */
	public List<CastViewObject> findAll() {
		return ofy().load().type(CastViewObject.class).list();
	}
	
	/**
	 * Saves a cast view object into datastore
	 * @param castViewObject Cast View Object to be saved
	 */
	public void save( CastViewObject castViewObject ) {
		logger.info("Saving cast view object with mnemonic = " + castViewObject.getMnemonic() );
		ofy().save().entity(castViewObject).now();
	}

}
