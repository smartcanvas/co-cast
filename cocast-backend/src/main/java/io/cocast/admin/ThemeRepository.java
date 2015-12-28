package io.cocast.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import io.cocast.util.CoCastCallException;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Persistence methods for themes
 */
@Singleton
class ThemeRepository {

    private static Logger logger = LogManager.getLogger(ConfigurationRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    private static final Cache<String, List<Theme>> cache;

    static {
        //initializes the caches
        cache = CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Lists all themes
     */
    public List<Theme> list() throws Exception {
        //looks into the cache
        return cache.get("cacheList", new Callable<List<Theme>>() {
            @Override
            public List<Theme> call() throws Exception {
                logger.debug("Populating theme cache...");
                return firebaseUtils.get("/themes.json", Theme.class);
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
    public void create(Theme theme) throws JsonProcessingException, CoCastCallException {

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
    public void cleanUpCache() {
        logger.debug("Cleaning up cache");
        cache.invalidateAll();
    }
}
