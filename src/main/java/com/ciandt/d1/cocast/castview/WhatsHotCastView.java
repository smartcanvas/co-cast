package com.ciandt.d1.cocast.castview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.ciandt.d1.cocast.configuration.ConfigurationServices;
import com.google.inject.Inject;

/**
 * Cast View implementation that casts the most liked and shared cards. 
 * 
 * @author Daniel Viveiros
 */
public class WhatsHotCastView extends CastView {
	
	@Inject
	private Logger logger;

	private PopularityComparator comparator;
	private List<CastViewObject> listCastedObjects;
	private Integer maxResults;
	private Integer maxAgeInHours;
	
	/**
	 * Constructor
	 */
	@Inject 
	public WhatsHotCastView( ConfigurationServices configuration ) {
		listCastedObjects = new ArrayList<CastViewObject>();
		comparator = new PopularityComparator();
		maxResults = configuration.getInt("whatshot_max_results");
		maxAgeInHours = configuration.getInt("whatshot_max_age_in_hours");
	}

	@Override
	public void onCacheLoad(List<CastViewObject> listObjects) {
		listCastedObjects.clear();
		
		//the list is already ordered by date. first, let's discard the entries older than "max age"
		for ( CastViewObject castObj: listObjects ) { 
			if ( castObj.getDate() != null ) {
				Long ageInMilliSeconds = System.currentTimeMillis() - castObj.getDate().getTime();
				if ( ageInMilliSeconds <= (maxAgeInHours * 60 * 60 * 1000) ) {
					listCastedObjects.add(castObj);
					logger.info("Object " + castObj.getMnemonic() + " added to 'what is hot' list. Update date = " 
							+ castObj.getDate() );
				} else {
					logger.info("Discarding object " + castObj.getMnemonic() + " because it's too old. Date = " 
							+ castObj.getDate());
				}
			}
		}
		
		//sort the list by popularity and then by date
		Collections.sort(listCastedObjects, comparator);
	}

	@Override
	public List<CastViewObject> castObjects() {
		//if I don't have enough cards to discard, let's return the whole package
		if (listCastedObjects.size() <= maxResults) {
			return listCastedObjects;
		} else {
			//ok, I have more cards than I'm supposed to show, let's take the top ones
			return listCastedObjects.subList(0, maxResults);
		}
	}

	@Override
	protected String getMnemonic() {
		return "whatshot";
	}

}
