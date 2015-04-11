package com.ciandt.dcoder.c2.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusRequestInitializer;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;
import com.google.api.services.plus.model.Person;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Class to handle all Google Plus API services and calls
 * 
 * @author Daniel Viveiros
 */
@Singleton
public class GooglePlusServices {
	
	/** Google Plus access stub */
    private Plus plus;
    
    /** Google API key */
    private static String GOOGLE_API_KEY;
    
    @Inject
    private ConfigurationServices configurationServices;
    
    /**
     * Constructor
     */
    public GooglePlusServices() {
    	GOOGLE_API_KEY = configurationServices.get("google_api_key");
    	//GOOGLE_API_KEY = "AIzaSyDZIKKCZiHmIyki0yyPWnEUrkgFzw09zUs";
        this.initialize("GooglePlusConnectorLab");

    }
	
	/**
     * List the public activities based on the userId
     * 
     * @param userId
     *            Google Plus user id
     * @param maxResults
     *            Maximum number of results
     */
    public List<Activity> listActivities(String userId, Integer maxResults) throws IOException {
        List<Activity> activities = new ArrayList<Activity>();
        Plus.Activities.List listActivities = this.plus.activities().list(userId, "public");

        listActivities.setMaxResults(new Long(maxResults));

        // get the 1st page of activity objects
        ActivityFeed activityFeed = listActivities.execute();

        // unwrap the request and extract the pieces we want
        List<Activity> pageOfActivities = activityFeed.getItems();

        boolean endReached = false;

        // loop through until we arrive at an empty page
        while ((pageOfActivities != null) && (!endReached)) {
            for (Activity activity : pageOfActivities) {
                // it seems that the API is ignoring MaxResults... lets deal
                // with it explicitly
                if (activities.size() < maxResults.intValue()) {
                    activities.add(activity);
                    if (activities.size() == maxResults.intValue()) {
                        endReached = true;
                    }
                } else {
                    break;
                }
            }

            if (!endReached) {
                // we will know we are on the last page when the next page token
                // is null (in which case, break).
                if (activityFeed.getNextPageToken() == null) {
                    break;
                }

                // prepare to request the next page of activities
                listActivities.setPageToken(activityFeed.getNextPageToken());

                // execute and process the next page request
                activityFeed = listActivities.execute();
                pageOfActivities = activityFeed.getItems();
            }
        }

        return activities;
    }
    
    /**
     * Gets the profile info based on profiles email
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public Person getPerson(String userId) throws IOException {
        Person person = null;
        person = plus.people().get("me").execute();
        return person;
    }
    
    
    /**
     * Initialization
     * 
     * @param googleApiKey
     * @param applicationName
     */
    private void initialize(String applicationName) {
        GoogleCredential credential = new GoogleCredential();
        // initializes Google Plus
        JsonFactory jsonFactory = new JacksonFactory();
        HttpTransport httpTransport = new NetHttpTransport();
        this.plus = new Plus.Builder(httpTransport, jsonFactory, credential).setApplicationName(applicationName)
                .setHttpRequestInitializer(credential)
                .setPlusRequestInitializer(new PlusRequestInitializer(GOOGLE_API_KEY)).build();
    }

}
