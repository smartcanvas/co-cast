package com.ciandt.dcoder.c2.service;

import java.util.ArrayList;
import java.util.List;

import com.ciandt.dcoder.c2.entity.CastViewObject;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Singleton;

/**
 * Cache to handle cast view data objects and its interactions
 * 
 * @author Daniel Viveiros
 */
@Singleton
public class CastViewObjectCache {
	
	/** Cache Keys */
	private static final String CASTVIEW_CACHE_KEY = "list/CacheViewObjects";
	
	/** Observers */
	private List<CastViewObjectCacheObserver> observers;
	
	/**
	 * Constructor
	 */
	public CastViewObjectCache() {
		observers = new ArrayList<CastViewObjectCacheObserver>();
	}
	
	/**
	 * Put this list of CastViewObjects into de cache
	 */
	public void loadCache( List<CastViewObject> listObjects ) {
		//gets the memcache service
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.put(CASTVIEW_CACHE_KEY, listObjects, Expiration.byDeltaSeconds( 60 * 60 ));
		
		for ( CastViewObjectCacheObserver observer: observers ) {
			observer.onCacheLoad(listObjects);
		}
	}

}
