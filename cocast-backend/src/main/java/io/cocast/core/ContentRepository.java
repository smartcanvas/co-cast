package io.cocast.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Persistence methods for contents
 */
@Singleton
class ContentRepository {

    private static Logger logger = LogManager.getLogger(ContentRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    @Inject
    private NetworkServices networkServices;

    private static final Cache<String, Content> cache;

    static {
        //initializes the caches
        cache = CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Create or update a new content
     */
    public void save(Content content) throws Exception {

        networkServices.validate(content.getNetworkMnemonic());

        //validate the content
        this.validate(content);

        //insert
        firebaseUtils.saveAsRoot(content, "/contents/" + content.getNetworkMnemonic() + "/" + content.getId() + ".json");
        cache.put(generateCacheKey(content.getNetworkMnemonic(), content.getId()), content);
    }


    /**
     * Get a specific content
     */
    public Content get(String networkMnemonic, String id) throws Exception {
        networkServices.validate(networkMnemonic);
        String cacheKey = generateCacheKey(networkMnemonic, id);

        //looks into the cache
        Content content = cache.get(cacheKey, new ContentLoader(networkMnemonic, id));
        if (content == null) {
            //cannot cache null
            cache.invalidate(cacheKey);
        }

        return content;
    }

    /**
     * Validate is the content is ready to be created or updated
     */
    private void validate(Content content) {

    }

    /**
     * Generates the key for content cache
     */
    private String generateCacheKey(String networkMnemonic, String id) {
        return networkMnemonic + "_" + id;
    }

    /**
     * Loads the cache for specific contents
     */
    private class ContentLoader implements Callable<Content> {

        private String networkMnemonic;
        private String id;

        public ContentLoader(String networkMnemonic, String id) {
            this.networkMnemonic = networkMnemonic;
            this.id = id;
        }

        @Override
        public Content call() throws Exception {

            if (logger.isDebugEnabled()) {
                logger.debug("Populating content cache for ID = " + id + ", and network = " + networkMnemonic);
            }

            //a list of contents
            String uri = "/contents/" + networkMnemonic + "/" + id + ".json";
            Content content = firebaseUtils.getAsRoot(uri, Content.class);
            if ((content == null) || (!content.isActive())) {
                return null;
            } else {
                return content;
            }
        }
    }
}
