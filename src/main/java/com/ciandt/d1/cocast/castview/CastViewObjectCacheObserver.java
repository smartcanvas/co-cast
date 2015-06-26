package com.ciandt.d1.cocast.castview;

import java.util.List;

/**
 * Interface common to all cast view cache observers. All Cast Views will implement this interface to be
 * notified that something has changed in the cache area and will be able to update its contents and
 * strategies based on that. 
 * 
 * @author Daniel Viveiros
 */
public interface CastViewObjectCacheObserver {
	
	/**
	 * Callback when a new list is put into the cache
	 */
	public void onCacheLoad( List<CastViewObject> listObjects );

}
