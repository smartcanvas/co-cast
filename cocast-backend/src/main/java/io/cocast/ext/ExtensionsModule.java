package io.cocast.ext;

import com.google.inject.servlet.ServletModule;
import io.cocast.ext.people.PersonRepository;

/**
 * Guice module for admin resources
 */
public class ExtensionsModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(PersonRepository.class);
    }
}

