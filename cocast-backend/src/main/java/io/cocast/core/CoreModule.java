package io.cocast.core;

import com.google.inject.servlet.ServletModule;

/**
 * Guice module for core resources
 */
public class CoreModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(NetworkServices.class);
        bind(NetworkRepository.class);
    }
}
