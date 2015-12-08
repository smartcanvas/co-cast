package io.cocast.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.security.token.TokenGenerator;
import io.cocast.admin.ConfigurationServices;
import io.cocast.auth.SecurityContext;
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
import java.util.*;

/**
 * Utility class to interface with Firebase
 */
public class FirebaseUtils {

    private static Logger logger = LogManager.getLogger(FirebaseUtils.class.getName());

    private ConfigurationServices configurationServices;
    private ObjectMapper objectMapper;

    /**
     * Firebase secret
     */
    private static String FIREBASE_SECRET;

    /**
     * Base firebase URL
     */
    private static String FIREBASE_URL;

    /**
     * Constructor
     */
    @Inject
    public FirebaseUtils(ConfigurationServices configurationServices,
                         ObjectMapper objectMapper) {
        this.configurationServices = configurationServices;
        this.objectMapper = objectMapper;

        FIREBASE_SECRET = configurationServices.getString("firebase.secret", null);
        FIREBASE_URL = configurationServices.getString("firebase.base_url", null);
    }

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

        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        return FIREBASE_URL + uri + "?auth=" + generateToken();
    }

    /**
     * Generate token
     */
    private String generateToken() {

        // Generate a new secure JWT
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", SecurityContext.get().userIdentification());
        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SECRET);
        return tokenGenerator.createToken(payload);
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
