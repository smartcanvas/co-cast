package io.cocast.auth;

import com.google.inject.Singleton;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;

import java.util.Date;

/**
 * Creates the security claim based on Co-Cast standards
 */
@Singleton
public class CoCastSecurityClaimsBuilder extends SecurityClaimsBuilder {

    private static Logger logger = LogManager.getLogger(CoCastSecurityClaimsBuilder.class.getName());

    @Override
    public SecurityClaims createSecurityClaims(JwtClaims jwtClaims, String issuer) throws MalformedClaimException {

        SecurityClaims claims = new SecurityClaims(issuer);
        claims.setEmail(jwtClaims.getStringClaimValue(AuthConstants.JWT_FIELD_EMAIL));
        claims.setIssuer(jwtClaims.getIssuer());
        claims.setSubject(jwtClaims.getSubject());
        claims.setIssuedAt(new Date(jwtClaims.getIssuedAt().getValueInMillis()));
        claims.setProvider(jwtClaims.getStringClaimValue(AuthConstants.JWT_FIELD_PROVIDER));
        claims.setName(jwtClaims.getStringClaimValue(AuthConstants.JWT_FIELD_NAME));

        if (jwtClaims.getExpirationTime() != null)
            claims.setExpirationTime(new Date(jwtClaims.getExpirationTime().getValueInMillis()));

        if (logger.isDebugEnabled()) {
            logger.debug("Security claims readed: " + claims);
            logger.debug("jwtClaims = " + jwtClaims);
        }

        return claims;
    }
}
