package io.cocast.ext.match;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.cocast.config.BasicBackendModule;
import io.cocast.ext.people.Person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shuffles a list of persons to match experience
 */
public abstract class Shuffler {

    /**
     * Default implementation
     */
    private static String DEFAULT_SHUFFLER = "io.cocast.ext.match.TeamworkShuffler";

    /**
     * Map with Shufflers
     */
    private static Map<String, Shuffler> shufflerMap = new HashMap<String, Shuffler>();

    /**
     * Returns the instance of the shuffler
     */
    public static Shuffler getInstance() throws ClassNotFoundException {

        //in the future, we may support a settings here to change the implementation
        String className = DEFAULT_SHUFFLER;

        Shuffler shuffler = shufflerMap.get(className);
        if (shuffler == null) {
            Injector injector = Guice.createInjector(new BasicBackendModule());
            Class clazz = Class.forName(className);
            shuffler = (Shuffler) injector.getInstance(clazz);
            shufflerMap.put(className, shuffler);
        }

        return shuffler;
    }

    /**
     * Shuffles the list
     */
    public abstract List<Person> shuffle(String networkMnemonic, List<Person> personList, Person requester) throws Exception;
}
