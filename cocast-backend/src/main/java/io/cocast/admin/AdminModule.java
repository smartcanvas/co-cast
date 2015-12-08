package io.cocast.admin;

import com.google.inject.servlet.ServletModule;

/**
 * Guice module for admin resources
 */
public class AdminModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(ColorPaletteRepository.class);
    }
}
