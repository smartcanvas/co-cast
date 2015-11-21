package io.cocast.admin.config;

import com.google.inject.servlet.ServletModule;
import io.cocast.network.services.NetworkServices;
import io.cocast.network.services.impl.DefaultNetworkServicesImpl;

/**
 * Module to configure CoCast bindings
 */
public class CoCastModule extends ServletModule {

    @Override
    protected void configureServlets() {

        //Resources
        bind(NetworkServices.class).to(DefaultNetworkServicesImpl.class);
    }
}
