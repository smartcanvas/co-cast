package io.cocast.admin;

import com.google.inject.Singleton;
import io.cocast.util.CacheUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Persistence methods for themes
 */
@Singleton
class ThemeRepository {

    private static Logger logger = LogManager.getLogger(ConfigurationRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    private static CacheUtils cache = CacheUtils.getInstance(Theme.class);

    /**
     * Lists all themes
     */
    public List<Theme> list() throws Exception {
        //looks into the cache
        return cache.get("themeList_cache", new Callable<List<Theme>>() {
            @Override
            public List<Theme> call() throws Exception {
                return firebaseUtils.list("/themes.json", Theme.class);
            }
        });
    }

    /**
     * Gets a specific themes
     */
    public Theme get(String key) throws Exception {
        List<Theme> themeList = this.list();
        for (Theme theme : themeList) {
            if (theme.getMnemonic().equals(key)) {
                return theme;
            }
        }

        return null;
    }

    /**
     * Creates a new configuration
     */
    public void create(Theme theme) throws Exception {

        //adds a hash to the beginning of each color
        if (!theme.getPrimaryColor().startsWith("#")) {
            theme.setPrimaryColor("#" + theme.getPrimaryColor());
        }
        if (!theme.getSecondaryColor().startsWith("#")) {
            theme.setSecondaryColor("#" + theme.getSecondaryColor());
        }
        if (!theme.getAccentColor().startsWith("#")) {
            theme.setAccentColor("#" + theme.getAccentColor());
        }
        if (!theme.getPrimaryFontColor().startsWith("#")) {
            theme.setPrimaryFontColor("#" + theme.getPrimaryFontColor());
        }
        if (!theme.getSecondaryFontColor().startsWith("#")) {
            theme.setSecondaryFontColor("#" + theme.getSecondaryFontColor());
        }
        if (!theme.getAccentFontColor().startsWith("#")) {
            theme.setAccentFontColor("#" + theme.getAccentFontColor());
        }

        firebaseUtils.save(theme, "/themes/" + theme.getMnemonic() + ".json");
        this.cleanUpCache();
    }

    /**
     * Reloads the cache
     */
    public void cleanUpCache() throws Exception {
        cache.invalidate("themeList_cache");
    }
}
