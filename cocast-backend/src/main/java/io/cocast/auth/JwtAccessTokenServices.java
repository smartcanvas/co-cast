package io.cocast.auth;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

public class JwtAccessTokenServices extends AbstractJwtTokenServices {

    @Override
    protected JwtClaims claims(SecurityClaims claims, Integer ttl) throws MalformedClaimException {
        JwtClaims jwtClaims = super.claims(claims, ttl);
        jwtClaims.setExpirationTimeMinutesInTheFuture(ttl);
        return jwtClaims;
    }

    @Override
    protected JwtConsumer buildJwtConsumer(String clientSecret) {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireExpirationTime().setRequireIssuedAt()
                .setAllowedClockSkewInSeconds(30).setRequireSubject().setVerificationKey(getKey(clientSecret)).build();
        return jwtConsumer;
    }

}
