package com.ciandt.d1.cocast.castview;

import static com.ciandt.d1.cocast.util.OfyService.ofy;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Persistent methods for Cast View
 * 
 * @author <a href="mailto:viveiros@ciandt.com">Daniel Viveiros</a>
 */
@Singleton
public class CastViewDAO {
    
    private static final String CACHE_KEY = "cacheCastView";
    private static Integer EXPIRATION_TIME = 60 * 60 * 3;
    
    @Inject
    private Logger logger;
    
    /**
     * Returns the cast view by its mnemonic. If mnemonic == null, returns the default. 
     */
    public CastView findByMnemonic( String mnemonic ) {
        List<CastView> castViews = this.findAll();
        for (CastView castView: castViews) {
            if (!StringUtils.isEmpty(mnemonic)) {
                if ( mnemonic.equals(castView.getMnemonic())) {
                    return castView;
                }
            } else {
                if (castView.getIsDefault()) {
                    return castView;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Return all cast views saved on the datastore
     * @param castViewObject Cast View Object to be saved
     */
    @SuppressWarnings("unchecked")
    public List<CastView> findAll() {
        MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
        List<CastView> castViews = (List<CastView>) cache.get(CACHE_KEY);
        if (castViews != null) {
            return castViews;
        } else {
            castViews = ofy().load().type(CastView.class).list();
            cache.put(CACHE_KEY, castViews, Expiration.byDeltaSeconds( EXPIRATION_TIME ));
        }
        return castViews;
    }
    
    /**
     * Saves a cast view into datastore
     * @param castViewObject Cast View Object to be saved
     */
    public void save( CastView castView ) {
        logger.info( "Saving cast view= " + castView );
        ofy().save().entity(castView).now();
        
        //resets the cache
        MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
        cache.delete(CACHE_KEY);
    }

}
