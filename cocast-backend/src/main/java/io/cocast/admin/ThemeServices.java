package io.cocast.admin;

import com.google.inject.Singleton;

import javax.inject.Inject;

/**
 * Services for themes
 */
@Singleton
public class ThemeServices {

    @Inject
    private ThemeRepository themeRepository;

    /**
     * Checks if a specific theme exists or not
     */
    public boolean exists(String strTheme) throws Exception {
        return this.get(strTheme) != null;
    }

    /**
     * Returns a theme by its mnemonic
     */
    public Theme get(String themeMnemonic) throws Exception {
        return themeRepository.get(themeMnemonic);
    }
}
