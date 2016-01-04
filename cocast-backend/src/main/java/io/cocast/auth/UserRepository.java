package io.cocast.auth;

import com.google.inject.Singleton;
import io.cocast.util.CacheUtils;
import io.cocast.util.FirebaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Persistence methods for users
 */
@Singleton
class UserRepository {

    private static Logger logger = LogManager.getLogger(UserRepository.class.getName());

    @Inject
    private FirebaseUtils firebaseUtils;

    private static CacheUtils cache = CacheUtils.getInstance(User.class);

    /**
     * Returns a user based on its uid
     */
    public User findUser(String uid) throws IOException, ExecutionException {

        //looks into the cache
        return cache.get(uid, new UserLoader(uid));
    }

    private class UserLoader implements Callable<User> {

        private String uid;

        public UserLoader(String uid) {
            this.uid = uid;
        }

        @Override
        public User call() throws Exception {
            logger.debug("Populating user cache...");
            User user = firebaseUtils.getAsRoot("/users/" + uid + ".json", User.class);
            if (user != null) {
                user.setUid(uid);
            }
            return user;
        }
    }
}
