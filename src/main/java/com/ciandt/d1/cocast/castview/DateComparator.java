package com.ciandt.d1.cocast.castview;

import java.util.Comparator;

/**
 * Comparator to be used to put the most recent object in the top
 * 
 * @author Daniel Viveiros
 */
public class DateComparator implements Comparator<CastViewObject> {

	@Override
	public int compare(CastViewObject o1, CastViewObject o2) {
		return o2.getDate().compareTo(o1.getDate());
	}

}
