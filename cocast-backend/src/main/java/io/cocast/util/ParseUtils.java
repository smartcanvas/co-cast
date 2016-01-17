package io.cocast.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;
import io.cocast.admin.ConfigurationServices;
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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Utility class to handle Parse notification calls
 */
@Singleton
public class ParseUtils {

    private static final Logger logger = LogManager.getLogger(ParseUtils.class.getName());

    private static final String PARSE_PUSH_NOTIFICATION_ENDPOINT = "https://api.parse.com/1/push";

    private static final String CONF_KEY_PARSE_APPLICATION_ID = "parse.application_id";
    private static final String CONF_KEY_PARSE_API_KEY = "parse.api_key";

    protected static final String PARSE_HEADER_APPLICATION_ID = "X-Parse-Application-Id";
    protected static final String PARSE_HEADER_API_KEY = "X-Parse-REST-API-Key";

    private List<String> channels;
    private String parseApiKey;
    private String parseApplicationId;

    private ObjectMapper objectMapper;

    /**
     * Constructor
     */
    @Inject
    public ParseUtils(ConfigurationServices configurationServices, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        String strChannels = configurationServices.getString("parse.channels");
        if (strChannels == null) {
            LogUtils.fatal(logger, "No channels defined for Parse", null);
        }

        this.channels = Arrays.asList(strChannels.split(","));
        this.parseApiKey = configurationServices.getString("parse.api_key");
        this.parseApplicationId = configurationServices.getString("parse.application_id");
    }

    public void send(ParseMessage message) throws IOException {

        message.setChannels(channels);

        logger.debug(String.format("Sending message to Parse: %s", message));
        long initTime = System.currentTimeMillis();
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
            LogUtils.fatal(logger, "Parse error sending to " + channels, null);
        }

        LogUtils.logExternalCall(logger, strResponse, "Parse", "send", channels.toString(),
                0, response.getStatus(), endTime - initTime);
    }

    /**
     * Parse message
     */
    public static class ParseMessage {

        private List<String> channels;
        private Map<String, Object> data;

        /**
         * Constructor
         */
        public ParseMessage(Map<String, Object> data) {
            this.data = data;
        }

        public List<String> getChannels() {
            return channels;
        }

        private void setChannels(List<String> channels) {
            this.channels = channels;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "ParseMessage{" +
                    "channels=" + channels +
                    ", data=" + data +
                    '}';
        }
    }
}
