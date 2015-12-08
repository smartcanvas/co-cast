package io.cocast.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import io.cocast.util.FirebaseException;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Persistence methods for color palettes
 */
@Singleton
public class ColorPaletteRepository {

    private static Logger logger = LogManager.getLogger(ConfigurationRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    private static final Cache<String, List<ColorPalette>> cache;

    static {
        //initializes the caches
        cache = CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Lists all color palettes
     */
    public List<ColorPalette> list() throws Exception {
        //looks into the cache
        return cache.get("cacheList", new Callable<List<ColorPalette>>() {
            @Override
            public List<ColorPalette> call() throws Exception {
                logger.debug("Populating color palette cache...");
                return firebaseUtils.get("/colorPalettes.json", ColorPalette.class);
            }
        });
    }

    /**
     * Gets a specific color palette
     */
    public ColorPalette get(String key) throws Exception {
        List<ColorPalette> colorList = this.list();
        for (ColorPalette colorPalette : colorList) {
            if (colorPalette.getMnemonic().equals(key)) {
                return colorPalette;
            }
        }

        return null;
    }

    /**
     * Creates a new configuration
     */
    public void create(ColorPalette colorPalette) throws JsonProcessingException, FirebaseException {

        //adds a hash to the beginning of each color
        if (!colorPalette.getPrimaryColor().startsWith("#")) {
            colorPalette.setPrimaryColor("#" + colorPalette.getPrimaryColor());
        }
        if (!colorPalette.getSecondaryColor().startsWith("#")) {
            colorPalette.setSecondaryColor("#" + colorPalette.getSecondaryColor());
        }
        if (!colorPalette.getAccentColor().startsWith("#")) {
            colorPalette.setAccentColor("#" + colorPalette.getAccentColor());
        }
        if (!colorPalette.getPrimaryTextColor().startsWith("#")) {
            colorPalette.setPrimaryTextColor("#" + colorPalette.getPrimaryTextColor());
        }
        if (!colorPalette.getSecondaryTextColor().startsWith("#")) {
            colorPalette.setSecondaryTextColor("#" + colorPalette.getSecondaryTextColor());
        }
        if (!colorPalette.getAccentTextColor().startsWith("#")) {
            colorPalette.setAccentTextColor("#" + colorPalette.getAccentTextColor());
        }

        firebaseUtils.save(colorPalette, "/colorPalettes/" + colorPalette.getMnemonic() + ".json");
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
