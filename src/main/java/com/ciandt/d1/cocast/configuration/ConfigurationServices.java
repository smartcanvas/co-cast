package com.ciandt.d1.cocast.configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Handle application configurations
 * 
 * @author Daniel Viveiros
 */
@Singleton
public class ConfigurationServices {
    
    /** Cache */
    private static final String CACHE_KEY = "cache_Configurations_CoCast";
    private static Integer EXPIRATION_TIME = 60 * 60 * 3;
    
    @Inject
    private Logger logger;
    
    @Inject
    private ConfigurationDAO configurationDAO;
	
	/** Property file: co-cast.properties */
    private ResourceBundle properties;

    /**
     * Constructor
     */
	public ConfigurationServices() {
	    try {
	        properties = ResourceBundle.getBundle("co-cast");
	    } catch ( Exception exc ) {
	        //logger.warning("Properties file not found: co-cast.properties. All properties must be in Datastore");
	        System.out.println( "Properties file not found: co-cast.properties. All properties must be in Datastore" );
	    }
	}
	
	/**
	 * List the configurations
	 */
	public List<Configuration> list() {
	    List<Configuration> configurations = new ArrayList<Configuration>();
	    Configuration configuration;
	    String key;
	    String value;
	    
	    //loads data from the bundle
        if (properties != null) {
            Iterator<String> iterator = properties.keySet().iterator();
            while (iterator.hasNext()) {
                key = iterator.next();
                value = properties.getString(key);
                configuration = new Configuration();
                configuration.setKey(key);
                configuration.setValue(value);
                configurations.add(configuration);
                
            }
        }
        
        //loads data from the datastore
        List<Configuration> savedConfigurations = configurationDAO.findAll();
        configurations.addAll(savedConfigurations);
        
        return configurations;
	}
	
	/**
	 * Reloads the configurations
	 */
	public List<Configuration> reload() {
	    MemcacheService cache = MemcacheServiceFactory.getMemcacheService( CACHE_KEY );
	    cache.clearAll();
	    
	    List<Configuration> configurations = this.list();
	    for (Configuration configuration: configurations) {
	        cache.put(configuration.getKey(), configuration.getValue(),
	                Expiration.byDeltaSeconds( EXPIRATION_TIME ));
	    }
	    
	    return configurations;
	}
	
	/**
	 * Get the value of a configuration
	 * 
	 * @param key Key for the configuration
	 * @return The configuration value
	 */
	public String get(String key) {
	    MemcacheService cache = MemcacheServiceFactory.getMemcacheService( CACHE_KEY );
		String value = null;
		
		value = (String) cache.get(key);
		if (value == null) {
		    
		    //let's try to reload the cache
		    reload();
		    value = (String) cache.get(key);
		    if (value == null) {
		        throw new ConfigurationRequiredException(key);
		    }
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
			value = this.get(key); 
		} catch ( ConfigurationRequiredException exc ) {
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
