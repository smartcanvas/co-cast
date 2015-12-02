package io.cocast.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cocast.configuration.ConfigurationServices;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * Utility class to interface with Firebase
 */
public class FirebaseUtils {

    private static Logger logger = LogManager.getLogger(FirebaseUtils.class.getName());

    @Inject
    private ConfigurationServices configurationServices;

    /**
     * Base firebase URL
     */
    private static String FIREBASE_URL;

    public void save(Object obj, String uri) throws JsonProcessingException, FirebaseException {

        String completeURL = getFirebaseURL(uri);
        Client client = ClientBuilder.newClient().register(JacksonFeature.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Firebase URL = " + completeURL);
            logger.debug("Object to be created = " + obj);
        }

        Response response = client.target(completeURL).request().put(Entity.json(obj));

        if (logger.isDebugEnabled()) {
            logger.debug("Response = " + response);
        }

        if (!(response.getStatus() == HttpServletResponse.SC_OK) &&
                !(response.getStatus() == HttpServletResponse.SC_CREATED)) {
            throw new FirebaseException("Error creating entity " + obj + ". Status = " + response.getStatus());
        }
    }

    /**
     * Gets the complete URL
     */
    private String getFirebaseURL(String uri) {

        if (FIREBASE_URL == null) {
            FIREBASE_URL = configurationServices.getString("firebase.base_url", null);
        }

        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        return FIREBASE_URL + uri;
    }
}
