package io.cocast.ext;

import com.google.inject.servlet.ServletModule;
import io.cocast.ext.match.MatchActionRepository;
import io.cocast.ext.match.MatchRepository;
import io.cocast.ext.people.PersonRepository;
import io.cocast.ext.people.PersonServices;

/**
 * Guice module for admin resources
 */
public class ExtensionsModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(PersonRepository.class);
        bind(MatchActionRepository.class);
        bind(MatchRepository.class);
        bind(PersonServices.class);
    }
}

