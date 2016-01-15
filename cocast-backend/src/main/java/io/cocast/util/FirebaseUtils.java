package io.cocast.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.security.token.TokenGenerator;
import com.google.inject.Singleton;
import io.cocast.admin.ConfigurationServices;
import io.cocast.auth.SecurityContext;
import io.cocast.util.log.LogUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

/**
 * Utility class to interface with Firebase
 */
@Singleton
public class FirebaseUtils {

    private static Logger logger = LogManager.getLogger(FirebaseUtils.class.getName());

    private ObjectMapper objectMapper;

    /**
     * Firebase secret
     */
    private static String FIREBASE_SECRET;

    /**
     * Base firebase URL
     */
    private static String FIREBASE_URL;

    private ConfigurationServices configurationServices;

    /**
     * Constructor
     */
    @Inject
    public FirebaseUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Saves an object inside Firebase
     */
    public void save(Object obj, String uri) throws JsonProcessingException {

        String completeURL = getFirebaseURL(uri);
        Client client = ClientBuilder.newClient().register(JacksonFeature.class);

        long initTime = System.currentTimeMillis();
        Response response = client.target(completeURL).request().put(Entity.json(obj));
        long endTime = System.currentTimeMillis();
        LogUtils.logExternalCall(logger, "Object saved on Firebase", "Firebase", "save", uri, 0,
                response.getStatus(), endTime - initTime);

        if (!(response.getStatus() == HttpServletResponse.SC_OK) &&
                !(response.getStatus() == HttpServletResponse.SC_CREATED)) {
            throw new CoCastCallException("Error creating entity " + obj + ". Status = " + response.getStatus(),
                    response.getStatus());
        }
    }

    /**
     * Saves an string to a Firebase property
     */
    public void saveString(String strValue, String uri) throws JsonProcessingException {

        String completeURL = getFirebaseURL(uri);
        Client client = ClientBuilder.newClient();

        long initTime = System.currentTimeMillis();
        Response response = client.target(completeURL).request().accept(MediaType.TEXT_PLAIN).
                put(Entity.text("\"" + strValue + "\""));
        long endTime = System.currentTimeMillis();
        LogUtils.logExternalCall(logger, "String saved on Firebase", "Firebase", "save", uri, 0, response.getStatus(),
                endTime - initTime);

        if (!(response.getStatus() == HttpServletResponse.SC_OK) &&
                !(response.getStatus() == HttpServletResponse.SC_CREATED)) {
            throw new CoCastCallException("Error saving string " + strValue + ". Status = " + response.getStatus(),
                    response.getStatus());
        }
    }

    /**
     * Saves an object inside Firebase
     */
    public void saveAsRoot(Object obj, String uri) throws JsonProcessingException, CoCastCallException {

        String completeURL = getFirebaseURLAsRoot(uri);
        Client client = ClientBuilder.newClient().register(JacksonFeature.class);

        long initTime = System.currentTimeMillis();
        Response response = client.target(completeURL).request().put(Entity.json(obj));
        long endTime = System.currentTimeMillis();
        LogUtils.logExternalCall(logger, "Object saved on Firebase", "Firebase", "save", uri, 0, response.getStatus(),
                endTime - initTime);

        if (!(response.getStatus() == HttpServletResponse.SC_OK) &&
                !(response.getStatus() == HttpServletResponse.SC_CREATED)) {
            throw new CoCastCallException("Error creating entity " + obj + ". Status = " + response.getStatus(),
                    response.getStatus());
        }
    }

    /**
     * Deletes an object from Firebase
     */
    public void deleteAsRoot(String uri) throws JsonProcessingException, CoCastCallException {

        String completeURL = getFirebaseURLAsRoot(uri);
        Client client = ClientBuilder.newClient().register(JacksonFeature.class);

        long initTime = System.currentTimeMillis();
        Response response = client.target(completeURL).request().delete();
        long endTime = System.currentTimeMillis();
        LogUtils.logExternalCall(logger, "Object deleted from Firebase", "Firebase", "delete", uri, 0, response.getStatus(),
                endTime - initTime);

        if (!(response.getStatus() == HttpServletResponse.SC_OK) &&
                !(response.getStatus() == HttpServletResponse.SC_NO_CONTENT)) {
            throw new CoCastCallException("Error deleting entity with URI = " + uri + ". Status = " + response.getStatus(),
                    response.getStatus());
        }
    }

