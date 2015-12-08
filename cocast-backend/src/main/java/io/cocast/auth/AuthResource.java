package io.cocast.auth;

import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Resource used to expose auth APIs
 */
@Produces("application/json")
@Consumes("application/json")
@Path(AuthConstants.AUTH_PATH)
@Singleton
public class AuthResource {

    private static final Logger logger = LogManager.getLogger(AuthResource.class.toString());

    @Inject
    private AuthServices authServices;

    @POST
    public Response auth(@NotBlank @HeaderParam(AuthConstants.FIREBASE_TOKEN) final String firebaseToken) {

        //validate the parameters
        if (StringUtils.isEmpty(firebaseToken)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            String token = authServices.performAuthAndGenerateToken(firebaseToken);
            return Response.ok(new AuthResponse().success(token)).build();
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed: " + e.getMessage() + " [" + e.getStatus() + "]");
            return Response.status(e.getStatus()).entity(new AuthResponse().fail(e)).build();
        } catch (Exception e) {
            logger.error("Error creating security token", e);
            return Response.serverError().entity(new AuthResponse().serverError(e)).build();
        }
    }


    private class AuthResponse {

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


}
