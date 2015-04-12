package com.ciandt.dcoder.c2.config;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class C2ServletModule extends ServletModule {

    @Override
    protected void configureServlets() {
        install(new C2Module());
        
        // Objectify requires a filter to clean up any thread-local transaction contexts and pending asynchronous
        // operations that remain at the end of a request: https://code.google.com/p/objectify-appengine/wiki/Setup
        filter("/*").through(ObjectifyFilter.class);
        
        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put(ResourceConfig.FEATURE_TRACE, "true");
        initParams.put(GuiceContainer.JSP_TEMPLATES_BASE_PATH, "/WEB-INF/jsp");
        initParams.put(GuiceContainer.PROPERTY_WEB_PAGE_CONTENT_REGEX,
                        "(/(_ah|appstats|images|static-images|scripts|styles)/?.*)|(/.*\\.jsp)|(/WEB-INF/.*\\.jsp)|(/favicon\\.ico)");
        initParams.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
        filter("/*").through(GuiceContainer.class, initParams);
    }

}
