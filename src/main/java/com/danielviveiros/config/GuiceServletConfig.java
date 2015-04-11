package com.danielviveiros.config;

import java.util.logging.Logger;

import com.danielviveiros.api.CommonResource;
import com.danielviveiros.dao.GreetingDAO;
import com.danielviveiros.dao.ObjectifyGreetingDAO;
import com.danielviveiros.resources.GuestbookServlet;
import com.danielviveiros.resources.SignGuestbookServlet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

public class GuiceServletConfig extends GuiceServletContextListener {
	
	private static Logger logger = Logger.getLogger(GuiceServletConfig.class.toString());

	@Override
	protected Injector getInjector() {
		logger.info("GuiceServletConfig.getInjector()");
		return Guice.createInjector(new CommonModule(), new ServletModule() {
			@Override
		    protected void configureServlets() {
				serve("/guestbook").with(GuestbookServlet.class);
				serve("/sign").with(SignGuestbookServlet.class);
				bind(CommonResource.class);
				bind(GreetingDAO.class).to(ObjectifyGreetingDAO.class);
		    }
			
		});
	}
}
