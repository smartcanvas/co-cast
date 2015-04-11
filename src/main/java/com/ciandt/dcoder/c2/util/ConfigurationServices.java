package com.ciandt.dcoder.c2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import com.google.inject.Singleton;

/**
 * Handle systems configuration
 * 
 * @author Daniel Viveiros
 */
@Singleton
public class ConfigurationServices {
	
	/** Property file: c2.properties */
    private ResourceBundle properties;

    /**
     * Constructor
     */
	public ConfigurationServices() {
		properties = ResourceBundle.getBundle("c2");
	}
	
	/**
	 * Get the value of a configuration
	 * 
	 * @param key Key for the configuration
	 * @return The configuration value
	 */
	public String get(String key) {
		String value = null;
		
		try {
			value = properties.getString(key); 
		} catch ( MissingResourceException exc ) {
			throw new ConfigurationRequiredException(key);
		}
		
		return value; 
	}
	
	/**
	 * Get the value of a configuration
	 * 
	 * @param key Key for the configuration
	 * @param defValue Default value in case the original one does not exist
	 * @return The configuration value
	 */
	public String get(String key, String defValue) {
		String value = null;
		
		try {
			value = properties.getString(key); 
		} catch ( MissingResourceException exc ) {
			value = defValue;
		}
		
		return value; 
	}
	
	/**
	 * Returns the property as an 'int'
	 * 
	 * @param key Property key
	 * @return Result
	 */
	public int getInt(String key) {
		String strValue = this.get(key);
		return Integer.parseInt(strValue);
	}

	/**
	 * Returns the property as an 'int; 
	 * 
	 * @param key Property key
	 * @param defaultValue Default value
	 * @return Result
	 */
	public int getInt(String key, int defaultValue) {
		String strValue = this.get(key, String.valueOf(defaultValue));
		return Integer.parseInt(strValue);
	}

	/**
	 * Returns the property as an 'boolean' 
	 * 
	 * @param key Property key
	 * @return Result
	 */
	public boolean getBoolean(String key) {
		String strValue = this.get(key);
		return Boolean.parseBoolean(strValue);
	}

	/**
	 * Returns the property as an 'boolean' 
	 * 
	 * @param key Property key
	 * @param defaultValue Default value
	 * @return Result
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		String strValue = this.get(key, String.valueOf(defaultValue));
		return Boolean.parseBoolean(strValue);
	}

	/**
	 * Returns the property as a list of values 
	 * 
	 * @param key Property key
	 * @return Result
	 */
	public List<String> getValues(String key) {
		String value = this.get(key);
		List<String> result = new ArrayList<String>();
        
        if (value != null) {
            StringTokenizer strToken = new StringTokenizer(value, ",");
            String str = null;
            while (strToken.hasMoreTokens()) {
                str = strToken.nextToken();
                result.add(str.trim());
            }
        }
        
        return result; 
	}
}
