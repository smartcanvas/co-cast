package io.cocast.auth;

/**
 * Constants used in Auth module
 */
interface AuthConstants {

    /**
     * Path for Auth API
     */
    public static final String AUTH_PATH = "/api/auth/v1";

    /**
     * Key on header for Firebase token
     */
    public static final String FIREBASE_TOKEN = "x-firebase-token";

    /**
     * Key on header to hold the access token
     */
    public static final String X_ACCESS_TOKEN = "x-access-token";

    /**
     * Key on header to hold the root token
     */
    public static final String X_ROOT_TOKEN = "x-root-token";

    /**
     * Root user email
     */
    public static final String ROOT_USER_EMAIL = "root@cocast.io";

    /**
     * Root user subject
     */
    public static final String ROOT_USER_SUBJECT = "root";

    /**
     * JWT default issuer
     */
    public static final String DEFAULT_ISSUER = "co-cast";

    /**
     * JWT TTL
     */
    public static final Integer JWT_TTL = 5256000;

}
