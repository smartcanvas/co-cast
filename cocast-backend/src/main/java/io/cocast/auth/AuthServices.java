package io.cocast.auth;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.cocast.admin.ConfigurationServices;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@Singleton
class AuthServices {

    private static final Logger logger = LogManager.getLogger(AuthServices.class.toString());

    public static final int DEFAULT_KEY_EXPIRATION = 60 * 24 * 365 * 10;

    @Inject
    private ConfigurationServices configuration;

    @Inject
    private TokenServices accessTokenServices;

    @Inject
    private FirebaseTokenReader firebaseTokenReader;

    /**
     * Performs authentication and generates the token
     * @return New access token
     */
    public String performAuthAndGenerateToken(String firebaseToken)
            throws AuthenticationException {

        String secret = configuration.getString("firebase.secret", null);

        FirebaseTokenReader.FirebaseTokenData firebaseTokenData = firebaseTokenReader
                .readToken(firebaseToken, secret);

        SecurityClaims securityClaims = new SecurityClaims(AuthConstants.DEFAULT_ISSUER)
                .setEmail(firebaseTokenData.getEmail()).setSubject(firebaseTokenData.getUid())
                .setName(firebaseTokenData.getName()).setProvider(firebaseTokenData.getProvider());

        try {
            return accessTokenServices.generateToken(securityClaims, AuthConstants.JWT_TTL, getJwtSecret());
        } catch (Exception e) {
            String message = String.format("Caught exception while trying to validate external tokens. [FirebaseToken: %s], message: %s",
                    firebaseToken, e.getMessage());
            throw new AuthenticationException(500, message, e);
        }
    }

    private String getJwtSecret() {
        return configuration.getString("cocast.secret", null);
    }

}
