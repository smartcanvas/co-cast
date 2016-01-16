package io.cocast.connector.sc1;

import io.cocast.connector.sc1.model.Bucket;
import io.cocast.connector.sc1.model.Card;
import io.cocast.connector.sc1.model.Content;
import io.cocast.connector.sc1.model.Search;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class SmartCanvasConnector {

    //logger
    private static Logger logger = LogManager.getLogger(SmartCanvasConnector.class.getName());

    //default fetching interval
    private static final Long DEFAULT_POLLING_INTERVAL = 300000L;

    //default locale
    private static final String DEFAULT_LOCALE = "zz-zz";

    //header values
    public static final String X_D1_USER = "5734017160708096";
    public static final String X_D1_PROVIDER = "googleplusdomain";
    public static final String X_D1_PROVIDER_USER_ID = "111766501721342558332";

    //Co-cast APIs base URL
    private static final String COCAST_BASE_URI_LOCAL = "http://localhost:8080/api";
    private static final String COCAST_BASE_URI_DEV = "https://api-dev.cocast.io";
    private static final String COCAST_BASE_URI_PRD = "https://api.cocast.io";

    //configuration
    private static Configuration cfg = new EnvironmentConfiguration();

    private static String COCAST_API_KEY;
    private static String COCAST_CLIENT_ID;
    private static String COCAST_NETWORK;
    private static String SMART_CANVAS_API_KEY;
    private static String SMART_CANVAS_CLIENT_ID;
    private static String SMART_CANVAS_TENANT;
    private static String SEARCH_TERMS;
    private static String LOCALE;
    private static Long POLLING_INTERVAL;
    private static String ENV;
    private static String COCAST_BASE_URI;

    /**
     * Executes the connection between Smart Canvas and Co-Cast
     */
    private void execute() throws Exception {

        if (SEARCH_TERMS != null) {
            String[] terms = SEARCH_TERMS.split(":");
            logger.debug("Found " + terms.length + " terms to search");
            for (int i = 0; i < terms.length; i++) {
                this.executeSearch(terms[i]);
            }
        } else {
            //let's search for everything
            this.executeSearch("");
        }
    }

    /**
     * Performs the search for a specific search term
     */
    private void executeSearch(String searchTerm) throws Exception {

        logger.info("Searching for '" + searchTerm + "'");

        String completeURL = getSmartCanvasURL(searchTerm);
        Client client = ClientBuilder.newClient().register(JacksonFeature.class);
        if (logger.isDebugEnabled()) {
            logger.debug("Smart Canvas URL = " + completeURL);
        }

        try {
            Search searchResult = client.target(completeURL).request()
                    .header("CLIENT_ID", SMART_CANVAS_CLIENT_ID)
                    .header("API_KEY", SMART_CANVAS_API_KEY)
                    .header("x-d1-user", X_D1_USER)
                    .header("x-d1-provider", X_D1_PROVIDER)
                    .header("x-d1-provider-user-id", X_D1_PROVIDER_USER_ID)
                    .get(Search.class);

            sendToCoCast(searchResult);

            if (logger.isDebugEnabled()) {
                logger.debug("Search result returned " + searchResult.getBucketSize() + " buckets");
            }
        } catch (Exception exc) {
            logger.error("Error processing search term = " + searchTerm, exc);
        }
    }

    /**
     * Sends the results to Co-Cast
     */
    private void sendToCoCast(Search searchResult) {
        List<Bucket> buckets = searchResult.getBuckets();
        for (Bucket bucket : buckets) {
            this.processBucket(bucket);
        }
    }

    /**
     * Process a bucket
     */
    private void processBucket(Bucket bucket) {
        if (bucket == null) {
            return;
        }

        List<Card> cards = bucket.getCards();
        for (Card card : cards) {
            processCard(card);
        }
    }

    /**
     * Process a card
     */
    private void processCard(Card card) {
        if (card == null) {
            return;
        }

        try {
            Content content = ContentConverter.convert(card, COCAST_NETWORK, SMART_CANVAS_TENANT);

            String completeURL = getCoCastURL();
            Client client = ClientBuilder.newClient().register(JacksonFeature.class);

            if (logger.isDebugEnabled()) {
                logger.debug("Co-Cast URL = " + completeURL);
            }

            Response postResponse = client.target(completeURL).request()
                    .header("x-root-token", COCAST_API_KEY)
                    .header("x-client-id", COCAST_CLIENT_ID)
                    .post(Entity.entity(content, MediaType.APPLICATION_JSON));

            if (!(postResponse.getStatus() == 201)) {
                logger.error("Error sending content to Co-Cast. Response was " + postResponse.getStatus());
            }

        } catch (Exception exc) {
            logger.error("Error processing card " + card, exc);
        }
    }

    /**
     * Returns the URL for Smart Canvas endpoint
     */
    private String getSmartCanvasURL(String searchTerm) throws UnsupportedEncodingException {
        return "http://" + SMART_CANVAS_TENANT + ".smartcanvas.com/brain/card/bucket/cards?locale=" + LOCALE
                + "&q=" + URLEncoder.encode(searchTerm, "UTF-8");
    }

    /**
     * Return the URL for Co-Cast endpoint
     */
    private String getCoCastURL() {
        return COCAST_BASE_URI + "/core/v1/contents/" + COCAST_NETWORK;
    }

    /**
     * Triggers the execution
     */
    public static void main(String[] args) throws Throwable {

        logger.info("Initializing SmartCanvasConnector (V1)");

        COCAST_API_KEY = cfg.getString("COCAST_API_KEY");
        COCAST_CLIENT_ID = cfg.getString("COCAST_CLIENT_ID");
        COCAST_NETWORK = cfg.getString("COCAST_NETWORK");
        SMART_CANVAS_API_KEY = cfg.getString("SMART_CANVAS_API_KEY");
        SMART_CANVAS_CLIENT_ID = cfg.getString("SMART_CANVAS_CLIENT_ID");
        SMART_CANVAS_TENANT = cfg.getString("SMART_CANVAS_TENANT");
        SEARCH_TERMS = cfg.getString("SEARCH_TERMS");
        LOCALE = cfg.getString("LOCALE");
        if (StringUtils.isEmpty(LOCALE)) {
            LOCALE = DEFAULT_LOCALE;
        }
        if (!StringUtils.isEmpty(cfg.getString("POLLING_INTERVAL"))) {
            POLLING_INTERVAL = cfg.getLong("POLLING_INTERVAL");
        } else {
            POLLING_INTERVAL = DEFAULT_POLLING_INTERVAL;
        }
        ENV = cfg.getString("ENV");
        if (StringUtils.isEmpty(ENV)) {
            ENV = "local";
        }
        if ("local".equals(ENV)) {
            COCAST_BASE_URI = COCAST_BASE_URI_LOCAL;
        } else if ("dev".equals(ENV)) {
            COCAST_BASE_URI = COCAST_BASE_URI_DEV;
        } else if ("prd".equals(ENV)) {
            COCAST_BASE_URI = COCAST_BASE_URI_PRD;
        } else {
            logger.error("Invalid environment: " + ENV);
            System.exit(-1);
        }

        //DEBUG
        if (logger.isDebugEnabled()) {
            logger.debug("COCAST_API_KEY = " + COCAST_API_KEY);
            logger.debug("COCAST_CLIENT_ID = " + COCAST_CLIENT_ID);
            logger.debug("COCAST_NETWORK = " + COCAST_NETWORK);
            logger.debug("SMART_CANVAS_API_KEY = " + SMART_CANVAS_API_KEY);
            logger.debug("SMART_CANVAS_CLIENT_ID = " + SMART_CANVAS_CLIENT_ID);
            logger.debug("SMART_CANVAS_TENANT = " + SMART_CANVAS_TENANT);
            logger.debug("SEARCH_TERMS = " + SEARCH_TERMS);
            logger.debug("LOCALE = " + LOCALE);
            logger.debug("POLLING_INTERVAL = " + POLLING_INTERVAL);
            logger.debug("COCAST_BASE_URI = " + COCAST_BASE_URI);
        }

        if ((COCAST_API_KEY == null) || (COCAST_CLIENT_ID == null)
                || (COCAST_NETWORK == null) || (SMART_CANVAS_API_KEY == null)
                || (SMART_CANVAS_CLIENT_ID == null) || (SMART_CANVAS_TENANT == null)) {
            logger.error("Invalid parameters. Required = COCAST_API_KEY, COCAST_CLIENT_ID, COCAST_NETWORK, "
                    + "SMART_CANVAS_API_KEY, SMART_CANVAS_CLIENT_ID, SMART_CANVAS_TENANT");
            System.exit(-1);
        }

        SmartCanvasConnector connector = new SmartCanvasConnector();

        while (true) {

            try {
                connector.execute();
            } catch (Throwable exc) {
                logger.error("Exception raised while executing Smart Canvas connector: " + exc.getMessage(), exc);
            }

            Thread.sleep(POLLING_INTERVAL);
        }
    }


}
