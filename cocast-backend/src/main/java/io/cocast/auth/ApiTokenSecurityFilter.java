package io.cocast.auth;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.base.Preconditions;
import com.google.common.net.MediaType;
import com.google.inject.name.Named;
import io.cocast.admin.ConfigurationServices;
import io.cocast.util.APIResponse;
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
    public ApiTokenSecurityFilter(@Named("accessToken") TokenServices tokenServices,
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
                    logger.debug("Defining security context for root access");
                    SecurityContext.set(new SecurityContext(SecurityClaims.root()));
                }
            } else {
                //if the root token is not present, the auth token must be
                String authToken = authToken(req);
                if (!Strings.isNullOrEmpty(authToken)) {
                    logger.debug("Creating the security context.");
                    SecurityContext.set(new SecurityContext(tokenServices.processToClaims(authToken, secret)));
                    logger.debug("Provided access token is valid. Proceeding with filter chain.");

                } else {
                    logger.error("Auth token is missing");
                    unauthorized(resp, "Auth token is missing");
                }
            }

            //execute the action
            if (!StringUtils.isEmpty(SecurityContext.get().userIdentification())) {
                filterChain.doFilter(request, response);
                SecurityContext.unset();
            }
        } catch (SecurityException se) {
            String logMessage = "Unable to create security context. Auth token or root token missing?";
            logger.error(logMessage, se);
            unauthorized(resp, logMessage);
        } catch (AuthenticationException ae) {
            logger.error("Error authenticating: ", ae);
            unauthorized(resp, ae.getMessage());
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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}