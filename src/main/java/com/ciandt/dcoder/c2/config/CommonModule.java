package com.ciandt.dcoder.c2.config;

import java.util.HashMap;
import java.util.Map;

import com.ciandt.dcoder.c2.api.CommonResource;
import com.ciandt.dcoder.c2.dao.GreetingDAO;
import com.ciandt.dcoder.c2.dao.ObjectifyGreetingDAO;
import com.ciandt.dcoder.c2.service.CardServices;
import com.ciandt.dcoder.c2.service.GooglePlusConnector;
import com.ciandt.dcoder.c2.service.PeopleServices;
import com.ciandt.dcoder.c2.util.APIServices;
import com.ciandt.dcoder.c2.util.ConfigurationServices;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class CommonModule extends ServletModule {

	@Override
	protected void configureServlets() {
		
		Map<String, String> initParams = new HashMap<String, String>();
		
		bind(CommonResource.class);
		bind(CardServices.class);
		bind(PeopleServices.class);
		bind(ConfigurationServices.class);
		bind(GooglePlusConnector.class);
		bind(APIServices.class);
		bind(GreetingDAO.class).to(ObjectifyGreetingDAO.class);
		
		filter("/api/*").through(GuiceContainer.class, initParams);
	}
}
