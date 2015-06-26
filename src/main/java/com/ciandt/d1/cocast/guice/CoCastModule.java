package com.ciandt.d1.cocast.guice;

import com.ciandt.d1.cocast.card.api.ContentResource;
import com.ciandt.d1.cocast.castview.CardServices;
import com.ciandt.d1.cocast.castview.CastViewObjectCache;
import com.ciandt.d1.cocast.castview.CastViewObjectDAO;
import com.ciandt.d1.cocast.castview.CastViewServices;
import com.ciandt.d1.cocast.castview.CastedCastView;
import com.ciandt.d1.cocast.castview.WhatsHotCastView;
import com.ciandt.d1.cocast.castview.WhatsNewCastView;
import com.ciandt.d1.cocast.castview.api.CastViewAPIResource;
import com.ciandt.d1.cocast.configuration.ConfigurationDAO;
import com.ciandt.d1.cocast.util.APIServices;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class CoCastModule extends ServletModule {

	@Override
	protected void configureServlets() {
		
		//Third party bindings
		bindThirdPartyClasses();
		
		//DAOs
		bind(CastViewObjectDAO.class);
		bind(ConfigurationDAO.class);
        
		//Cast View
	    bind(CastViewServices.class);
		bind(CastViewAPIResource.class);
		bind(ContentResource.class);
	    bind(CastViewObjectCache.class);
	    bind(WhatsHotCastView.class);
        bind(WhatsNewCastView.class);
        bind(CastedCastView.class);
		
		//Card
		bind(CardServices.class);
		
		//Util
		bind(APIServices.class);
	}
	
    /*
     * Bind third party classes
     */
    private void bindThirdPartyClasses() {
        bind(GuiceContainer.class);
        bind(ObjectifyFilter.class).in(Scopes.SINGLETON);
    }

}
