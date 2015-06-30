package com.ciandt.d1.cocast.castview;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * Abstract class for all Cast Views. A "Cast View" is an strategy that includes card selection to be shown in
 * a castable device.
 *  
 * @author Daniel Viveiros
 */
public abstract class CastViewStrategy implements CastViewObjectCacheObserver {
    
    private static final String CACHE_KEY = "cacheCastViewStrategy";
    private static Integer EXPIRATION_TIME = 60 * 60 * 3;
    
    private List<CastViewObject> listCastedObjects;

    /**
     * Constructor
     */
    public CastViewStrategy() {
        listCastedObjects = new ArrayList<CastViewObject>();
    }
	
	/**
	 * Performs lazy initialization on the cache
	 */
	@Override
	public void onCacheLoad(List<CastViewObject> listObjects) {
	    this.listCastedObjects = listObjects;
	    MemcacheService cache = MemcacheServiceFactory.getMemcacheService(CACHE_KEY + getStrategyName());
	    cache.clearAll();
	}
	
	/**
	 * Cast objects: return the list of objects that must be shown in the castable device
	 */
	@SuppressWarnings("unchecked")
    public List<CastViewObject> castObjects( CastView castView ) {
	    MemcacheService cache = MemcacheServiceFactory.getMemcacheService(CACHE_KEY + getStrategyName());
        List<CastViewObject> castViewObjects = (List<CastViewObject>) cache.get( castView.getMnemonic() );
        
        castViewObjects = loadObjects(castView);
        
        /*
        if (castViewObjects != null) {
            return castViewObjects;
        } else {
            castViewObjects = loadObjects(castView);
            cache.put(castView.getMnemonic(), castViewObjects, Expiration.byDeltaSeconds(EXPIRATION_TIME));
        }
        */
        
        return castViewObjects;
	}
	
	/**
	 * Create the list of objects for a specific cast view
	 */
	public abstract List<CastViewObject> loadObjects( CastView castView );
	
	/**
     * Create the list of objects for a specific cast view
     */
    public abstract String getStrategyName();
	
	/**
	 * Returns the list of objects
	 */
	public List<CastViewObject> getCastViewObjectList() {
	    return this.listCastedObjects;
	}
	
	/**
     * Returns a copy of list of objects
     */
    public List<CastViewObject> cloneCastViewObjectList() {
        List<CastViewObject> clone = new ArrayList<CastViewObject>();
        clone.addAll(getCastViewObjectList());
        return clone;
    }
}
