package io.cocast.util.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.cocast.admin.ConfigurationServices;
import io.cocast.auth.SecurityContext;
import io.cocast.config.BasicBackendModule;

/**
 * Represents a loggable object with some attributes in common
 */
abstract class LoggableObject {

    private static ConfigurationServices configurationServices;
    private static ObjectMapper objectMapper;

    private String userId;
    private String userEmail;
    private String environment;
    private String logType;
    private String message;

    static {
        Injector injector = Guice.createInjector(new BasicBackendModule());
        configurationServices = injector.getInstance(ConfigurationServices.class);

        objectMapper = new ObjectMapper();
        objectMapper.getFactory()
                .configure(com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII, true)
                .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.registerModule(new JodaModule());
        objectMapper.registerModule(new AfterburnerModule());
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public LoggableObject(String message) {
        this.message = message;
        this.environment = configurationServices.getString("environment");

        if (SecurityContext.get() != null) {
            this.userId = SecurityContext.get().userIdentification();
            this.userEmail = SecurityContext.get().email();
        }

        this.logType = this.logType();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public abstract String logType();

    public String toJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }
}
