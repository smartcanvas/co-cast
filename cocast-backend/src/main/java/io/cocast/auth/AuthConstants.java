package io.cocast.auth;

/**
 * Constants used in Auth module
 */
public class AuthConstants {

    /**
     * Path for Auth API
     */
    public static final String AUTH_PATH = "/api/auth/v1";

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
     * Key on header to hold the root token
     */
    public static final String X_ROOT_TOKEN = "x-root-token";

    /**
     * Root user
     */
    public static final String ROOT_USER = "root@cocast.io";

    /**
     * JWT default issuer
     */
    public static final String DEFAULT_ISSUER = "co-cast";

    /**
     * JWT TTL
     */
    public static final Integer JWT_TTL = 5256000;

}
