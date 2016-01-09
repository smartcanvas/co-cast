package io.cocast.auth;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.cocast.config.BasicBackendModule;
import org.jose4j.jwt.JwtClaims;

/**
 * Creates a security claim based on the issuer
 */
public abstract class SecurityClaimsBuilder {

    private static CoCastSecurityClaimsBuilder coCastSecurityClaimsBuilder;
    private static SmartCanvasV1SecurityClaimsBuilder smartCanvasV1SecurityClaimsBuilder;

    /**
     * Gets an instance of this class
     */
    public static SecurityClaimsBuilder getInstance(String issuer) {

        Injector injector = Guice.createInjector(new BasicBackendModule());

        if (AuthConstants.DEFAULT_ISSUER.equals(issuer)) {
            if (coCastSecurityClaimsBuilder == null) {
                coCastSecurityClaimsBuilder = injector.getInstance(CoCastSecurityClaimsBuilder.class);
            }
            return coCastSecurityClaimsBuilder;
        } else {
            if (smartCanvasV1SecurityClaimsBuilder == null) {
                smartCanvasV1SecurityClaimsBuilder = injector.getInstance(SmartCanvasV1SecurityClaimsBuilder.class);
            }
            return smartCanvasV1SecurityClaimsBuilder;
        }
    }

    /**
     * Create the security claims object
     */
    public abstract SecurityClaims createSecurityClaims(JwtClaims jwtClaims, String issuer) throws Exception;

}
