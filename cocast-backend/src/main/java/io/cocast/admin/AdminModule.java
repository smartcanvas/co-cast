package io.cocast.admin;

import com.google.inject.AbstractModule;

/**
 * Guice module for admin resources
 */
public class AdminModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ThemeRepository.class);
        bind(ThemeServices.class);
    }
}
