package com.ciandt.dcoder.c2.config;

import java.util.logging.Logger;

import com.ciandt.dcoder.c2.resources.ContentIngestionServlet;
import com.ciandt.dcoder.c2.resources.PeopleIngestionServiet;
import com.ciandt.dcoder.c2.resources.RefreshCacheServlet;
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
				serve("/people").with(PeopleIngestionServiet.class);
				serve("/content").with(ContentIngestionServlet.class);
				serve("/refreshcache").with(RefreshCacheServlet.class);
		    }			
		});
	}
}
