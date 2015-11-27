package io.cocast.auth;

/**
 * Constants used in Auth module
 */
public class AuthConstants {

    /**
     * Key on header for Firebase token
     */
    public static final String FIREBASE_TOKEN = "x-firebase-token";

    /**
     * Key on header for Google Access token
     */
    public static final String GOOGLE_ACCESS_TOKEN = "x-google-access-token";

    /**
     * Key on header to hold the access token
     */
    public static final String X_ACCESS_TOKEN = "x-access-token";

    /**
     * JWT default issuer
     */
    public static final String DEFAULT_ISSUER = "co-cast";
}
