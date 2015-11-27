package io.cocast.core;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Default implementation for network services
 */
@Singleton
class DefaultNetworkServicesImpl implements NetworkServices {

    @Override
    public String sayHello() {
        return "Hello world!";
    }

    @Override
    public List<Network> list() {

        Network network1 = new Network();
        network1.setName("Name1");
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
