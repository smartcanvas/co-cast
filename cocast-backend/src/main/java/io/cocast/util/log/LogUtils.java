package io.cocast.util.log;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import io.cocast.admin.ConfigurationServices;
import io.cocast.auth.SecurityContext;
import io.cocast.config.BasicBackendModule;
import io.cocast.util.AbstractRunnable;
import io.cocast.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

/**
 * Utility class for logging
 */
public class LogUtils {

    private static ConfigurationServices configurationServices;

    static {
        Injector injector = Guice.createInjector(new BasicBackendModule());
        configurationServices = injector.getInstance(ConfigurationServices.class);
    }

    /**
     * Logs an API execution
     */
    public static void logAPIExecution(Logger logger, String message, String module, String resource, String method,
                                       Integer numberResults, String requester, Integer status, Long execTime) {

        try {
            if (logger.isInfoEnabled()) {
                APIExecution apiExecution = new APIExecution(message, module, resource, method, numberResults,
                        requester, status, execTime);
                logger.info(apiExecution.toJson());
            }
        } catch (Exception exc) {
            logger.error("Error writing API execution log", exc);
        }
    }

    /**
     * Logs an external call
     */
    public static void logExternalCall(Logger logger, String message, String externalApp, String operation, String source,
                                       Integer numberResults, Integer status, Long executionTime) {

        try {
            if (logger.isInfoEnabled()) {
                ExternalCall externalCall = new ExternalCall(message, externalApp, operation, source, numberResults,
                        status, executionTime);
                logger.info(externalCall.toJson());
            }
        } catch (Exception exc) {
            logger.error("Error writing External Call log", exc);
        }
    }

    /**
     * Logs an asynchronous execution
     */
    public static void logAsyncExecution(Logger logger, AbstractRunnable runnable, String message, Long executionTime,
                                         boolean success, Integer retries) {
        try {
            if (logger.isInfoEnabled()) {
                AsyncExecution asyncExecution = new AsyncExecution(runnable, message, executionTime, success, retries);
                logger.info(asyncExecution.toJson());
            }
        } catch (Exception exc) {
            logger.error("Error writing Async Call log", exc);
        }
    }

    /**
     * Logs a fatal exception, that also sends an email to report the issue
     */
    public static void fatal(Logger logger, String message, Throwable t) {

        //logs the error
        logger.fatal(message, t);

        String environment = configurationServices.getString("environment");
        if (StringUtils.isEmpty(environment) || "local".equals(environment)) {
            return;
        }

        //sends an email
        String apiKey = configurationServices.getString("sendgrid.key");
        SendGrid sendgrid = new SendGrid(apiKey);

        SendGrid.Email email = new SendGrid.Email();

        //to
        String strTo = configurationServices.getString("sendgrid.email.to");
        String[] aStrTo = strTo.split(",");
        for (int i = 0; i < aStrTo.length; i++) {
            email.addTo(aStrTo[i].trim());
        }
        email.setFrom("no-reply@cocast.io");
        email.setSubject("[Co-Cast] " + message);

        //compose the message
        StringBuffer sb = new StringBuffer();
        sb.append("<b>Message</b>: " + message + "<br>");
        sb.append("<b>User ID</b>: " + SecurityContext.get().userIdentification() + "<br>");
        sb.append("<b>Email</b>: " + SecurityContext.get().email() + "<br>");
        sb.append("<b>When</b>: " + DateUtils.now() + "<br>");
        sb.append("<b>Environment</b>: " + environment + "<br>");

        if (t != null) {
            sb.append("<hr>");
            sb.append("<b>Stack trace</b>: <br><br><pre><code>");
            sb.append(ExceptionUtils.getStackTrace(t));
            sb.append("</code></pre>");
        }

        email.setHtml(sb.toString());

        try {
            SendGrid.Response response = sendgrid.send(email);
            if (response.getCode() != 200) {
                logger.warn("Sendgrid is returning an error code: " + response.getCode() + " with message "
                        + response.getMessage());
            }
        } catch (SendGridException e) {
            logger.error("Error sending email from Sendgrid", e);
        }
    }
}
