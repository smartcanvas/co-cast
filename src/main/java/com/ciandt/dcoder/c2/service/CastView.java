package com.ciandt.dcoder.c2.service;

import java.util.List;

import com.ciandt.dcoder.c2.entity.CastViewObject;

/**
 * Abstract class for all Cast Views. A "Cast View" is an strategy that includes card selection to be shown in
 * a castable device.
 *  
 * @author Daniel Viveiros
 */
public abstract class CastView implements CastViewObjectCacheObserver {

	
	/**
	 * Performs lazy initialization on the cache
	 */
	@Override
	public abstract void onCacheLoad(List<CastViewObject> listObjects);
	
	/**
	 * Cast objects: return the list of objects that must be shown in the castable device
	 */
	public abstract List<CastViewObject> castObjects();
	
	/**
	 * Overrides toString
	 */
	public String toString() {
		return "Cast View [" + getMnemonic() + "]";
	}
	
	/**
	 * Returns the mnemonic for this Cast View. This mnemonic is going to be used in the API.
	 */
	protected abstract String getMnemonic();
}
