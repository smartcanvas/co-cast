package com.ciandt.dcoder.c2.util;

/**
 * Exception thrown when a specific value is requested but it doesn't exist or it's null / empty.
 * 
 * @author Daniel Viveiros
 */
public class ConfigurationRequiredException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param key Configuration key
	 */
	public ConfigurationRequiredException( String key ) {
		super( "Required configuration not found! Key = " + key );
	}

}
