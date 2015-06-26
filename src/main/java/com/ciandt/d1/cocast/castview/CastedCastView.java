package com.ciandt.d1.cocast.castview;

import java.util.ArrayList;
import java.util.List;

import com.ciandt.d1.cocast.configuration.ConfigurationServices;
import com.google.inject.Inject;

/**
 * Cast View implementation that cast objects with a #cast hashtag 
 * 
 * @author Daniel Viveiros
 */
public class CastedCastView extends CastView {
    
    @Inject
    private ConfigurationServices configuration;
	
	private List<CastViewObject> listCastedObjects;
	private Integer maxResults;
	
	/**
	 * Constructor
	 */
	public CastedCastView() {
		listCastedObjects = new ArrayList<CastViewObject>();
		maxResults = configuration.getInt("casted_max_results");
	}

	@Override
	public void onCacheLoad(List<CastViewObject> listObjects) {
		listCastedObjects.clear();
		for ( CastViewObject castObj: listObjects ) {
			if (castObj.getIsCasted()) {
				listCastedObjects.add( castObj );
			}
		}
	}

	@Override
	public List<CastViewObject> castObjects() {
		//if I don't have enough cards to discard, let's return the whole package
		if (listCastedObjects.size() <= maxResults) {
			return listCastedObjects;
		} else {
			//ok, I have more cards than I'm supposed to show, let's take the newest ones
			return listCastedObjects.subList(0, maxResults);
		}
	}

	@Override
	protected String getMnemonic() {
		return "casted";
	}

}
