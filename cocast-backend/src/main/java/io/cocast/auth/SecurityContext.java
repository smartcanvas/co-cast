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

    static void unset() {
        securityThreadLocal.remove();
    }

    public static void set(final SecurityContext user) {
        securityThreadLocal.set(user);
    }

    public static SecurityContext get() {
        return securityThreadLocal.get();
    }

    private SecurityClaims claims;

    public SecurityContext(SecurityClaims claims) throws AuthenticationException {
        Preconditions.checkNotNull(claims, "claims is mandatory");
        this.claims = claims;
        this.validate(claims);
    }

    public String userIdentification() {
        return claims.getSubject();
    }

    /**
     * Validate the security claims
     */
    private void validate(SecurityClaims claims) throws AuthenticationException {

        //issuer
        if (!AuthConstants.DEFAULT_ISSUER.equals(claims.getIssuer())) {
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

}
