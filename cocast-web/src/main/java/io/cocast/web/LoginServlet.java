package io.cocast.web;

import com.google.inject.Singleton;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles login
 */
@Singleton
public class LoginServlet extends HttpServlet {

    /**
     * Path to login.html
     */
    private static final String LOGIN_PATH = "/WEB-INF/html/login.html";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher dispatcher = request.getRequestDispatcher(LOGIN_PATH);
        dispatcher.forward(request, response);
    }
}
