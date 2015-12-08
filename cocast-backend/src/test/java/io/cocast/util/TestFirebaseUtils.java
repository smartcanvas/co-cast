package io.cocast.util;

import io.cocast.admin.Configuration;
import io.cocast.test.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests ConfigurationServices
 */
public class TestFirebaseUtils extends BaseTest {

    private FirebaseUtils firebaseUtils;

    @Before
    public void setUp() {
        firebaseUtils = super.getInstance(FirebaseUtils.class);

    }

    @Test
    public void shouldReadConfigurations() throws Exception {
        String fromFirebase = "{\"config1\":{\"createdBy\":\"daniel.viveiros@gmail.com\",\"description\":\"desc1\",\"lastUpdate\":1449082639792,\"name\":\"config1\",\"value\":\"value2\"},\"config2\":{\"createdBy\":\"daniel.viveiros@gmail.com\",\"description\":\"desc2\",\"lastUpdate\":1449089710255,\"name\":\"config2\",\"value\":\"value2\"}}";
        List<Configuration> list = firebaseUtils.getListFromResult(fromFirebase, Configuration.class);
        assertEquals(2, list.size());
        assertEquals("config1", list.get(0).getName());
    }
}
