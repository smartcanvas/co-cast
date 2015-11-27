package io.cocast.auth;

import io.cocast.test.BaseTest;
import org.junit.Test;

/**
 * Tests Google Credential OAuth2
 */
public class IntegrationTestGoogleTokenReader extends BaseTest {


    @Test
    public void shouldGetGoogleCredentials() throws Exception {
        GoogleTokenReader service = super.getInstance(GoogleTokenReader.class);

        //the token expires, that' why this code bellow is commented
        /*
        GoogleTokenReader.GoogleTokenData tokenData = service.readToken(
                //"ya29.OQId5kz2pq-W2zJA0xKEIP0sB5cR18UxpkLztB0RMCz_RXlXAMFTN2ai_qNp_Vm0_Wp0" );
                "ya29.OQJxNKHKgr3FAhG6RCK0YP4HSHAV3NNmc3POY58DcvP0PTO78-MyxzI_H0MQc9AccSL5" );
        assertNotNull( tokenData );
        assertEquals( "daniel.viveiros@gmail.com", tokenData.getEmail() );
        */
    }
}
