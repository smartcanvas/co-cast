package io.cocast.auth;

import com.google.inject.AbstractModule;

/**
 * Guice module for auth resources
 */
public class AuthModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AuthServices.class);
        bind(TokenServices.class);
    }
}
