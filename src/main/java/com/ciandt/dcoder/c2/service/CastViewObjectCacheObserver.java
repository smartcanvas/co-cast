package com.ciandt.dcoder.c2.service;

import java.util.List;

import com.ciandt.dcoder.c2.entity.CastViewObject;

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
