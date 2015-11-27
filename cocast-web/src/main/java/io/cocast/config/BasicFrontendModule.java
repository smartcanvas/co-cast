package io.cocast.config;

import com.google.inject.servlet.ServletModule;
import io.cocast.web.LoginServlet;

/**
 * Common Guice configurations and bindings for Co-Cast
 */
public class BasicFrontendModule extends ServletModule {

    @Override
    protected void configureServlets() {

        //Login
        serve("/login").with(LoginServlet.class);

        //The rest of the routings
        //serve( "/*" ).with( RoutingServlet.class );

    }
}
