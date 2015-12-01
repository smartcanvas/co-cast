package io.cocast.auth;

import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Resource used to expose auth APIs
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/auth/v1")
@Singleton
public class AuthResource {

    private static final Logger logger = LoggerFactory.getLogger(AuthResource.class.toString());

    @Inject
    private AuthServices authServices;

    @POST
    public Response auth(@NotBlank @HeaderParam(AuthConstants.FIREBASE_TOKEN) final String firebaseToken,
                         @NotBlank @HeaderParam(AuthConstants.GOOGLE_ACCESS_TOKEN) final String googleAccessToken) {

        //validate the parameters
        if (StringUtils.isEmpty(firebaseToken) || StringUtils.isEmpty(googleAccessToken)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            String token = authServices.performAuthAndGenerateToken(firebaseToken, googleAccessToken);
            return Response.ok(new AuthResponse().success(token)).build();
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed: " + e.getMessage() + " [" + e.getStatus() + "]");
            return Response.status(e.getStatus()).entity(new AuthResponse().fail(e)).build();
        } catch (Exception e) {
            logger.error("Error creating security token", e);
            return Response.serverError().entity(new AuthResponse().serverError(e)).build();
        }
    }


}
