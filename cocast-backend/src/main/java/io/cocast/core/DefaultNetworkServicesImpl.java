package io.cocast.core;

import io.cocast.configuration.ConfigurationServices;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Default implementation for network services
 */
public class DefaultNetworkServicesImpl implements NetworkServices {

    @Inject
    private ConfigurationServices configurationServices;

    @Override
    public List<Network> list() {

        Network network1 = new Network();
        network1.setName(configurationServices.getString("environment", "N/A"));
        network1.setCreatedDate(new Date());
        network1.setOwner("viveiros@ciandt.com");

        Network network2 = new Network();
        network2.setName("Name2");
        network2.setCreatedDate(new Date());
        network2.setOwner("viveiros@ciandt.com");

        List<Network> result = new ArrayList<Network>();
        result.add(network1);
        result.add(network2);

        return result;
    }
}
