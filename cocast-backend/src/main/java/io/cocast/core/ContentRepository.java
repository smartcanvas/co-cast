package io.cocast.core;

import com.google.inject.Singleton;
import io.cocast.util.CacheUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.concurrent.Callable;

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

    private static CacheUtils cache = CacheUtils.getInstance(Content.class);

    /**
     * Create or update a new content
     *
     * @return If the content was changed or not
     */
    public boolean save(Content content) throws Exception {

        networkServices.validate(content.getNetworkMnemonic());

        //validate the content
        this.validate(content);

        //checks if the content already exists
        Content existingContent = this.get(content.getNetworkMnemonic(), content.getId());
        if ((existingContent != null) && (existingContent.equals(content))) {
            return false;
        }


        //insert
        firebaseUtils.saveAsRoot(content, "/contents/" + content.getNetworkMnemonic() + "/" + content.getId() + ".json");
        cache.set(generateCacheKey(content.getNetworkMnemonic(), content.getId()), content);

        return true;
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
     * Validate if the content is ready to be created or updated
     */
    private void validate(Content content) {

    }

    /**
     * Generates the key for content cache
     */
    private String generateCacheKey(String networkMnemonic, String id) {
        return "content_" + networkMnemonic + "_" + id;
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
