package com.ciandt.d1.cocast.castview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
	
	private DateComparator comparator;
	
	private CastViewObjectDAO castViewObjectDAO;
	
	/**
	 * Constructor
	 */
	@Inject
	public CastViewObjectCache( Map<String, CastViewStrategy> mapStrategies, CastViewObjectDAO castViewObjectDAO ) {
	    
	    this.castViewObjectDAO = castViewObjectDAO;
		observers = new ArrayList<CastViewObjectCacheObserver>();
		isLoaded = false;
		comparator = new DateComparator();
		
		Iterator<String> keys = mapStrategies.keySet().iterator();
		while (keys.hasNext()) {
		    String key = keys.next();
		    CastViewStrategy strategy = mapStrategies.get(key);
		    observers.add(strategy);
		}
		
		notifyObservers();
	}
	
	/**
	 * Put this list of CastViewObjects into de cache
	 */
	public void loadCache( List<CastViewObject> listObjects ) {
		
		Collections.sort(listObjects, comparator);
		
		//gets the memcache service
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.put(CASTVIEW_CACHE_KEY, listObjects, Expiration.byDeltaSeconds( 60 * 60 ));
		
		for ( CastViewObjectCacheObserver observer: observers ) {
			observer.onCacheLoad(listObjects);
		}
		
		isLoaded = true;
	}
	
	/**
	 * Notify the observers
	 */
	@SuppressWarnings("unchecked")
	public void notifyObservers() {
		List<CastViewObject> listObjects = null;
		if (isLoaded) {
			MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
			listObjects = (List<CastViewObject>) cache.get(CASTVIEW_CACHE_KEY);
			if (listObjects != null) {
				for ( CastViewObjectCacheObserver observer: observers ) {
					logger.info("Calling observer: " + observer);
					observer.onCacheLoad(listObjects);
				}
			} else {
				listObjects = castViewObjectDAO.findAll();
				this.loadCache(listObjects);
			}
		} else {
			listObjects = castViewObjectDAO.findAll();
			this.loadCache(listObjects);
		}
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
