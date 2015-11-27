package io.cocast.auth;

/**
 * Exception raised when authentication fails
 */
public class AuthenticationException extends Exception {

    private Integer status;

    /**
     * Constructor
     */
    public AuthenticationException(Integer status, String email, String uid) {
        super("Authentication failed. Email = " + email + ", uid = " + uid);
        this.status = status;
    }

    /**
     * Constructor
     */
    public AuthenticationException(Integer status, String message, Exception inner) {
        super(message, inner);
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
