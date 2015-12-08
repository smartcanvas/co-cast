package io.cocast.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.cocast.config.BasicBackendModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.Map;
import java.util.ResourceBundle;

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
     * Repository
     */
    @Inject
    private ConfigurationRepository configurationRepository;

    /**
     * Bundle
     */
    private static ResourceBundle bundle;

    @Inject
    private ObjectMapper objectMapper;

    static {
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
     * Return the singleton instance
     */
    public static ConfigurationServices getInstance() {
        Injector injector = Guice.createInjector(new BasicBackendModule());
        return injector.getInstance(ConfigurationServices.class);
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
                configuration = configurationRepository.get(key);
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
}
