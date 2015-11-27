package io.cocast.web;

import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet that deals with all Co-cast routing. Main URL form:
 * <p/>
 * http://<host>/<network>/<station>
 */
@Singleton
public class RoutingServlet extends HttpServlet {

    /**
     * Page to select the network
     */
    private static final String LOGIN_PATH = "/WEB-INF/html/selectNetwork.html";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(request.getContextPath());
    }
}
