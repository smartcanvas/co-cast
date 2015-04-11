package com.ciandt.dcoder.c2.config;

import java.util.logging.Logger;

import com.ciandt.dcoder.c2.api.CommonResource;
import com.ciandt.dcoder.c2.dao.GreetingDAO;
import com.ciandt.dcoder.c2.dao.ObjectifyGreetingDAO;
import com.ciandt.dcoder.c2.resources.GuestbookServlet;
import com.ciandt.dcoder.c2.resources.SignGuestbookServlet;
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
