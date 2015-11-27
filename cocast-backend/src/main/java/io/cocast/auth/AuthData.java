package io.cocast.auth;

import io.cocast.util.Email;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class to hold authentication data from an external resource
 */
public class AuthData {

    private final Email email;
    private final String provider;
    private final String uid;

    public AuthData(String email, String provider, String uid) {
        this.provider = provider;
        this.uid = uid;
        this.email = Email.of(checkNotNull(email));
    }

    public Email getEmail() {
        return email;
    }

    public String getProvider() {
        return provider;
    }

    public String getUid() {
        return uid;
    }

    public String getDomain() {
        return email.getDomain();
    }
}
