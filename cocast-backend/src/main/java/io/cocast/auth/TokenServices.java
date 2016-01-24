package io.cocast.auth;

import com.google.inject.Singleton;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

import java.security.Key;
import java.util.Date;

/**
 * Exposes services for generating and validating security tokens.
 */
@Singleton
class TokenServices {

    private static final Logger logger = LogManager.getLogger(TokenServices.class.getName());

    /* Default expiration time = 10 years */
    protected static final int EXPIRATION_TIME = 60 * 24 * 365 * 10;


    /**
     * Generate a Co-cast token based on claims
     */
    public String generateToken(SecurityClaims claims, Integer ttl, String clientSecret) {
        try {
            JwtClaims jwtClaims = claims(claims, ttl);
            JsonWebSignature jws = jsonWebSignature(jwtClaims, clientSecret);
            return jws.getCompactSerialization();
        } catch (Exception e) {
            throw new SecurityException(String.format("Failed to generate JWT token: %s", e.getMessage()), e);
        }
    }

    /**
     * Generate a Co-cast token based on claims
     */
    public String generateToken(SecurityClaims claims, String clientSecret) {
        return generateToken(claims, EXPIRATION_TIME, clientSecret);
    }

    /**
     * Create the SecurityClaims based on the security token
     */
    public SecurityClaims processToClaims(String token, String clientSecret, String issuer) throws SecurityException {
        try {

            JwtConsumer jwtConsumer = buildJwtConsumer(clientSecret);
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);

            SecurityClaimsBuilder securityClaimsBuilder = SecurityClaimsBuilder.getInstance(issuer);
            SecurityClaims claims = securityClaimsBuilder.createSecurityClaims(jwtClaims, issuer);

            return claims;
        } catch (Exception e) {
            final String msg = String.format("Failed to parse JWT token: %s", e.getMessage());
            logger.error(msg, e);
            throw new SecurityException(msg, e);
        }
    }

    /**
     * Validates the token
     */
    public boolean isValid(String token, String clientSecret, String issuer) {
        try {
            SecurityClaims claims = processToClaims(token, clientSecret, issuer);
            return (claims != null);
        } catch (Exception e) {
            logger.warn(String.format("Failed to validate token: %s, message: %s", token, e.getMessage()));
            return false;
        }
    }

    /**
     * Instantiates the claims object with the attributes required for Co-cast security model
     */
    protected JwtClaims claims(SecurityClaims claims, Integer ttl) throws MalformedClaimException {
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setIssuer(claims.getIssuer());
        jwtClaims.setIssuedAtToNow();
        jwtClaims.setGeneratedJwtId();
        jwtClaims.setSubject(claims.getSubject());
        jwtClaims.setClaim(AuthConstants.JWT_FIELD_EMAIL, claims.getEmail());
        jwtClaims.setClaim(AuthConstants.JWT_FIELD_PROVIDER, claims.getProvider());
        jwtClaims.setClaim(AuthConstants.JWT_FIELD_NAME, claims.getName());
        claims.setIssuedAt(new Date(jwtClaims.getIssuedAt().getValueInMillis()));

        jwtClaims.setExpirationTimeMinutesInTheFuture(ttl);

        return jwtClaims;
    }

    protected JwtConsumer buildJwtConsumer(String clientSecret) {
        return new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setRequireIssuedAt()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setVerificationKey(getKey(clientSecret))
                .build();
    }

    protected Key getKey(String privateKey) {
        return new HmacKey(privateKey.getBytes());
    }

    /**
     * Creates a web signature
     */
    private JsonWebSignature jsonWebSignature(JwtClaims jwtClaims, String clientSecret) {
        JsonWebSignature jws = new JsonWebSignature();
        jws.setKey(getKey(clientSecret));
        jws.setHeader("typ", "JWT");
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setPayload(jwtClaims.toJson());
        return jws;
    }
}