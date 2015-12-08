package io.cocast.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import io.cocast.util.FirebaseException;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Persistence methods for Configurations
 */
@Singleton
class ConfigurationRepository {

    private static Logger logger = LogManager.getLogger(ConfigurationRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    private static final Cache<String, List<Configuration>> cache;

    static {
        //initializes the caches
        cache = CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Lists all configurations
     */
    public List<Configuration> list() throws Exception {
        //looks into the cache
        return cache.get("cacheList", new Callable<List<Configuration>>() {
            @Override
            public List<Configuration> call() throws Exception {
                logger.debug("Populating configuration cache...");
                return firebaseUtils.get("/configurations.json", Configuration.class);
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
    public void create(Configuration configuration) throws JsonProcessingException, FirebaseException {
        firebaseUtils.save(configuration, "/configurations/" + configuration.getName() + ".json");
        this.cleanUpCache();
    }

    /**
     * Reloads the cache
     */
    public void cleanUpCache() {
        logger.debug("Cleaning up cache");
        cache.invalidateAll();
    }

}
