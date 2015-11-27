package io.cocast.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.cocast.config.BasicBackendModule;
import io.cocast.core.CoreModule;
import org.junit.Before;

/**
 * Basic class for all unit tests
 */
public class BaseTest {

    private Injector injector;

    @Before
    public void setUpParent() {
        this.injector = Guice.createInjector(new BasicBackendModule(), new CoreModule());
    }

    protected <T> T getInstance(Class<T> type) {
        return this.injector.getInstance(type);
    }
}
