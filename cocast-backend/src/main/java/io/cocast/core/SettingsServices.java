package io.cocast.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Services for settings
 */
@Singleton
public class SettingsServices {

    @Inject
    SettingsRepository settingsRepository;

    /**
     * Gets the value of a setting
     */
    public String getString(String networkMnemonic, String key) throws Exception {

        Settings setting = settingsRepository.get(networkMnemonic, key);
        if (setting != null) {
            return setting.getValue();
        } else {
            return null;
        }
    }
}
