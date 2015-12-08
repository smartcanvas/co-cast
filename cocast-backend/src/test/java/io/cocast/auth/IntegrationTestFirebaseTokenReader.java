package io.cocast.auth;

import io.cocast.admin.ConfigurationServices;
import io.cocast.test.BaseTest;
import io.cocast.util.FirebaseUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the firebase reader
 */
public class IntegrationTestFirebaseTokenReader extends BaseTest {

    private ConfigurationServices configuration;
    private FirebaseUtils firebaseUtils;

    @Before
    public void setUp() {
        configuration = super.getInstance(ConfigurationServices.class);
        firebaseUtils = super.getInstance(FirebaseUtils.class);
    }

    @Test
    public void shouldGetFirebaseCredentials() throws Exception {
        String secret = configuration.getString("firebase.secret", null);

        User user = new User();
        user.setUid("google:118239183782204424177");
        user.setProvider("google");
        user.setEmail("viveiros@ciandt.com");
        user.setName("Daniel Viveiros");
        firebaseUtils.saveAsRoot(user, "/users/" + user.getUid() + ".json");


        FirebaseTokenReader service = super.getInstance(FirebaseTokenReader.class);
        FirebaseTokenReader.FirebaseTokenData tokenData = service.readToken(
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ2IjowLCJkIjp7InVpZCI6Imdvb2dsZToxMTgyMzkxODM3ODIyMDQ0MjQxNzciLCJwcm92aWRlciI6Imdvb2dsZSJ9LCJpYXQiOjE0NDk1Mjk2MzR9.SepZ-kRl13ZqbFzRN3zy_CKsHrd4VE1ATCJ0ORNClzo",
                secret);
        assertNotNull(tokenData);
        assertEquals("google:118239183782204424177", tokenData.getUid());
        assertEquals("google", tokenData.getProvider());
        assertEquals("Daniel Viveiros", tokenData.getName());
        assertEquals("viveiros@ciandt.com", tokenData.getEmail());
    }
}
