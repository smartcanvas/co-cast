package com.ciandt.dcoder.c2.config;

import java.util.HashMap;
import java.util.Map;

import com.ciandt.dcoder.c2.api.CommonResource;
import com.ciandt.dcoder.c2.dao.CastViewObjectDAO;
import com.ciandt.dcoder.c2.resources.TaskQueueResource;
import com.ciandt.dcoder.c2.service.CardServices;
import com.ciandt.dcoder.c2.service.CastViewDataServices;
import com.ciandt.dcoder.c2.service.CastViewObjectCache;
import com.ciandt.dcoder.c2.service.CastedCastView;
import com.ciandt.dcoder.c2.service.GooglePlusConnector;
import com.ciandt.dcoder.c2.service.PeopleServices;
import com.ciandt.dcoder.c2.service.WhatsHotCastView;
import com.ciandt.dcoder.c2.service.WhatsNewCastView;
import com.ciandt.dcoder.c2.util.APIServices;
import com.ciandt.dcoder.c2.util.GooglePlusServices;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class CommonModule extends ServletModule {

	@Override
	protected void configureServlets() {
		
		Map<String, String> initParams = new HashMap<String, String>();
		initParams.put("com.sun.jersey.config.feature.Trace","true");
        initParams.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
		
		bind(CommonResource.class);
		bind(CardServices.class);
		bind(PeopleServices.class);
		bind(GooglePlusServices.class);
		bind(GooglePlusConnector.class);
		bind(APIServices.class);
		bind(CastViewDataServices.class);
		bind(CastViewObjectDAO.class);
		bind(TaskQueueResource.class);
		bind(CastViewObjectCache.class);
		bind(WhatsHotCastView.class);
		bind(WhatsNewCastView.class);
		bind(CastedCastView.class);
		
		filter("/api/*").through(GuiceContainer.class, initParams);
		filter("/tasks/*").through(GuiceContainer.class, initParams);
	}
}
