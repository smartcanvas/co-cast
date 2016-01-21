package io.cocast.core;

import com.google.inject.AbstractModule;

/**
 * Guice module for core resources
 */
public class CoreModule extends AbstractModule {

    @Override
    protected void configure() {

        //Repositories
        bind(NetworkRepository.class);
        bind(StationRepository.class);
        bind(SettingsRepository.class);
        bind(ChannelRepository.class);
        bind(ContentRepository.class);

        //Services
        bind(LiveStreamServices.class);
        bind(ContentServices.class);
    }
}