    /**
     * Fetchs data from Firebase
     */
    public <T> List<T> list(String uri, Class<T> cls) throws IOException {

        String completeURL = getFirebaseURL(uri);
        Client client = ClientBuilder.newClient().register(JacksonFeature.class);

        long initTime = System.currentTimeMillis();
        String strFirebaseResult = client.target(completeURL).request().get(String.class);

        List<T> result = getListFromResult(strFirebaseResult, cls);

        long endTime = System.currentTimeMillis();
        LogUtils.logExternalCall(logger, "List fetched from Firebase", "Firebase", "get", uri, result.size(),
                HttpServletResponse.SC_OK, endTime - initTime);

        return result;
    }

    /**
     * Fetchs data from Firebase
     */
    public <T> List<T> listAsRoot(String uri, Class<T> cls) throws IOException {

        String completeURL = getFirebaseURLAsRoot(uri);
        Client client = ClientBuilder.newClient().register(JacksonFeature.class);

        long initTime = System.currentTimeMillis();
        String strFirebaseResult = client.target(completeURL).request().get(String.class);
        List<T> result = getListFromResult(strFirebaseResult, cls);

        long endTime = System.currentTimeMillis();
        LogUtils.logExternalCall(logger, "List fetched from Firebase", "Firebase", "get", uri, result.size(),
                HttpServletResponse.SC_OK, endTime - initTime);


        return result;
    }

    /**
     * Fetchs data as JSON
     */
    public JsonNode listAsJsonNode(String uri) throws IOException {
        String completeURL = getFirebaseURL(uri);
        Client client = ClientBuilder.newClient().register(JacksonFeature.class);


        long initTime = System.currentTimeMillis();
        String strFirebaseResult = client.target(completeURL).request().get(String.class);
        JsonNode node = objectMapper.readTree(strFirebaseResult);

        long endTime = System.currentTimeMillis();
        LogUtils.logExternalCall(logger, "List fetched as nodes from Firebase", "Firebase", "get", uri, node.size(),
                HttpServletResponse.SC_OK, endTime - initTime);


        return node;
    }

    /**
     * Fetchs a specific data from Firebase
     */
    public <T> T getAsRoot(String uri, Class<T> cls) throws IOException {
        String completeURL = getFirebaseURLAsRoot(uri);


        Client client = ClientBuilder.newClient().register(JacksonFeature.class);

        long initTime = System.currentTimeMillis();
        Response response = client.target(completeURL).request().get();
        T result = response.readEntity(cls);
        long endTime = System.currentTimeMillis();
        LogUtils.logExternalCall(logger, "Object fetched from Firebase", "Firebase", "get", uri, 1,
                HttpServletResponse.SC_OK, endTime - initTime);

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new CoCastCallException(response.readEntity(String.class), response.getStatus());
        }

        return result;
    }

    /**
     * Gets the complete URL
     */
    private String getFirebaseURL(String uri) {

        if (FIREBASE_URL == null) {
            FIREBASE_URL = getConfiguration("firebase.base_url");
        }

        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        if (uri.indexOf("?") == -1) {
            return FIREBASE_URL + uri + "?auth=" + generateToken();
        } else {
            return FIREBASE_URL + uri + "&auth=" + generateToken();
        }

    }

    /**
     * Gets the complete URL as root
     */
    private String getFirebaseURLAsRoot(String uri) {

        if (FIREBASE_URL == null) {
            FIREBASE_URL = getConfiguration("firebase.base_url");
        }
        if (FIREBASE_SECRET == null) {
            FIREBASE_SECRET = getConfiguration("firebase.secret");
        }

        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        if (uri.indexOf("?") == -1) {
            return FIREBASE_URL + uri + "?auth=" + FIREBASE_SECRET;
        } else {
            return FIREBASE_URL + uri + "&auth=" + FIREBASE_SECRET;
        }
    }

    /**
     * Generate token
     */
    private String generateToken() {

        if (FIREBASE_SECRET == null) {
            FIREBASE_SECRET = getConfiguration("firebase.secret");
        }

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

    /**
     * Get configuration
     */
    private String getConfiguration(String key) {
        if (configurationServices == null) {
            configurationServices = ConfigurationServices.getInstance();
        }
        return configurationServices.getString(key, null);
    }
}
