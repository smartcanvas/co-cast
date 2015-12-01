package io.cocast.auth;

/**
 * Response for authentication endpoint and process
 */
public class AuthResponse {

    private int status;
    private String accessToken;
    private String errorMessage;

    /**
     * Auth OK
     */
    public AuthResponse success(String token) {
        this.setStatus(200);
        this.setAccessToken(token);
        this.setErrorMessage(null);
        return this;
    }

    /**
     * Auth not-OK
     */
    public AuthResponse fail(AuthenticationException exc) {
        this.setStatus(exc.getStatus());
        this.setAccessToken(null);
        this.setErrorMessage(exc.getMessage());
        return this;
    }

    /**
     * Auth not-OK
     */
    public AuthResponse fail(String message, Integer status) {
        this.setStatus(status);
        this.setAccessToken(null);
        this.setErrorMessage(errorMessage);
        return this;
    }

    /**
     * Auth not-OK
     */
    public AuthResponse serverError(Exception exc) {
        this.setStatus(500);
        this.setAccessToken(null);
        this.setErrorMessage(exc.getMessage());
        return this;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
