package io.cocast.auth;

import com.google.common.base.MoreObjects;
import io.cocast.util.Email;

import java.util.Date;

/**
 * Utility class for adding contextual user information into security tokens.
 *
 * @author iluz
 */
public class SecurityClaims {

    private String subject;
    private String email;
    private String issuer;
    private Date expirationTime;
    private Date issuedAt;

    public String getSubject() {
        return subject;
    }

    public SecurityClaims setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public SecurityClaims(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuer() {
        return issuer;
    }

    public SecurityClaims setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public SecurityClaims setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;

        return this;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public SecurityClaims setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;

        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("email", email)
                .add("issuer", issuer)
                .add("expirationTime", expirationTime)
                .add("issuedAt", issuedAt)
                .toString();
    }

    public String getEmail() {
        return email;
    }

    public SecurityClaims setEmail(String email) {
        this.email = email;
        return this;
    }

    public Email getId() {
        return Email.of(email);
    }

}
