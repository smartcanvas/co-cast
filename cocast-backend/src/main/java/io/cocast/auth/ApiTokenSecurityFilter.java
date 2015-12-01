package io.cocast.auth;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.base.Preconditions;
import com.google.common.net.MediaType;
import com.google.inject.name.Named;
import io.cocast.util.APIResponse;
import io.cocast.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that applies the security to the endpoints
 */
public class ApiTokenSecurityFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ApiTokenSecurityFilter.class.getName());

    private final TokenServices tokenServices;

    private final Configuration configuration;

    private final ObjectWriter objectWriter;

    @Inject
    public ApiTokenSecurityFilter(@Named("accessToken") TokenServices tokenServices, Configuration configuration,
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

        System.out.println("Entrando no APITokenSecurityFilter");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String authToken = authToken(req);

        if (!Strings.isNullOrEmpty(authToken)) {
            try {

                String secret = configuration.getString("jwt.secret", null);
                logger.debug("Creating the security context.");
                SecurityContext.set(new SecurityContext(tokenServices.processToClaims(authToken, secret)));
                logger.debug("Provided access token is valid. Proceeding with filter chain.");

                filterChain.doFilter(request, response);

                SecurityContext.unset();
            } catch (SecurityException se) {
                String logMessage = "Provided auth token is invalid: " + authToken;
                logger.error(logMessage, se);
                unauthorized(resp, logMessage);
            } catch (AuthenticationException ae) {
                logger.error("Error authenticating: ", ae);
                unauthorized(resp, ae.getMessage());
            }
        } else {
            logger.error("Auth token is missing");
            unauthorized(resp, "Auth token is missing");
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