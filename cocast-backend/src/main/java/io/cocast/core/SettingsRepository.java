package io.cocast.core;

import com.google.inject.Singleton;
import io.cocast.util.CacheUtils;
import io.cocast.util.CoCastCallException;
import io.cocast.util.DateUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Persistence methods for settings
 */
@Singleton
public class SettingsRepository {

    private static Logger logger = LogManager.getLogger(SettingsRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    @Inject
    private NetworkServices networkServices;

    private static CacheUtils cache = CacheUtils.getInstance(Settings.class);

    /**
     * Creates a new settings
     */
    public void create(Settings settings) throws Exception {

        networkServices.validate(settings.getNetworkMnemonic());

        //validate if all info are OK
        if (settings.getName() == null) {
            throw new ValidationException("Settings name must be informed");
        }
        if (settings.getValue() == null) {
            throw new ValidationException("Settings value must be informed");
        }

        //checks if exists
        Settings existingSettings = this.get(settings.getNetworkMnemonic(), settings.getName());
        if (existingSettings != null) {
            throw new ValidationException("Settings with name = " + settings.getName() + " already exists");
        }

        //insert
        firebaseUtils.saveAsRoot(settings, "/settings/" + settings.getNetworkMnemonic() + "/" + settings.getName() + ".json");
        cache.invalidate(settings.getNetworkMnemonic());
    }

    /**
     * Lists all settings for a specific network
     */
    public List<Settings> list(String networkMnemonic) throws Exception {

        networkServices.validateWithIssuer(networkMnemonic);

        //looks into the cache
        List<Settings> listSettings = cache.get(networkMnemonic, new SettingsLoader(networkMnemonic));
        if (listSettings == null) {
            //cannot cache null
            cache.invalidate(networkMnemonic);
        }

        return listSettings;
    }

    /**
     * Get a specific settings
     */
    public Settings get(String networkMnemonic, String name) throws Exception {
        networkServices.validateWithIssuer(networkMnemonic);

        List<Settings> allSettings = this.list(networkMnemonic);
        for (Settings settings : allSettings) {
            if (settings.getName().equals(name)) {
                return settings;
            }
        }

        return null;
    }

    /**
     * Update a settings
     */
    public Settings update(Settings settings, String networkMnemonic) throws Exception {

        networkServices.validate(networkMnemonic);

        Settings existingSettings = this.get(networkMnemonic, settings.getName());
        if (existingSettings == null) {
            throw new CoCastCallException("Could not find settings with name: " + settings.getName(), 404);
        }

        //update info
        settings.setNetworkMnemonic(networkMnemonic);
        settings.setLastUpdate(DateUtils.now());
        settings.setCreatedBy(existingSettings.getCreatedBy());
        if (settings.getValue() == null) {
            settings.setValue(existingSettings.getValue());
        }
        if (settings.getDescription() == null) {
            settings.setDescription(existingSettings.getDescription());
        }

        //update
        firebaseUtils.saveAsRoot(settings, "/settings/" + networkMnemonic + "/" + settings.getName() + ".json");
        cache.invalidate(networkMnemonic);

        return settings;
    }

    /**
     * Delete a settings
     */
    public void delete(String networkMnemonic, String name) throws Exception {
        networkServices.validate(networkMnemonic);

        Settings existingSettings = this.get(networkMnemonic, name);
        if (existingSettings == null) {
            throw new CoCastCallException("Could not find settings with name: " + name, 404);
        }

        //delete
        firebaseUtils.deleteAsRoot("/settings/" + networkMnemonic + "/" + existingSettings.getName() + ".json");
        cache.invalidate(networkMnemonic);
    }

    private class SettingsLoader implements Callable<List<Settings>> {

        private String networkMnemonic;

        public SettingsLoader(String networkMnemonic) {
            this.networkMnemonic = networkMnemonic;
        }

        @Override
        public List<Settings> call() throws Exception {
            String uri = "/settings/" + networkMnemonic + ".json";
            return firebaseUtils.listAsRoot(uri, Settings.class);
        }
    }
}
