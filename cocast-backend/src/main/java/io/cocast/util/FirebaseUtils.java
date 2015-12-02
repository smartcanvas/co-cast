package io.cocast.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class to interface with Firebase
 */
public class FirebaseUtils {

    private static Logger logger = LogManager.getLogger(FirebaseUtils.class.getName());

    @Inject
    private ConfigurationServices configurationServices;

    @Inject
    private ObjectMapper objectMapper;

    /**
     * Base firebase URL
     */
    private static String FIREBASE_URL;

    /**
     * Saves an object inside Firebase
     */
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
     * Fetchs data from Firebase
     */
    public <T> List<T> get(String uri, Class<T> cls) throws IOException {

        String completeURL = getFirebaseURL(uri);
        Client client = ClientBuilder.newClient().register(JacksonFeature.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Firebase URL = " + completeURL);
        }

        String strFirebaseResult = client.target(completeURL).request().get(String.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Return from Firebase = " + strFirebaseResult);
        }

        return getListFromResult(strFirebaseResult, cls);
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

    /**
     * Gets a list from the result
     */
    <T> List<T> getListFromResult(String strObjectList, Class<T> cls) throws IOException {
        JsonNode rootNode = objectMapper.readTree(strObjectList);
        ArrayList<T> result = new ArrayList<T>();
        Iterator<JsonNode> iterator = rootNode.iterator();
        while (iterator.hasNext()) {
            JsonNode child = iterator.next();
            result.add(objectMapper.treeToValue(child, cls));
        }

        return result;
    }
}
