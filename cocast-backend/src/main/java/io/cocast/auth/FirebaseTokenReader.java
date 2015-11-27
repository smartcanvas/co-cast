package io.cocast.auth;

import com.google.inject.Singleton;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

import java.util.Map;

/**
 * Reads info from Firebase token
 */
@Singleton
public class FirebaseTokenReader {

    public static final String DATA = "d";

    @SuppressWarnings("unchecked")
    public FirebaseTokenData readToken(String token, String secret) throws AuthenticationException {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireIssuedAt()
                .setVerificationKey(new HmacKey(secret.getBytes())).build();
        try {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            Map<String, String> dataMap = (Map<String, String>) jwtClaims.getClaimsMap().get(DATA);
            return new FirebaseTokenData(dataMap.get("uid"), dataMap.get("provider"));
        } catch (InvalidJwtException e) {
            throw new AuthenticationException(403, "Invalid firebase token", e);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Should have not been thrown");
        }
    }

    public static class FirebaseTokenData {
        private final String uid;
        private final String provider;

        public FirebaseTokenData(String uid, String provider) {
            this.uid = uid;
            this.provider = provider;
        }

        public String getUid() {
            return uid;
        }

        public String getProvider() {
            return provider;
        }
    }
}
