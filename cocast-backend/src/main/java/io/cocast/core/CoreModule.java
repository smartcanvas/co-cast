package io.cocast.core;

import com.google.inject.servlet.ServletModule;

/**
 * Guice module for core module
 */
public class CoreModule extends ServletModule {

    @Override
    protected void configureServlets() {

        //Resources
        bind(NetworkServices.class).to(DefaultNetworkServicesImpl.class);
    }
}
