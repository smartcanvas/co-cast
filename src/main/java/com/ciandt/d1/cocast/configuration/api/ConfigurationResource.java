package com.ciandt.d1.cocast.configuration.api;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ciandt.d1.cocast.configuration.Configuration;
import com.ciandt.d1.cocast.configuration.ConfigurationDAO;
import com.ciandt.d1.cocast.configuration.ConfigurationServices;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Resource to expose configuration services as APIs
 * 
 * @author <a href="mailto:viveiros@ciandt.com">Daniel Viveiros</a>
 */
@Singleton
@Path("/api/configurations")
public class ConfigurationResource {
    
    @Inject
    private Logger logger;
    
    @Inject
    private ConfigurationServices configurationServices;
    
    @Inject
    private ConfigurationDAO configurationDAO;
    
    /**
     * Read content from Smart Canvas and populates the cache
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response saveConfiguration( 
            @NotNull @QueryParam("key") final String key,
            @NotNull @QueryParam("value") final String value, 
            @QueryParam("desc") final String desc ) {
        
        Configuration configuration = new Configuration();
        configuration.setKey(key);
        configuration.setValue(value);
        configuration.setDescription(desc);
        
        try {
            configurationDAO.save(configuration);
        } catch (Exception exc) {
            logger.log(Level.SEVERE, "Error saving configuration: " + configuration, exc);
            return Response.serverError().build();
        }
        
        return Response.ok(configuration).build();
    }
    
    /**
     * Read content from Smart Canvas and populates the cache
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listConfigurations() {
        logger.log(Level.FINE, "Listing configurations");
        
        List<Configuration> configurations = null;
        
        try {
            configurations = configurationServices.list();
        } catch ( Exception exc ) {
            logger.log(Level.SEVERE, "Error listing configurations", exc);
            return Response.serverError().build();
        }
        
        return Response.ok( configurations ).build();
    }
    
    /**
     * Read content from Smart Canvas and populates the cache
     */
    @GET
    @Path("/reload")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response reloadConfigurations() {
        List<Configuration> configurations = null;
        
        try {
            configurations = configurationServices.reload();
        } catch ( Exception exc ) {
            logger.log(Level.SEVERE, "Error reloading configurations", exc);
            return Response.serverError().build();
        }
        
        return Response.ok( configurations ).build();
    }

}
