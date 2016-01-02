package io.cocast.core;

import com.google.inject.servlet.ServletModule;

/**
 * Guice module for core resources
 */
public class CoreModule extends ServletModule {

    @Override
    protected void configureServlets() {

        //Repositories
        bind(NetworkRepository.class);
        bind(StationRepository.class);
        bind(SettingsRepository.class);
        bind(ChannelRepository.class);
        bind(ContentRepository.class);
    }
}
