package com.ciandt.d1.cocast.configuration;

import static com.ciandt.d1.cocast.util.OfyService.ofy;

import java.util.List;
import java.util.logging.Logger;

import com.google.inject.Inject;

/**
 * Persists and loads configurations
 * 
 * @author Daniel Viveiros
 */
public class ConfigurationDAO {
    
    @Inject
    private Logger logger;
    
    /**
     * Return all configurations saved on the datastore
     * @param castViewObject Cast View Object to be saved
     */
    public List<Configuration> findAll() {
        return ofy().load().type(Configuration.class).list();
    }
    
    /**
     * Saves a configuration object into datastore
     * @param castViewObject Cast View Object to be saved
     */
    public void save( Configuration configuration ) {
        logger.info("Saving configuration = " + configuration );
        ofy().save().entity(configuration).now();
    }

}
