package com.ciandt.dcoder.c2.config;

import com.ciandt.dcoder.c2.api.CastViewAPIResource;
import com.ciandt.dcoder.c2.dao.CastViewObjectDAO;
import com.ciandt.dcoder.c2.resources.ContentResource;
import com.ciandt.dcoder.c2.resources.PeopleResource;
import com.ciandt.dcoder.c2.resources.TaskQueueResource;
import com.ciandt.dcoder.c2.service.CardServices;
import com.ciandt.dcoder.c2.service.CastViewServices;
import com.ciandt.dcoder.c2.service.CastViewObjectCache;
import com.ciandt.dcoder.c2.service.CastedCastView;
import com.ciandt.dcoder.c2.service.GooglePlusConnector;
import com.ciandt.dcoder.c2.service.PeopleServices;
import com.ciandt.dcoder.c2.service.WhatsHotCastView;
import com.ciandt.dcoder.c2.service.WhatsNewCastView;
import com.ciandt.dcoder.c2.util.APIServices;
import com.ciandt.dcoder.c2.util.GooglePlusServices;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class C2Module extends ServletModule {

	@Override
	protected void configureServlets() {
		
		//third party bindings
		bindThirdPartyClasses();
        
        
		bind(CastViewAPIResource.class);
		bind(PeopleResource.class);
		bind(ContentResource.class);
		bind(CardServices.class);
		bind(PeopleServices.class);
		bind(GooglePlusServices.class);
		bind(GooglePlusConnector.class);
		bind(APIServices.class);
		bind(CastViewServices.class);
		bind(CastViewObjectDAO.class);
		bind(TaskQueueResource.class);
		bind(CastViewObjectCache.class);
		bind(WhatsHotCastView.class);
		bind(WhatsNewCastView.class);
		bind(CastedCastView.class);
	}
	
    /*
     * Bind third party classes
     */
    private void bindThirdPartyClasses() {
        bind(GuiceContainer.class);
        bind(ObjectifyFilter.class).in(Scopes.SINGLETON);
    }

}
