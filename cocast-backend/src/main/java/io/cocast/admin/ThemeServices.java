package io.cocast.admin;

import com.google.inject.Singleton;

import javax.inject.Inject;
import java.util.List;

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
        List<Theme> themeList = themeRepository.list();
        for (Theme theme : themeList) {
            if (strTheme.equals(theme.getMnemonic())) {
                return true;
            }
        }

        return false;
    }
}
