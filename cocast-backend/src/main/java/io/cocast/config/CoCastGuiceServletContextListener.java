package io.cocast.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import io.cocast.core.CoreModule;

/**
 * Configures Guice
 */
public class CoCastGuiceServletContextListener extends GuiceServletContextListener {

    public static Injector injector;

    @Override
    protected Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(new BasicModule(), new CoreModule());
        }

        return injector;
    }
}
