package io.cocast.core;

import io.cocast.test.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests Network Repository
 */
public class TestNetworkRepository extends BaseTest {

    private NetworkRepository networkRepository;

    @Before
    public void setUp() {
        networkRepository = super.getInstance(NetworkRepository.class);
    }

    @Test
    public void shouldListMemberships() throws Exception {
        List<NetworkMembership> membershipList = networkRepository.listMemberships();
        assertNotNull(membershipList);
        assertTrue(membershipList.size() > 0);
    }


}
