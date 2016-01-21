package io.cocast.auth;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.cocast.admin.ConfigurationServices;
import io.cocast.util.CacheUtils;
import io.cocast.util.log.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.jose4j.jwt.JwtClaims;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Creates the security claim based on Co-Cast standards
 */
@Singleton
public class SmartCanvasV1SecurityClaimsBuilder extends SecurityClaimsBuilder {

    private static Logger logger = LogManager.getLogger(SmartCanvasV1SecurityClaimsBuilder.class.getName());

    private static CacheUtils cache = CacheUtils.getInstance(SmartCanvasV1Person.class);

    @Inject
    private ConfigurationServices configurationServices;

    @Override
    public SecurityClaims createSecurityClaims(JwtClaims jwtClaims, String issuer) throws Exception {

        SecurityClaims claims = new SecurityClaims(issuer);
        String personId = jwtClaims.getSubject();
        if (StringUtils.isEmpty(personId)) {
            throw new ValidationException("Person ID cannot be null");
        }

        SmartCanvasV1Person person = cache.get(issuer + "_" + personId, new PersonLoader(personId, issuer));

        claims.setEmail(person.getEmail());
        claims.setIssuer(jwtClaims.getIssuer());
        claims.setSubject(person.getEmail());
        claims.setIssuedAt(new Date(jwtClaims.getIssuedAt().getValueInMillis()));
        claims.setProvider("smartcanvasv1");
        claims.setName(person.getDisplayName());

        if (jwtClaims.getExpirationTime() != null)
            claims.setExpirationTime(new Date(jwtClaims.getExpirationTime().getValueInMillis()));

        if (logger.isDebugEnabled()) {
            logger.debug("Security claims readed: " + claims);
            logger.debug("jwtClaims = " + jwtClaims);
        }

        return claims;
    }

    /**
     * Loads a person from Smart Canvas V1
     */
    private class PersonLoader implements Callable<SmartCanvasV1Person> {

        private String issuer;
        private String personId;

        public PersonLoader(String personId, String issuer) {
            this.personId = personId;
            this.issuer = issuer;
        }

        @Override
        public SmartCanvasV1Person call() throws Exception {

            String clientId = configurationServices.getString(issuer + ".client.id");
            String apiKey = configurationServices.getString(issuer + ".api.key");
            String environment = configurationServices.getString("environment");

            if (StringUtils.isEmpty(environment)) {
                throw new ValidationException("Unable to determine environment");
            }
            if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(apiKey)) {
                throw new ValidationException("Could not find client id or api key for tenant " + issuer);
            }

            String completeURL = getSmartCanvasURL(environment);
            Client client = ClientBuilder.newClient().register(JacksonFeature.class);

            if (logger.isDebugEnabled()) {
                logger.debug("Calling smart canvas. URL = " + completeURL);
            }

            long initTime = System.currentTimeMillis();

            SmartCanvasV1Person person = client.target(completeURL).request()
                    .header("CLIENT_ID", clientId)
                    .header("API_KEY", apiKey)
                    .get(SmartCanvasV1Person.class);

            long endTime = System.currentTimeMillis();

            LogUtils.logExternalCall(logger, "Person retrived from Smart Canvas: " + person, "Smart Canvas V1",
                    "get", completeURL, 1, HttpServletResponse.SC_OK, endTime - initTime);

            if (logger.isDebugEnabled()) {
                logger.debug("Returning person = " + person);
            }

            return person;
        }

        private String getSmartCanvasURL(String environment) {
            String baseURL = null;
            if ("prd".equals(environment)) {
                baseURL = "https://d1-prd.appspot.com/brain";
            } else {
                baseURL = "https://d1-dev.appspot.com/brain";
            }

            return baseURL + "/" + this.issuer + "/people/v2/people/" + this.personId;
        }
    }
}
