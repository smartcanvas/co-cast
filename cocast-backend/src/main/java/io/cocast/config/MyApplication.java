package io.cocast.config;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;

/**
 * Configure resources for Jersey 2.0. This will enable creating endpoints for APIs using Jersey.
 */
@ApplicationPath("/")
public class MyApplication extends ResourceConfig {

    @Inject
    public MyApplication(ServiceLocator serviceLocator) {

        System.out.println("Registering injectables...");

        //Resource packages
        packages(true, "io.cocast");

        //Activate Jackson-based JSON support
        register(JacksonFeature.class);

        //Configuring the bridget between Guice and HK2 - Jersey 2 default injector
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(BackendGuiceServletContextListener.injector);

    }

}
