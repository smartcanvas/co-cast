package io.cocast.auth;

import com.google.inject.Singleton;
import io.cocast.util.AbstractResource;
import io.cocast.util.log.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Resource used to expose auth APIs
 */
@Produces("application/json")
@Consumes("application/json")
@Path(AuthConstants.AUTH_PATH)
@Singleton
public class AuthResource extends AbstractResource {

    private static final Logger logger = LogManager.getLogger(AuthResource.class.toString());

    @Inject
    private AuthServices authServices;

    @POST
    public Response auth(@Context HttpServletRequest request,
                         @NotBlank @HeaderParam(AuthConstants.FIREBASE_TOKEN) final String firebaseToken) {
        long initTime = System.currentTimeMillis();

        //validate the parameters
        if (StringUtils.isEmpty(firebaseToken)) {
            logResult("", request, "post", 0, HttpServletResponse.SC_BAD_REQUEST, initTime);
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
        }

        try {
            String token = authServices.performAuthAndGenerateToken(firebaseToken);
            logResult("OK", request, "post", 0, HttpServletResponse.SC_OK, initTime);
            return Response.ok(new AuthResponse().success(token)).build();
        } catch (AuthenticationException exc) {
            String message = exc.getMessage();
            logResult(message, request, "post", 0, exc.getStatus(), initTime);
            return Response.status(exc.getStatus()).entity(new AuthResponse().fail(exc)).build();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "post", 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return Response.serverError().entity(new AuthResponse().serverError(exc)).build();
        }
    }

    @Override
    protected String getModuleName() {
        return "auth";
    }

    @Override
    protected String getResourceName() {
        return "/";
    }

    @Override
    protected Logger getLogger() {
        return logger;
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
