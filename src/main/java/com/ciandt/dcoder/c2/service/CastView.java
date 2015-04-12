package com.ciandt.dcoder.c2.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ciandt.dcoder.c2.entity.CastViewObject;
import com.ciandt.dcoder.c2.util.ConfigurationUtils;
import com.google.inject.Inject;

/**
 * Abstract class for all Cast Views. A "Cast View" is an strategy that includes card selection to be shown in
 * a castable device.
 *  
 * @author Daniel Viveiros
 */
@SuppressWarnings("rawtypes")
public abstract class CastView implements CastViewObjectCacheObserver {
	
	@Inject
	private static Logger logger;
	
	@Inject
	private static CastViewDataServices castDataServices;
	
	/** Maps the mnemonics to the implementations */
	private static Map<String,CastView> implementationMap;
	
	/** Initializes the Cast View mechanism */
	static {
		implementationMap = new HashMap<String,CastView>();
		ConfigurationUtils confUtil = ConfigurationUtils.getInstance();
		List<String> strImplementations = confUtil.getValues("cast_view_implementations");
		for (String strImplementation: strImplementations) {
			try {
				Class cImplementation = Class.forName(strImplementation);
				CastView castView = (CastView) cImplementation.newInstance();
				logger.info("Registering cast view with mnemonic: " + castView.getMnemonic());
				implementationMap.put(castView.getMnemonic(), castView);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				logger.log(Level.SEVERE, "Error initializing Cast View mechanism", e);
			}
		}
		
		try {
			castDataServices.refreshCardCache();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error initializing the cache", e);
		}
	}
	
	/**
	 * Gets the implementation based on the mnemonic
	 */
	public static CastView getCastView( String mnemonic ) {
		CastView castView = implementationMap.get(mnemonic);
		if (castView == null) {
			throw new RuntimeException("Cast View implementation not found: " + mnemonic);
		} else {
			return castView;
		}
	}
	
	/**
	 * Cast objects: return the list of objects that must be shown in the castable device
	 */
	public abstract List<CastViewObject> castObjects();
	
	/**
	 * Returns the mnemonic for this Cast View. This mnemonic is going to be used in the API.
	 */
	protected abstract String getMnemonic();

}
