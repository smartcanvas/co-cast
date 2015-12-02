package io.cocast.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import io.cocast.util.FirebaseException;
import io.cocast.util.FirebaseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Configuration mechanism
 */
@Singleton
public class ConfigurationServices {

    /**
     * Logger
     */
    private static Logger logger = LogManager.getLogger(ConfigurationServices.class.getName());

    /**
     * Default env
     */
    private static final String DEFAULT_ENV = "local";

    /**
     * Env KEY
     */
    private static final String ENV_KEY = "COCAST_ENV";

    /**
     * Cache and Bundle
     */
    private static final Cache<String, List<Configuration>> cache;
    private static ResourceBundle bundle;

    @Inject
    private FirebaseUtils firebaseUtils;

    @Inject
    private ObjectMapper objectMapper;

    static {

        //initializes the caches
        cache = CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();

        //gets the environment information
        Map<String, String> externalVars = System.getenv();
        String env = externalVars.get(ENV_KEY);
        logger.info("Initializing configuration mechanism... " + ENV_KEY + " = " + env);
        if (StringUtils.isEmpty(env)) {
            env = ConfigurationServices.DEFAULT_ENV;
            logger.info("Environment is null. Initializing as " + env);
        }

        try {
            //the property file must have the same name as the 'env' system property
            bundle = ResourceBundle.getBundle(env);
        } catch (RuntimeException exc) {
            logger.error("FATAL ERROR: could find file " + env + ".properties. The system won't work properly. " +
                    "Create this file of change the system property named '" + ENV_KEY + "' to another value.");
            throw exc;
        }

    }

    /**
     * Returns the value of a configuration referenced by 'key'
     *
     * @param key Configuration key
     * @return The value of the configuration
     */
    public String getString(String key) {
        return this.getString(key, null);
    }

    /**
     * Returns the value of a configuration referenced by 'key'
     *
     * @param key          Configuration key
     * @param defaultValue The default value in case the configuration doesn't exist
     * @return The value of the configuration
     */
    public String getString(String key, String defaultValue) {

        //try the bundle
        String value;
        try {
            value = bundle.getString(key);
        } catch (Exception e) {

            //try the configuration
            Configuration configuration = null;
            try {
                configuration = this.get(key);
            } catch (Exception e2) {
                logger.error("Error getting configuration with key = " + key, e2);
            }
            if (configuration != null) {
                value = configuration.getValue();
            } else {
                value = defaultValue;
            }
        }

        return value;
    }

    /**
     * Returns the value of a configuration referenced by 'key'
     *
     * @param key          Configuration key
     * @param defaultValue The default value in case the configuration doesn't exist
     * @return The value of the configuration
     */
    public Integer getInt(String key, Integer defaultValue) {
        Integer result;
        String strValue = this.getString(key);
        if (strValue != null) {
            result = Integer.parseInt(strValue);
        } else {
            result = defaultValue;
        }

        return result;
    }

    /**
     * Creates a new configuration
     */
    void create(Configuration configuration) throws JsonProcessingException, FirebaseException {
        firebaseUtils.save(configuration, "/configurations/" + configuration.getName() + ".json");
    }

    /**
     * Lists all configurations
     */
    List<Configuration> list() throws Exception {
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
    Configuration get(String key) throws Exception {
        List<Configuration> confList = this.list();
        for (Configuration configuration : confList) {
            if (configuration.getName().equals(key)) {
                return configuration;
            }
        }

        return null;
    }

    /**
     * Reloads the cache
     */
    void cleanUpCache() {
        logger.debug("Cleaning up cache");
        cache.invalidateAll();
    }
}
