package com.danielviveiros.resources;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.danielviveiros.dao.GreetingDAO;
import com.danielviveiros.entity.Greeting;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SignGuestbookServlet extends HttpServlet {
    
	private static final long serialVersionUID = 2800778962458456034L;
	
	@Inject
	private Logger log;
	
	@Inject
	private GreetingDAO dao;

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String content = req.getParameter("content");
        if (content == null) {
            content = "(No greeting)";
        }
        if (user != null) {
            log.info("Greeting posted by user " + user.getNickname() + ": " + content);
        } else {
            log.info("Greeting posted anonymously: " + content);
        }
        
        Date date = new Date();
        Greeting greeting = new Greeting(user, content, date);
        dao.insert(greeting);
        
        resp.sendRedirect("/guestbook");
    }
}