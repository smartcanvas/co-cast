package io.cocast.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.cocast.admin.ConfigurationServices;
import io.cocast.config.BasicBackendModule;
import io.cocast.util.log.LogUtils;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Cache methods
 */
@Singleton
public class CacheUtils {

    private static final Integer DEFAULT_TIMEOUT = 3600;

    private static Logger logger = LogManager.getLogger(CacheUtils.class.getName());

    private static Map<String, CacheUtils> instanceMap = new HashMap<String, CacheUtils>();

    private MemcachedClient memcachedClient;
    private String clientName;
    private Class clazz;

    /**
     * Constructor
     */
    @Inject
    private CacheUtils(ConfigurationServices configurationServices) {

        //creates the client
        String strAddresses = configurationServices.getString("memcached.servers");
        try {
            memcachedClient = new MemcachedClient(AddrUtil.getAddresses(strAddresses));
        } catch (IOException e) {
            LogUtils.fatal(logger, "Error creating memcachedClient: " + e.getMessage(), e);
        }

        this.clientName = "default";
    }

    /**
     * Returns an instance of the cache for a specific object
     */
    public static CacheUtils getInstance(Class entityClass) {
        String className = entityClass.getName();
        if (instanceMap.get(className) != null) {
            return instanceMap.get(className);
        } else {
            Injector injector = Guice.createInjector(new BasicBackendModule());
            CacheUtils instance = injector.getInstance(CacheUtils.class);
            instance.setType(entityClass);
            instanceMap.put(className, instance);
            return instance;
        }
    }

    /**
     * Stores an object into the cache
     */
    public void set(String key, Integer timeout, Object value) {

        if (value == null) {
            return;
        }

        try {
            Object storedValue = memcachedClient.set(generateKey(key), timeout, value).get();
            if (storedValue == null) {
                LogUtils.fatal(logger, "Memcached returned a null object after trying to store object " + value
                        + " under key = " + key, null);
            }
        } catch (InterruptedException e) {
            LogUtils.fatal(logger, "Error putting object " + value + " into memcached under key = " + key, e);
        } catch (ExecutionException e) {
            LogUtils.fatal(logger, "Error putting object " + value + " into memcached under key = " + key, e);
        }
    }

    /**
     * Stores an object into the cache
     */
    public void set(String key, Object value) {
        this.set(key, DEFAULT_TIMEOUT, value);
    }

    /**
     * Retrieves an object from cache
     */
    public <T> T get(String key) {

        Object value = memcachedClient.get(generateKey(key));
        if (value == null) {
            return null;
        }

        return (T) value;
    }

    /**
     * Retrieves an object from cache
     */
    public <T> T get(String key, Callable<? extends T> valueLoader) throws Exception {

        String genkey = generateKey(key);
        Object value = memcachedClient.get(genkey);
        if (value == null) {
            value = valueLoader.call();
            this.set(key, value);
        }

        return (T) value;
    }

    /**
     * Removes this key from cache
     */
    public void invalidate(String key) {
        memcachedClient.delete(generateKey(key));
    }

    /**
     * Cleans the cache
     */
    public void invalidateAll() {
        memcachedClient.flush();
    }

    /**
     * Generate the cache key
     */
    private String generateKey(String key) {
        return ExtraStringUtils.generateMnemonic(clientName + "_" + key);
    }

    /**
     * Defines the class type
     */
    private void setType(Class clazz) {
        this.clazz = clazz;
        this.clientName = clazz.getSimpleName();
    }

}
