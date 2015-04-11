package com.danielviveiros.dao;

import java.util.List;

import com.danielviveiros.entity.Greeting;

public interface GreetingDAO {
	
	/**
	 * List all greetings
	 * @return All greetings
	 */
	public List<Greeting> findGreetings();
	
	/**
	 * Insert a new greeting into datastore
	 * @param greeting Greeting to be inserted
	 * @return the generated id
	 */
	public Long insert( Greeting greeting );
	
	/**
	 * Remove a specific greeting from the datastore
	 * @param Greeting to be removed
	 */
	public void delete( Greeting greeting );

}
