package io.cocast.auth;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;

/**
 * Holds the security context of any request
 */
public class SecurityContext {

    private static final Logger LOGGER = LogManager.getLogger(SecurityContext.class.getName());

    private static ThreadLocal<SecurityContext> securityThreadLocal = new ThreadLocal<SecurityContext>();

    private SecurityClaims claims;

    public static void set(final SecurityContext user) {
        securityThreadLocal.set(user);
    }

    public static SecurityContext get() {
        return securityThreadLocal.get();
    }

    public SecurityContext(SecurityClaims claims) throws AuthenticationException {
        this(claims, AuthConstants.DEFAULT_ISSUER);
    }

    public SecurityContext(SecurityClaims claims, String issuer) throws AuthenticationException {
        Preconditions.checkNotNull(claims, "claims is mandatory");
        this.claims = claims;
        this.validate(claims, issuer);
    }

    public String userIdentification() {
        return claims.getSubject();
    }

    public String issuer() {
        return claims.getIssuer();
    }

    /**
     * Validate the security claims
     */
    private void validate(SecurityClaims claims, String issuer) throws AuthenticationException {

        //issuer
        if (!issuer.equals(claims.getIssuer())) {
            throw new AuthenticationException(HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid issuer: " + claims.getIssuer());
        }

        //subject
        if (StringUtils.isEmpty(claims.getSubject())) {
            throw new AuthenticationException(HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid subject: " + claims.getSubject());
        }

        //expiration
        if ((claims.getExpirationTime() != null) &&
                (claims.getExpirationTime().getTime() - System.currentTimeMillis() < 0)) {
            throw new AuthenticationException(HttpServletResponse.SC_UNAUTHORIZED,
                    "Token expired in " + claims.getExpirationTime());
        }

        //email
        if (claims.getEmail() == null) {
            throw new AuthenticationException(HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid email: " + claims);
        }
    }

    static void unset() {
        securityThreadLocal.remove();
    }

}
