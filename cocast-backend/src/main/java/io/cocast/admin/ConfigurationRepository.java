package io.cocast.admin;

import com.google.inject.Singleton;
import io.cocast.util.CacheUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Persistence methods for Configurations
 */
@Singleton
class ConfigurationRepository {

    private static Logger logger = LogManager.getLogger(ConfigurationRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    private static CacheUtils cache = CacheUtils.getInstance(Configuration.class);

    /**
     * Lists all configurations
     */
    public List<Configuration> list() throws Exception {
        //looks into the cache
        return cache.get("configurationList_cache", new Callable<List<Configuration>>() {
            @Override
            public List<Configuration> call() throws Exception {
                return firebaseUtils.list("/configurations.json", Configuration.class);
            }
        });
    }

    /**
     * Gets a specific configuration
     */
    public Configuration get(String key) throws Exception {
        List<Configuration> confList = this.list();
        for (Configuration configuration : confList) {
            if (configuration.getName().equals(key)) {
                return configuration;
            }
        }

        return null;
    }

    /**
     * Creates a new configuration
     */
    public void create(Configuration configuration) throws Exception {
        firebaseUtils.save(configuration, "/configurations/" + configuration.getName() + ".json");
        this.cleanUpCache();
    }

    /**
     * Reloads the cache
     */
    public void cleanUpCache() throws Exception {
        cache.invalidate("configurationList_cache");
    }
}
