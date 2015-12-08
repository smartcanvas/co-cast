package io.cocast.auth;

import com.google.inject.Singleton;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Reads info from Firebase token
 */
@Singleton
class FirebaseTokenReader {

    public static final String DATA = "d";

    @Inject
    private UserRepository userRepository;

    @SuppressWarnings("unchecked")
    public FirebaseTokenData readToken(String token, String secret) throws AuthenticationException {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireIssuedAt()
                .setVerificationKey(new HmacKey(secret.getBytes())).build();
        try {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            Map<String, String> dataMap = (Map<String, String>) jwtClaims.getClaimsMap().get(DATA);

            String uid = dataMap.get("uid");
            User user = userRepository.findUser(uid);

            return new FirebaseTokenData(uid, user.getProvider(), user.getEmail(), user.getName());
        } catch (IOException e) {
            throw new AuthenticationException(500, "Error reading user info on Firebase", e);
        } catch (ExecutionException e) {
            throw new AuthenticationException(500, "Error reading user info on Firebase", e);
        } catch (InvalidJwtException e) {
            throw new AuthenticationException(403, "Invalid firebase token", e);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Should have not been thrown");
        }
    }

    public static class FirebaseTokenData {
        private final String uid;
        private final String provider;
        private final String email;
        private final String name;

        public FirebaseTokenData(String uid, String provider, String email, String name) {
            this.uid = uid;
            this.provider = provider;
            this.email = email;
            this.name = name;
        }

        public String getUid() {
            return uid;
        }

        public String getProvider() {
            return provider;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }
    }
}
