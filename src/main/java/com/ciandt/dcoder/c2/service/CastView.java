package com.ciandt.dcoder.c2.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ciandt.dcoder.c2.config.CommonModule;
import com.ciandt.dcoder.c2.entity.CastViewObject;
import com.ciandt.dcoder.c2.util.ConfigurationUtils;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Abstract class for all Cast Views. A "Cast View" is an strategy that includes card selection to be shown in
 * a castable device.
 *  
 * @author Daniel Viveiros
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class CastView implements CastViewObjectCacheObserver {
	
	@Inject
	private CastViewDataServices castViewDataServices;
	
	/** Maps the mnemonics to the implementations */
	private static Map<String,CastView> implementationMap;
	
	private static boolean hasCachedItens = false;
	
	/** Initializes the Cast View mechanism */
	static {
		implementationMap = new HashMap<String,CastView>();
		ConfigurationUtils confUtil = ConfigurationUtils.getInstance();
		List<String> strImplementations = confUtil.getValues("cast_view_implementations");
		for (String strImplementation: strImplementations) {
			try {
				Class cImplementation = Class.forName(strImplementation);
				//CastView castView = (CastView) cImplementation.newInstance();
				Injector injector = Guice.createInjector( new CommonModule() );
				CastView castView = injector.getInstance(cImplementation);
				implementationMap.put(castView.getMnemonic(), castView);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
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
	 * Performs lazy initialization on the cache
	 */
	@Override
	public void onCacheLoad(List<CastViewObject> listObjects) {
		hasCachedItens = true;
		this.innerOnCacheLoad(listObjects);
	}
	
	/**
	 * Cast objects: return the list of objects that must be shown in the castable device
	 */
	public List<CastViewObject> castObjects() {
		if (!hasCachedItens) {
			try {
				castViewDataServices.createExpressCache();
				hasCachedItens = true;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		return innerCastObjects();
	}
	
	/**
	 * Performs lazy initialization on the cache
	 */
	protected abstract void innerOnCacheLoad(List<CastViewObject> listObjects);
	
	/**
	 * Cast objects: return the list of objects that must be shown in the castable device
	 */
	protected abstract List<CastViewObject> innerCastObjects();
	
	/**
	 * Returns the mnemonic for this Cast View. This mnemonic is going to be used in the API.
	 */
	protected abstract String getMnemonic();
}
