package io.cocast.auth;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Persistence methods for users
 */
@Singleton
class UserRepository {

    private static Logger logger = LogManager.getLogger(UserRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    private static final Cache<String, List<User>> cache;

    static {
        //initializes the caches
        cache = CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Returns a user based on its uid
     */
    public User findUser(String uid) throws IOException, ExecutionException {

        //looks into the cache
        List<User> listUser = cache.get(uid, new UserLoader(uid));
        if ((listUser != null) && (listUser.size() > 0)) {
            return listUser.get(0);
        } else {
            return null;
        }
    }

    private class UserLoader implements Callable<List<User>> {

        private String uid;

        public UserLoader(String uid) {
            this.uid = uid;
        }

        @Override
        public List<User> call() throws Exception {
            logger.debug("Populating user cache...");
            User user = firebaseUtils.getAsRoot("/users/" + uid + ".json", User.class);
            if (user != null) {
                user.setUid(uid);
            }
            List<User> resultList = new ArrayList<User>();
            resultList.add(user);
            return resultList;
        }
    }
}
