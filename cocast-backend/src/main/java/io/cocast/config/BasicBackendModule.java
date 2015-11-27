package io.cocast.config;

import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import io.cocast.auth.*;
import io.cocast.util.Configuration;

/**
 * Common Guice configurations and bindings for Co-Cast
 */
public class BasicBackendModule extends ServletModule {

    @Override
    protected void configureServlets() {

        // Utility classes
        bind(Configuration.class);

        //Auth classes
        bind(AuthServices.class);
        bind(FirebaseTokenReader.class);
        bind(GoogleTokenReader.class);
        bind(TokenServices.class).annotatedWith(Names.named("accessToken")).to(JwtAccessTokenServices.class);
        bind(TokenServices.class).annotatedWith(Names.named("refreshToken")).to(JwtRefreshTokenServices.class);
    }
}
