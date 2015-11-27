package io.cocast.auth;

/**
 * Exposes services for generating and validating security tokens.
 */
public interface TokenServices {

    /**
     * Generates either an auth or refresh token based on the given SecurityClaims.
     *
     * @param claims
     * @return
     */
    public String generateToken(SecurityClaims claims, String clientSecret);

    /**
     * Generates either an auth or refresh token based on the given SecurityClaims.
     *
     * @param claims
     * @param ttl
     * @param clientSecret
     * @return
     */
    public String generateToken(SecurityClaims claims, Integer ttl, String clientSecret);

    /**
     * Creates a SecurityClaims instance containing information regarding the user to which the token was issued to.
     *
     * @param token
     * @param clientSecret
     * @return
     * @throws SecurityException
     */
    public SecurityClaims processToClaims(String token, String clientSecret) throws SecurityException;

    /**
     * Checks if auth token is valid. Tokens may not be valid, for example, if they have expired or have been tampered.
     *
     * @param token
     * @param clientSecret
     * @return
     */
    public boolean isValid(String token, String clientSecret);

}