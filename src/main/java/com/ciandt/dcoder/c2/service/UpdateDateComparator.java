package com.ciandt.dcoder.c2.service;

import java.util.Comparator;

import com.ciandt.dcoder.c2.entity.CastViewObject;

/**
 * Comparator to be used to put the most recent object in the top
 * 
 * @author Daniel Viveiros
 */
public class UpdateDateComparator implements Comparator<CastViewObject> {

	@Override
	public int compare(CastViewObject o1, CastViewObject o2) {
		return new Long(o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()).intValue();
	}

}
