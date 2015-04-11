package com.ciandt.dcoder.c2.dao;

import com.ciandt.dcoder.c2.entity.Greeting;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {

	static {
		factory().register(Greeting.class);
	}

	/**
	 * Return the ofy correct transaction context.
	 * 
	 * @return Objectify
	 */
	public static Objectify ofy() {
		return ObjectifyService.ofy();
	}

	/**
	 * Return the ofy factory.
	 * 
	 * @return ObjectifyFactory
	 */
	public static ObjectifyFactory factory() {
		return ObjectifyService.factory();
	}
}
