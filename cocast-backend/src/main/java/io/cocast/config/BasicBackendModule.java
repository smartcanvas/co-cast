package io.cocast.config;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.thetransactioncompany.cors.CORSFilter;
import io.cocast.admin.AdminModule;
import io.cocast.auth.ApiTokenSecurityFilter;
import io.cocast.auth.AuthModule;
import io.cocast.core.CoreModule;
import io.cocast.ext.ExtensionsModule;

/**
 * Common Guice configurations and bindings for Co-Cast
 */
public class BasicBackendModule extends ServletModule {

    @Override
    protected void configureServlets() {

        this.install(new AdminModule());
        this.install(new AuthModule());
        this.install(new CoreModule());
        this.install(new ExtensionsModule());

        //filters
        bind(GenericErrorHandlingFilter.class).in(Scopes.SINGLETON);
        bind(CORSFilter.class).in(Scopes.SINGLETON);
        bind(ApiTokenSecurityFilter.class).in(Scopes.SINGLETON);
        filter("/*").through(GenericErrorHandlingFilter.class);
        filter("/*").through(CORSFilter.class, ImmutableMap.of("cors.allowSubdomains", "true",
                "cors.supportedMethods", "GET, POST, HEAD, PUT, DELETE, OPTIONS",
                "cors.maxAge", "1728000" // 20 dias
        ));
        filter("/api/*").through(ApiTokenSecurityFilter.class);
    }

    @Provides
    @Singleton
    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.getFactory()
                .configure(com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII, true)
                .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.registerModule(new JodaModule());
        objectMapper.registerModule(new AfterburnerModule());
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    @Provides
    @Singleton
    private ObjectWriter objectWriter(ObjectMapper objectMapper) {
        return objectMapper.writer();
    }

    @Provides
    @Singleton
    private ObjectReader objectReader(ObjectMapper objectMapper) {
        return objectMapper.reader();
    }
}
