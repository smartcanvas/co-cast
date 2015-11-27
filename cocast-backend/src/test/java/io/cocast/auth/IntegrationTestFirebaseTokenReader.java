package io.cocast.auth;

import io.cocast.test.BaseTest;
import io.cocast.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the firebase reader
 */
public class IntegrationTestFirebaseTokenReader extends BaseTest {

    private Configuration configuration;

    @Before
    public void setUp() {
        configuration = super.getInstance(Configuration.class);
    }

    @Test
    public void shouldGetGoogleCredentials() throws Exception {
        String secret = configuration.getString("firebase.secret", null);

        FirebaseTokenReader service = super.getInstance(FirebaseTokenReader.class);
        FirebaseTokenReader.FirebaseTokenData tokenData = service.readToken(
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ2IjowLCJkIjp7InVpZCI6Imdvb2dsZToxMDU2OTM5OTQxMzMxOTQ3MTE4MzEiLCJwcm92aWRlciI6Imdvb2dsZSJ9LCJpYXQiOjE0NDg2NTcyMzN9.aKu3fVCWWBlmV0udb0dBOY6tSValriW69XznDryRJ0U",
                secret);
        assertNotNull(tokenData);
        assertEquals("google:105693994133194711831", tokenData.getUid());
    }
}
