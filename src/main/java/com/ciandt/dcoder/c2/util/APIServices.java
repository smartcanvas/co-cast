package com.ciandt.dcoder.c2.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.google.inject.Singleton;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * Utility class with helper methods to deal with APIs
 * 
 * @author <a href="mailto:viveiros@ciandt.com">Daniel Viveiros</a>
 */
@Singleton
public class APIServices {
	
	private ConfigurationUtils configurationServices = ConfigurationUtils.getInstance();

	/** Base URL for API calls */
    private String BASE_URI;
    
    /**
     * Constructor
     */
    public APIServices() {
        BASE_URI = configurationServices.get("base_uri");
    }
    
    /**
     * Creates the Builder (Jersey - java framework to invoke and create APIs) 
     */
    public Builder createBuilder( String apiSpecificPath ) {
        return this.createBuilder(apiSpecificPath, null);
    }
    
    /**
     * Creates the Builder (Jersey - java framework to invoke and create APIs) 
     */
    public Builder createBuilder( String apiSpecificPath, MultivaluedMap<String,String> queryParams ) {
        return this.createBuilder(BASE_URI, apiSpecificPath, queryParams);
    }
    
    /**
     * Creates the Builder (Jersey - java framework to invoke and create APIs) 
     */
    public Builder createBuilder( String rootPath, String apiSpecificPath, MultivaluedMap<String,String> queryParams ) {
        String apiPath = rootPath + apiSpecificPath;
        System.out.println( "Creating builder for API path = " + apiPath );
        
        Client client = Client.create();
        client.setReadTimeout(0); //infinity
        client.setFollowRedirects(false);
        WebResource webResource = client.resource( apiPath );
        if (queryParams != null) {
            webResource.queryParams( queryParams );
        }
        
        Builder builder = webResource.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_XML).type(
                MediaType.APPLICATION_JSON);
        String clientId = configurationServices.get("client_id");
        String apiKey = configurationServices.get("api_key");
        builder.header("CLIENT_ID", clientId);
        builder.header("API_KEY", apiKey);
        
        return builder;
    }
    
    /**
     * Creates the Builder (Jersey - java framework to invoke and create APIs) 
     */
    public Builder createBuilderForPojo(String apiSpecificPath) {
        String apiPath = BASE_URI + apiSpecificPath;
        System.out.println( "Creating builder for API path = " + apiPath );
        
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(
                JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        
        Client client = Client.create(clientConfig);
        WebResource webResource = client.resource(apiPath);
        Builder builder = webResource.accept("application/json").type("application/json");
        String clientId = configurationServices.get("client_id");
        String apiKey = configurationServices.get("api_key");
        builder.header("CLIENT_ID", clientId);
        builder.header("API_KEY", apiKey);
        
        return builder;
    }

}
