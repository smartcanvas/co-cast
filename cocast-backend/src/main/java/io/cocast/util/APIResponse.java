package io.cocast.util;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

/**
 * Object to hold API response
 */
public class APIResponse {

    private Integer status;
    private String message;
    private String developerMessage;

    /**
     * Constructor
     */
    public APIResponse() {

    }

    /**
     * Constructor
     */
    public APIResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Create an error response
     *
     * @return Object with an error response
     */
    public static APIResponse serverError(String message) {
        return new APIResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    /**
     * Create a bad request response
     *
     * @return Object with an error response
     */
    public static APIResponse badRequest(String message) {
        return new APIResponse(HttpServletResponse.SC_BAD_REQUEST, message);
    }

    /**
     * Create an auth fail response
     *
     * @return Object with an auth fail response
     */
    public static APIResponse authFail(String message) {
        return new APIResponse(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    /**
     * Entity created
     */
    public static APIResponse created(String message) {
        return new APIResponse(HttpServletResponse.SC_CREATED, message);
    }

    /**
     * Co-cast exception happened
     */
    public static APIResponse fromException(CoCastCallException exc) {
        return new APIResponse(exc.getStatus(), exc.getMessage());
    }

    /**
     * Creates the response based on the internal status
     *
     * @return Response object
     */
    @JsonIgnore
    public Response getResponse() {
        if (getStatus() == null) {
            return null;
        }

        return Response.status(getStatus()).entity(this).build();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }
}
