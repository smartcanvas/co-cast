package com.ciandt.dcoder.c2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ciandt.dcoder.c2.entity.CastViewObject;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Cache to handle cast view data objects and its interactions
 * 
 * @author Daniel Viveiros
 */
@Singleton
public class CastViewObjectCache {
	
	@Inject
	private Logger logger;
	
	/** Cache Keys */
	private static final String CASTVIEW_CACHE_KEY = "list/CacheViewObjects";
	
	/** Observers */
	private List<CastViewObjectCacheObserver> observers;
	
	private boolean isLoaded;
	
	/**
	 * Constructor
	 */
	public CastViewObjectCache() {
		observers = new ArrayList<CastViewObjectCacheObserver>();
		isLoaded = false;
	}
	
	/**
	 * Put this list of CastViewObjects into de cache
	 */
	public void loadCache( List<CastViewObject> listObjects ) {
		logger.info("Loading cast view objects cache with " + listObjects.size() + " entities");
		
		//gets the memcache service
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.put(CASTVIEW_CACHE_KEY, listObjects, Expiration.byDeltaSeconds( 60 * 60 ));
		
		for ( CastViewObjectCacheObserver observer: observers ) {
			logger.info("Calling observer: " + observer);
			observer.onCacheLoad(listObjects);
		}
		
		isLoaded = true;
	}
	
	/**
	 * Returns is this cache is loaded or not
	 */
	public boolean isLoaded() {
		return isLoaded;
	}
	
	/**
	 * Register a new observer in this cache
	 */
	public void registerObserver( CastViewObjectCacheObserver observer ) {
		observers.add( observer );
	}

}
