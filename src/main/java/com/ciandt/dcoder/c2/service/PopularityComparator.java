package com.ciandt.dcoder.c2.service;

import java.util.Comparator;

import com.ciandt.dcoder.c2.entity.CastViewObject;

/**
 * Comparator used to sort the popularity list and discard the least popular entries
 *  
 * @author Daniel Viveiros
 */
public class PopularityComparator implements Comparator<CastViewObject> {

	@Override
	public int compare(CastViewObject o1, CastViewObject o2) {
		
		// by popularity
		Integer comparison = o1.getPopularity() - o2.getPopularity();
		if (comparison > 0) {
			return comparison;
		}
		
		//by date
		if (o1.getUpdateDate() != null && o2.getUpdateDate() != null) {
			comparison = new Long(o1.getUpdateDate().getTime() - o2.getUpdateDate().getTime()).intValue();
		} else {
			comparison = 0;
		}
		
		return comparison;
	}

}
