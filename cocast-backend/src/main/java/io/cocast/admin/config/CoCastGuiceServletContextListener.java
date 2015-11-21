package io.cocast.admin.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Configures Guice
 */
public class CoCastGuiceServletContextListener extends GuiceServletContextListener {

    public static Injector injector;

    @Override
    protected Injector getInjector() {

        System.out.println("Getting injector");
        injector = Guice.createInjector(new CoCastModule());
        return injector;

    }
}
