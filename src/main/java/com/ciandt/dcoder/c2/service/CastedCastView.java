package com.ciandt.dcoder.c2.service;

import java.util.ArrayList;
import java.util.List;

import com.ciandt.dcoder.c2.entity.CastViewObject;
import com.ciandt.dcoder.c2.util.ConfigurationUtils;

/**
 * Cast View implementation that cast objects with a #cast hashtag 
 * 
 * @author Daniel Viveiros
 */
public class CastedCastView extends CastView {
	
	private List<CastViewObject> listCastedObjects;
	private ConfigurationUtils configuration = ConfigurationUtils.getInstance();
	private Integer maxResults;
	
	/**
	 * Constructor
	 */
	public CastedCastView() {
		listCastedObjects = new ArrayList<CastViewObject>();
		maxResults = configuration.getInt("casted_max_results");
	}

	@Override
	protected void innerOnCacheLoad(List<CastViewObject> listObjects) {
		listCastedObjects.clear();
		for ( CastViewObject castObj: listObjects ) {
			if (castObj.getIsCasted()) {
				listCastedObjects.add( castObj );
			}
		}
	}

	@Override
	protected List<CastViewObject> innerCastObjects() {
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
