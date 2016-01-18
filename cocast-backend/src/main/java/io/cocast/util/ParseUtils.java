package io.cocast.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;
import io.cocast.core.SettingsServices;
import io.cocast.util.log.LogUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle Parse notification calls
 */
@Singleton
public class ParseUtils {

    private static final Logger logger = LogManager.getLogger(ParseUtils.class.getName());

    private static final String PARSE_PUSH_NOTIFICATION_ENDPOINT = "https://api.parse.com/1/push";

    protected static final String PARSE_HEADER_APPLICATION_ID = "X-Parse-Application-Id";
    protected static final String PARSE_HEADER_API_KEY = "X-Parse-REST-API-Key";

    private ObjectMapper objectMapper;

    @Inject
    private SettingsServices settingsServices;

    /**
     * Constructor
     */
    @Inject
    public ParseUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void send(String networkMnemonic, ParseMessage message) throws Exception {

        long initTime = System.currentTimeMillis();

        String parseApiKey = settingsServices.getString(networkMnemonic, "parse-api-key");
        String parseApplicationId = settingsServices.getString(networkMnemonic, "parse-application-id");

        Client client = ClientBuilder.newClient().register(JacksonFeature.class);
        Response response = client.target(PARSE_PUSH_NOTIFICATION_ENDPOINT).request()
                .accept(MediaType.APPLICATION_JSON)
                .header(PARSE_HEADER_API_KEY, parseApiKey)
                .header(PARSE_HEADER_APPLICATION_ID, parseApplicationId)
                .post(Entity.json(message));
        long endTime = System.currentTimeMillis();

        String strResponse = response.readEntity(String.class);
        JsonNode jsonNode = objectMapper.readTree(strResponse);
        Boolean success = jsonNode.get("result").asBoolean();
        Integer code = response.getStatus();

        if ((code != 200) || (!success)) {
            LogUtils.fatal(logger, "Parse error sending to " + message, null);
        }

        LogUtils.logExternalCall(logger, strResponse, "Parse", "send", message.toString(),
                0, response.getStatus(), endTime - initTime);
    }

    /**
     * Parse message
     */
    public static class ParseMessage {

        private Map<String, Object> where;
        private ParseData data;
        private String title;

        /**
         * Constructor
         */
        public ParseMessage(String deviceType, String deviceToken, String title, String body,
                            Map<String, Object> payload) {
            this.title = title;
            this.data = new ParseData(title, body, payload);
            this.where = new HashMap<>();
            where.put("deviceType", deviceType);
            where.put("deviceToken", deviceToken);
        }

        public ParseData getData() {
            return data;
        }

        public Map<String, Object> getWhere() {
            return where;
        }

        @Override
        public String toString() {
            return "parseMessage: deviceToken = " + where.get("deviceToken") + ", title = " + this.title;
        }
    }

    /**
     * Parse payload
     */
    private static class ParseData {
        private Map<String, Object> payload;
        private Map<String, Object> alert;

        public ParseData(String title, String body, Map<String, Object> payload) {
            alert = new HashMap<>();
            alert.put("title", title);
            alert.put("body", body);

            this.payload = payload;
        }

        public Map<String, Object> getPayload() {
            return payload;
        }


        public Map<String, Object> getAlert() {
            return alert;
        }
    }
}
