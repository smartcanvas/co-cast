package io.cocast.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Configure resources for Jersey 2.0. This will enable creating endpoints for APIs using Jersey.
 */
@ApplicationPath("/")
public class MyApplication extends ResourceConfig {

    /**
     * Constructor
     */
    public MyApplication() {

        System.out.println("Registering injectables...");

        //Resource packages
        packages("io.cocast.core");

        // activate Jackson-based JSON support
        register(JacksonFeature.class);

        //weld
        //Weld weld = new Weld();
        //weld.initialize();

    }

}
