package io.cocast.auth;

import io.cocast.util.DateUtils;
import io.cocast.util.Email;

import java.util.Date;

/**
 * Utility class for adding contextual user information into security tokens.
 */
public class SecurityClaims {

    private static SecurityClaims rootClaims;

    private String subject;
    private String email;
    private String provider;
    private String name;
    private String issuer;
    private Date expirationTime;
    private Date issuedAt;
    private boolean isRoot = false;
    private boolean isLiveToken = false;

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

    public String getProvider() {
        return provider;
    }

    public SecurityClaims setProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public String getName() {
        return name;
    }

    public SecurityClaims setName(String name) {
        this.name = name;
        return this;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public SecurityClaims setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;

        return this;
    }

    public String getEmail() {
        return email;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public boolean isLiveToken() {
        return isLiveToken;
    }

    public void setIsLiveToken(boolean isLiveToken) {
        this.isLiveToken = isLiveToken;
    }

    public SecurityClaims setEmail(String email) {
        this.email = email;
        return this;
    }

    public Email getId() {
        return Email.of(email);
    }

    public static SecurityClaims root() {
        if (rootClaims == null) {
            rootClaims = new SecurityClaims(AuthConstants.DEFAULT_ISSUER);
            rootClaims.setEmail(AuthConstants.ROOT_USER_EMAIL);
            rootClaims.setSubject(AuthConstants.ROOT_USER_SUBJECT);
            rootClaims.setProvider("CoCast");
            rootClaims.setName("Root");
            rootClaims.setIssuedAt(DateUtils.now());
            rootClaims.setExpirationTime(DateUtils.eternity());
            rootClaims.isRoot = true;
        }

        return rootClaims;
    }

    public static SecurityClaims liveToken(String networkMnemonic) {
        SecurityClaims liveTokenClaims = new SecurityClaims(AuthConstants.DEFAULT_ISSUER);
        liveTokenClaims.setEmail(networkMnemonic + "@cocast.io");
        liveTokenClaims.setSubject(networkMnemonic);
        liveTokenClaims.setProvider("CoCast");
        liveTokenClaims.setName(networkMnemonic);
        liveTokenClaims.setIssuedAt(DateUtils.now());
        liveTokenClaims.setExpirationTime(DateUtils.eternity());
        liveTokenClaims.isLiveToken = true;

        return liveTokenClaims;
    }

    @Override
    public String toString() {
        return "SecurityClaims{" +
                "subject='" + subject + '\'' +
                ", email='" + email + '\'' +
                ", provider='" + provider + '\'' +
                ", name='" + name + '\'' +
                ", issuer='" + issuer + '\'' +
                ", expirationTime=" + expirationTime +
                ", issuedAt=" + issuedAt +
                ", isRoot=" + isRoot +
                ", isLiveToken=" + isLiveToken +
                '}';
    }
}
