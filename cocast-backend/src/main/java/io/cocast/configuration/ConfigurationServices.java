package io.cocast.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Singleton;
import io.cocast.util.FirebaseException;
import io.cocast.util.FirebaseUtils;
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

    private static ResourceBundle bundle;

    @Inject
    private FirebaseUtils firebaseUtils;

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

    /**
     * Create a new configuration
     */
    public void create(Configuration configuration) throws JsonProcessingException, FirebaseException {
        firebaseUtils.save(configuration, "/configurations/" + configuration.getName() + ".json");
    }

}
