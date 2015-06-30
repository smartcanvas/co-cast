package com.ciandt.d1.cocast.guice;

import com.ciandt.d1.cocast.castview.CastViewDAO;
import com.ciandt.d1.cocast.castview.CastViewObjectCache;
import com.ciandt.d1.cocast.castview.CastViewObjectDAO;
import com.ciandt.d1.cocast.castview.CastViewServices;
import com.ciandt.d1.cocast.castview.CastViewStrategy;
import com.ciandt.d1.cocast.castview.DefaultCastViewStrategy;
import com.ciandt.d1.cocast.castview.api.CastViewAPIResource;
import com.ciandt.d1.cocast.configuration.ConfigurationDAO;
import com.ciandt.d1.cocast.configuration.ConfigurationServices;
import com.ciandt.d1.cocast.configuration.api.ConfigurationResource;
import com.ciandt.d1.cocast.content.CardServices;
import com.ciandt.d1.cocast.content.api.ContentResource;
import com.ciandt.d1.cocast.util.APIServices;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.google.inject.multibindings.MapBinder;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class CoCastModule extends ServletModule {

	@Override
	protected void configureServlets() {
		
		//Third party bindings
		bindThirdPartyClasses();
		
		//DAOs
		bind(CastViewObjectDAO.class);
		bind(ConfigurationDAO.class);
		bind(CastViewDAO.class);
        
		//Cast View
	    bind(CastViewServices.class);
		bind(CastViewAPIResource.class);
		bind(ContentResource.class);
	    bind(CastViewObjectCache.class);
        bind(DefaultCastViewStrategy.class);
		
		//Card
		bind(CardServices.class);
		
		//Util
		bind(APIServices.class);
		bind(ConfigurationServices.class);
		bind(ConfigurationResource.class);
		
		// Strategies
        MapBinder<String, CastViewStrategy> commands = MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {
        }, new TypeLiteral<CastViewStrategy>() {
        });
        commands.addBinding("default").to(DefaultCastViewStrategy.class);
	}
	
    /*
     * Bind third party classes
     */
    private void bindThirdPartyClasses() {
        bind(GuiceContainer.class);
        bind(ObjectifyFilter.class).in(Scopes.SINGLETON);
    }

}
