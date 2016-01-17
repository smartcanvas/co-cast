package io.cocast.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Key;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
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
import java.util.Map;

/**
 * Utility class to handle Google Cloud Messaging calls
 */
@Singleton
public class GCMUtils {

    private static final Logger logger = LogManager.getLogger(GCMUtils.class.getName());

    private static final String GCM_ENDPOINT = "https://gcm-http.googleapis.com/gcm/send";
    private static final String CONF_KEY_GCM_TOKEN = "gcm.service_token";
    private static final String MISSING_CONFIGURATION_BASE_MESSAGE = "Make sure %s configuration is properly set.";

    private ObjectMapper objectMapper;

    @Inject
    private ConfigurationServices configurationServices;

    public static class GCMMessage {

        @Key
        private String to;

        @Key
        private Map<String, Object> data;

        public GCMMessage(String to, Map<String, Object> data) {
            super();
            this.to = to;
            this.data = data;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "GCMMessage{" +
                    "to='" + to + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

    @Inject
    public GCMUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void send(GCMMessage message) throws IOException {
        logger.debug(String.format("Sending message to GCM: %s", message));
        long initTime = System.currentTimeMillis();
        Client client = ClientBuilder.newClient().register(JacksonFeature.class);
        Response response = client.target(GCM_ENDPOINT).request()
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "key=" + gcmToken())
                .post(Entity.json(message));
        long endTime = System.currentTimeMillis();
        String strResponse = response.readEntity(String.class);
        JsonNode jsonNode = objectMapper.readTree(strResponse);
        Integer intSuccess = jsonNode.get("success").asInt();
        Integer code = response.getStatus();

        if ((code != 200) || (intSuccess != 1)) {
            LogUtils.fatal(logger, "GCM error sending to " + message.getTo(), null);
        }

        LogUtils.logExternalCall(logger, strResponse, "GCM", "send", message.getTo(),
                0, response.getStatus(), endTime - initTime);
    }


    private String gcmToken() {
        String token = configurationServices.getString(CONF_KEY_GCM_TOKEN);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(token),
                String.format(MISSING_CONFIGURATION_BASE_MESSAGE, CONF_KEY_GCM_TOKEN));

        return token;
    }


}
