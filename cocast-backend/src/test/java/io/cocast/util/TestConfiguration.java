package io.cocast.util;

import io.cocast.configuration.ConfigurationServices;
import io.cocast.test.BaseTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the configuration mechanism
 */
public class TestConfiguration extends BaseTest {

    private ConfigurationServices configuration;

    @Before
    public void setUp() {
        configuration = super.getInstance(ConfigurationServices.class);
    }

    @Test
    public void shouldReadConfiguration() throws Exception {
        String key = configuration.getString("firebase.secret", null);
        assertNotNull(key);
    }

}
