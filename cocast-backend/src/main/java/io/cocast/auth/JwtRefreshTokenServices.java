package io.cocast.auth;

import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

public class JwtRefreshTokenServices extends AbstractJwtTokenServices {

    @Override
    protected JwtConsumer buildJwtConsumer(String clientSecret) {
        return new JwtConsumerBuilder()
                .setRequireIssuedAt()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setVerificationKey(getKey(clientSecret))
                .build();
    }

}
