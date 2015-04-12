package com.ciandt.dcoder.c2.config;

import java.util.HashMap;
import java.util.Map;

import com.ciandt.dcoder.c2.api.CommonResource;
import com.ciandt.dcoder.c2.dao.CastViewObjectDAO;
import com.ciandt.dcoder.c2.resources.TaskQueueResource;
import com.ciandt.dcoder.c2.service.CardServices;
import com.ciandt.dcoder.c2.service.CastViewDataServices;
import com.ciandt.dcoder.c2.service.GooglePlusConnector;
import com.ciandt.dcoder.c2.service.PeopleServices;
import com.ciandt.dcoder.c2.util.APIServices;
import com.ciandt.dcoder.c2.util.GooglePlusServices;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class CommonModule extends ServletModule {

	@Override
	protected void configureServlets() {
		
		Map<String, String> initParams = new HashMap<String, String>();
		
		bind(CommonResource.class);
		bind(CardServices.class);
		bind(PeopleServices.class);
		bind(GooglePlusServices.class);
		bind(GooglePlusConnector.class);
		bind(APIServices.class);
		bind(CastViewDataServices.class);
		bind(CastViewObjectDAO.class);
		bind(TaskQueueResource.class);
		
		filter("/api/*").through(GuiceContainer.class, initParams);
		filter("/tasks/*").through(GuiceContainer.class, initParams);
	}
}
