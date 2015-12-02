package io.cocast.configuration;

import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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

    private static ResourceBundle bundle;

    static {
        //gets the environment information
        String env = System.getProperty("env");
        logger.info("Initializing configuration mechanism... Env = " + env);
        if (StringUtils.isEmpty(env)) {
            env = ConfigurationServices.DEFAULT_ENV;
            logger.info("Environment is null. Initializing as " + env);
        }

        try {
            //the property file must have the same name as the 'env' system property
            bundle = ResourceBundle.getBundle(env);
        } catch (RuntimeException exc) {
            logger.error("FATAL ERROR: could find file " + env + ".properties. The system won't work properly. " +
                    "Create this file of change the system property named 'env' to another value.");
            throw exc;
        }
    }

    /**
     * Returns the value of a configuration referenced by 'key'
     *
     * @param key          Configuration key
     * @param defaultValue The default value in case the configuration doesn't exist
     * @return The value of the configuration
     */
    public String getString(String key, String defaultValue) {

        String value;
        try {
            value = bundle.getString(key);
        } catch (Exception e) {
            value = defaultValue;
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
        Integer value;
        try {
            value = Integer.parseInt(bundle.getString(key));
        } catch (Exception e) {
            value = defaultValue;
        }

        return value;
    }

}
