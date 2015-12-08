package io.cocast.auth;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.cocast.admin.ConfigurationServices;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@Singleton
public class AuthServices {

    private static final Logger logger = LogManager.getLogger(AuthServices.class.toString());

    public static final int DEFAULT_KEY_EXPIRATION = 60 * 24 * 365 * 10;

    @Inject
    private ConfigurationServices configuration;

    @Inject
    @Named("accessToken")
    private TokenServices accessTokenServices;

    @Inject
    private FirebaseTokenReader firebaseTokenReader;

    @Inject
    private GoogleTokenReader googleTokenReader;


    /**
     * Performs authentication and generates the token
     * @return New access token
     */
    public String performAuthAndGenerateToken(String firebaseToken, String googleAccessToken)
            throws AuthenticationException {

        String secret = configuration.getString("firebase.secret", null);
        FirebaseTokenReader.FirebaseTokenData firebaseTokenData = firebaseTokenReader
                .readToken(firebaseToken, secret);

        GoogleTokenReader.GoogleTokenData googleTokenData = googleTokenReader.readToken(googleAccessToken);

        //checks if this pair firebase + google is valid
        if (!googleTokenData.getVerified_email() ||
                (!firebaseTokenData.getUid().equals("google:" + googleTokenData.getUser_id()))) {
            throw new AuthenticationException(403, googleTokenData.getEmail(), googleTokenData.getUser_id());
        }

        SecurityClaims securityClaims = new SecurityClaims(AuthConstants.DEFAULT_ISSUER)
                .setEmail(googleTokenData.getEmail()).setSubject(firebaseTokenData.getUid());

        try {
            return accessTokenServices.generateToken(securityClaims, AuthConstants.JWT_TTL, getJwtSecret());
        } catch (Exception e) {
            String message = String.format("Caught exception while trying to validate external tokens. [FirebaseToken: %s, GoogleToken: %s], message: %s",
                    firebaseToken, googleAccessToken, e.getMessage());
            throw new AuthenticationException(500, message, e);
        }
    }

    private String getJwtSecret() {
        return configuration.getString("cocast.secret", null);
    }

}
