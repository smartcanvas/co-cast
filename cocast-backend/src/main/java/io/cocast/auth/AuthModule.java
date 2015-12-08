package io.cocast.auth;

import com.google.inject.servlet.ServletModule;

/**
 * Guice module for auth resources
 */
public class AuthModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(AuthServices.class);
        bind(TokenServices.class);
    }
}
