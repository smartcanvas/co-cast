package com.ciandt.d1.cocast.castview;

import java.util.ArrayList;
import java.util.List;

import com.ciandt.d1.cocast.configuration.ConfigurationServices;
import com.google.inject.Inject;

/**
 * Cast View implementation that returns the newest releases to be shown
 * @author Daniel Viveiros
 */
public class WhatsNewCastView extends CastViewStrategy {
    
	private List<CastViewObject> listCastedObjects;
	private Integer maxResults;
	
	/**
	 * Constructor
	 */
	@Inject
	public WhatsNewCastView( ConfigurationServices configuration ) {
		listCastedObjects = new ArrayList<CastViewObject>();
		maxResults = configuration.getInt("whatsnew_max_results");
	}

	@Override
	public void onCacheLoad(List<CastViewObject> listObjects) {
		listCastedObjects.clear();
		
		//the list is already ordered by date, so we just need to take the first entries
		int i = 0;
		for ( CastViewObject castObj: listObjects ) {
			if ( i < maxResults ) {
				listCastedObjects.add( castObj );
			} else {
				break;
			}
			i++;
		}
	}

	@Override
	public List<CastViewObject> castObjects() {
		return listCastedObjects;
	}

	@Override
	protected String getMnemonic() {
		return "whatsnew";
	}

}
