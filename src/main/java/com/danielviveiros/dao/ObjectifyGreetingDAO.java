package com.danielviveiros.dao;

import static com.danielviveiros.dao.OfyService.ofy;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.danielviveiros.entity.Greeting;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;

@Singleton
public class ObjectifyGreetingDAO implements GreetingDAO {
	
	@Inject
	private Logger log;
	
	@Override
	public List<Greeting> findGreetings() {
		log.info("Finding all greetings");
		
		//checks if the greetings are in the cache
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		@SuppressWarnings("unchecked")
		List<Greeting> greetings = (List<Greeting>) syncCache.get( "GREETINGS" );
		
		if (greetings == null) {
			log.info("Not found in cache");
			greetings = ofy().load().type(Greeting.class).list();
		} else {
			log.info("Using cache!");
		}
		
	    if (greetings != null) {
	    	log.info("Returning " + greetings.size() + " greetings");
	    }
	    return greetings;
	}
	
	@Override
	public Long insert( Greeting greeting ) {
		log.info("Inserting a new greeting");
		
		//invalidates the cache
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		syncCache.delete( "GREETINGS" );
		
		Key<Greeting> key = ofy().save().entity(greeting).now();
		return key.getId();
		
	}
	
	@Override
	public void delete(Greeting greeting) {
		log.info("Deleting a new greeting");
		ofy().delete().entity(greeting).now();
	}

}
