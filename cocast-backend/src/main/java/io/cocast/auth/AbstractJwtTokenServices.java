package io.cocast.auth;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;

/**
 * Abstract class for JWT token services
 */
public class AbstractJwtTokenServices implements TokenServices {

    private static final Logger logger = LoggerFactory.getLogger(AbstractJwtTokenServices.class.getName());

    static final String FIELD_EMAIL = "email";

    /* Default expiration time = 10 years */
    protected static final int EXPIRATION_TIME = 60 * 24 * 365 * 10;

    @Override
    public String generateToken(SecurityClaims claims, Integer ttl, String clientSecret) {
        try {
            JwtClaims jwtClaims = claims(claims, ttl);
            JsonWebSignature jws = jsonWebSignature(jwtClaims, clientSecret);
            return jws.getCompactSerialization();
        } catch (Exception e) {
            throw new SecurityException(String.format("Failed to generate JWT token: %s", e.getMessage()), e);
        }
    }

    @Override
    public String generateToken(SecurityClaims claims, String clientSecret) {
        return generateToken(claims, EXPIRATION_TIME, clientSecret);
    }
    private JsonWebSignature jsonWebSignature(JwtClaims jwtClaims, String clientSecret) {
        JsonWebSignature jws = new JsonWebSignature();
        jws.setKey(getKey(clientSecret));
        jws.setHeader("typ", "JWT");
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setPayload(jwtClaims.toJson());
        return jws;
    }

    protected JwtClaims claims(SecurityClaims claims, Integer ttl) throws MalformedClaimException {
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setIssuer(claims.getIssuer());
        jwtClaims.setIssuedAtToNow();
        jwtClaims.setGeneratedJwtId();
        jwtClaims.setSubject(claims.getSubject());
        jwtClaims.setClaim(FIELD_EMAIL, claims.getEmail());
        claims.setIssuedAt(new Date(jwtClaims.getIssuedAt().getValueInMillis()));

        return jwtClaims;
    }

    @Override
    public SecurityClaims processToClaims(String token, String clientSecret) throws SecurityException {
        try {
            JwtConsumer jwtConsumer = buildJwtConsumer(clientSecret);
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);

            SecurityClaims claims = new SecurityClaims(jwtClaims.getIssuer());
            claims.setEmail(jwtClaims.getStringClaimValue(FIELD_EMAIL));
            claims.setIssuer(jwtClaims.getIssuer());
            claims.setSubject(jwtClaims.getSubject());
            claims.setIssuedAt(new Date(jwtClaims.getIssuedAt().getValueInMillis()));

            if (jwtClaims.getExpirationTime() != null)
                claims.setExpirationTime(new Date(jwtClaims.getExpirationTime().getValueInMillis()));

            logger.debug("Security claims readed: " + claims);

            return claims;
        } catch (Exception e) {
            final String msg = String.format("Failed to parse JWT token: %s", e.getMessage());
            logger.error(msg, e);
            throw new SecurityException(msg, e);
        }
    }

    protected JwtConsumer buildJwtConsumer(String clientSecret) {
        return new JwtConsumerBuilder()
                .setRequireIssuedAt()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setVerificationKey(getKey(clientSecret))
                .build();
    }

    protected Key getKey(String privateKey) {
        return new HmacKey(privateKey.getBytes());
    }

    @Override
    public boolean isValid(String token, String clientSecret) {
        logger.debug("Starting token validation {}" + token);
        try {
            SecurityClaims claims = processToClaims(token, clientSecret);
            return (claims != null);
        } catch (Exception e) {
            logger.warn(String.format("Failed to validate token: %s, message: %s", token, e.getMessage()));
            return false;
        }
    }

}