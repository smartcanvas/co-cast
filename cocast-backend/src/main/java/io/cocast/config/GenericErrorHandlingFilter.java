package io.cocast.config;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.net.MediaType;
import io.cocast.util.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Error handling standard mechanism
 */
public class GenericErrorHandlingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(GenericErrorHandlingFilter.class.getName());

    private ObjectWriter objectWriter;

    @Inject
    public GenericErrorHandlingFilter(ObjectWriter objectWriter) {
        super();
        this.objectWriter = objectWriter;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        try {

            filterChain.doFilter(servletRequest, servletResponse);

        } catch (Throwable e) {

            logger.error("[GENERIC ERROR] - " + e.getMessage(), e);
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setContentType(MediaType.JSON_UTF_8.toString());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            APIResponse apiResponse = APIResponse.serverError(e.getMessage());
            if (request.getParameter("debug") != null) {
                StringWriter errorStackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(errorStackTrace));
                apiResponse.setDeveloperMessage(errorStackTrace.toString());
            }

            objectWriter.writeValue(response.getWriter(), apiResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
