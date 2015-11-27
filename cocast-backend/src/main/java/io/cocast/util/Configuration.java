package io.cocast.util;

import com.google.inject.Singleton;

import java.util.ResourceBundle;

/**
 * Gets configurations for Co-Cast
 */
@Singleton
public class Configuration {

    private static ResourceBundle bundle = ResourceBundle.getBundle("config");

    /**
     * Returns the value of a configuration referenced by 'key'
     *
     * @param key          Configuration key
     * @param defaultValue The default value in case the configuration doesn't exist
     * @return The value of the configuration
     */
    public String getString(String key, String defaultValue) {

        String value = null;
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
        Integer value = null;
        try {
            value = Integer.parseInt(bundle.getString(key));
        } catch (Exception e) {
            value = defaultValue;
        }

        return value;
    }

}
