package io.cocast.auth;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.base.Preconditions;
import com.google.common.net.MediaType;
import io.cocast.admin.ConfigurationServices;
import io.cocast.util.APIResponse;
import io.cocast.util.log.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that applies the security to the endpoints
 */
public class ApiTokenSecurityFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(ApiTokenSecurityFilter.class.getName());

    private final TokenServices tokenServices;

    private final ConfigurationServices configuration;

    private final ObjectWriter objectWriter;

    @Inject
    public ApiTokenSecurityFilter(TokenServices tokenServices,
                                  ConfigurationServices configuration,
                                  ObjectWriter objectWriter) {
        super();
        this.tokenServices = Preconditions.checkNotNull(tokenServices, "tokenServices is mandatory");
        this.configuration = Preconditions.checkNotNull(configuration, "configuration is mandatory");
        this.objectWriter = objectWriter;
    }

    /**
     * Checks the security of this request
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        //Auth endpoint doesn't require authentication
        String contextPath = req.getRequestURI();
        if (contextPath.equals(AuthConstants.AUTH_PATH)) {
            //go ahead
            filterChain.doFilter(request, response);
            return;
        }


        String secret = configuration.getString("cocast.secret", null);
        try {
            //check if we have the secret in the header. if so, the security context will be defined as 'root'
            //and this call will be made on its behalf
            String xSecret = req.getHeader(AuthConstants.X_ROOT_TOKEN);
            if (!StringUtils.isEmpty(xSecret)) {
                if (xSecret.equals(secret)) {
                    SecurityContext.set(new SecurityContext(SecurityClaims.root(), AuthConstants.DEFAULT_ISSUER));

                    //execute the action
                    if (!StringUtils.isEmpty(SecurityContext.get().userIdentification())) {
                        filterChain.doFilter(request, response);
                        SecurityContext.unset();
                    }
                }
            } else {
                //if the root token is not present, the auth token must be
                String authToken = authToken(req);
                String issuer = getIssuer(req);
                String jwtSecret = getJWTSecret(issuer);

                if (!Strings.isNullOrEmpty(authToken)) {
                    SecurityContext.set(new SecurityContext(tokenServices.processToClaims(authToken, jwtSecret, issuer), issuer));

                    //execute the action
                    if (!StringUtils.isEmpty(SecurityContext.get().userIdentification())) {
                        filterChain.doFilter(request, response);
                        SecurityContext.unset();
                    }
                } else {
                    unauthorized(resp, "Auth token is missing");
                }
            }
        } catch (SecurityException se) {
            String logMessage = "Unable to create security context. Auth token or root token missing or expired?";
            logger.error(logMessage, se);
            unauthorized(resp, logMessage);
        } catch (AuthenticationException ae) {
            logger.error("Error authenticating");
            unauthorized(resp, ae.getMessage());
        } catch (Exception exc) {
            String message = "Generic error while authenticating";
            LogUtils.fatal(logger, message, exc);
            response.setContentType(MediaType.JSON_UTF_8.toString());
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            APIResponse apiResponse = APIResponse.authFail(message);
            objectWriter.writeValue(response.getWriter(), apiResponse);
        }
    }

    /**
     * Creates the response and writes it on response object
     */
    private void unauthorized(HttpServletResponse response, String logMessage) throws IOException {
        response.setContentType(MediaType.JSON_UTF_8.toString());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        APIResponse apiResponse = APIResponse.authFail(logMessage);
        objectWriter.writeValue(response.getWriter(), apiResponse);
    }

    /**
     * Retrieves the access token from the header
     */
    private String authToken(HttpServletRequest req) {
        return req.getHeader(AuthConstants.X_ACCESS_TOKEN);
    }

    /**
     * Retrieves the issuer
     */
    private String getIssuer(HttpServletRequest req) {
        String issuer = req.getHeader(AuthConstants.X_ISSUER);
        if (StringUtils.isEmpty(issuer)) {
            issuer = AuthConstants.DEFAULT_ISSUER;
        }

        return issuer;
    }

    /**
     * Retrives the JWT Secret to be used to process the claims
     */
    private String getJWTSecret(String issuer) {
        if (AuthConstants.DEFAULT_ISSUER.equals(issuer)) { //Co-cast
            return configuration.getString("cocast.secret", null);
        } else {
            //SmartCanvas (only these two are supported right now
            return configuration.getString("smartcanvas.secret", null);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}