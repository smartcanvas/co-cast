package com.ciandt.dcoder.c2.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.ciandt.dcoder.c2.config.CommonModule;
import com.ciandt.dcoder.c2.entity.Attachment;
import com.ciandt.dcoder.c2.entity.Card;
import com.ciandt.dcoder.c2.util.ConfigurationServices;
import com.ciandt.dcoder.c2.util.HTMLUtils;
import com.ciandt.dcoder.c2.util.HashtagsUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusRequestInitializer;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.Activity.PlusObject.Attachments;
import com.google.api.services.plus.model.Activity.PlusObject.Attachments.FullImage;
import com.google.api.services.plus.model.Activity.PlusObject.Attachments.Image;
import com.google.api.services.plus.model.ActivityFeed;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class GooglePlusConnector {
	
	@Inject
	private CardServices cardServices;

    @Inject
    private ConfigurationServices configurationServices;
    
    @Inject
	private Logger logger;

    /** Google API key */
    private static String GOOGLE_API_KEY;

    /** Default values for C2 */
    private static String C2_CATEGORY = "c2";
    private static String C2_PROVIDER_ID = "c2";
    
    private static final String GOOGLE_DRIVE_URL_REGEX = "https?://(drive|docs)\\.google\\.com[^\\s]*/(spreadsheets?|file|drawings?|documents?|presentations?)/[^\\s]*";

    /** Google Plus access stub */
    private Plus plus;

    /**
     * Constructor
     */
    public GooglePlusConnector() {
    	//TODO: resolver isso aqui
    	//GOOGLE_API_KEY = configurationServices.get("google_api_key");
    	GOOGLE_API_KEY = "AIzaSyDZIKKCZiHmIyki0yyPWnEUrkgFzw09zUs";
        this.initialize("GooglePlusConnectorLab");
    }

    /**
     * Runs the connector
     * @throws Exception 
     */
    public void execute() throws Exception {
        
        // gets the max number of activities to be retrieved from Google+
        // for this lab, this number is a configuration inside the property file
        Integer maxResults = configurationServices.getInt("max_results");

        //gets the publishers
        List<String> publisherIds = getPublishersIds();
        if ( publisherIds != null ) {
            logger.info("We found " + publisherIds.size() + " publisher(s)");
            for (String publisherId: publisherIds) {
            	logger.info( "Reading posts for publisher with ID = " + publisherId );
                List<Activity> activities = this.listActivities(publisherId, maxResults);
                if (activities != null) {
                	logger.info( activities.size() + " activities found. Let's create some cards!");
                    for (Activity activity: activities) {
                        if (activity.getObject() != null) {
                        	logger.info("Processing activity with id = " + activity.getId() );
                            
                            //converts the activity into a card
                            Card card = convertActivity( activity );
                            
                            //sends the card to Smart Canvas
                            cardServices.createCard( card );
                        }
                    }
                } else {
                	logger.info( "No cards found for publisher with ID = " + publisherId );
                }
            }
        }
    }
    
    /**
     * Gets the profile info based on profiles email
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    /*
    private Person getPerson(String userId) throws IOException {
        Person person = null;
        person = plus.people().get("me").execute();
        return person;
    }
    */
    
    /**
     * Convert an Activity (Google Plus specific object) to a Card
     * @throws Exception 
     */
    private Card convertActivity( Activity activity ) throws Exception {
        Card card = new Card();
        
        card.setContent(this.getContent(activity));
        card.setTitle(this.generateTitle(activity.getTitle()));
        String cardDescription = this.extractDescription(card.getContent());
        card.setDescription(cardDescription);
        card.setAutoModerated(true);
        card.setProviderId( C2_PROVIDER_ID );
        if (activity.getActor() != null) {
            card.setProviderUserId(activity.getActor().getId());
            card.setAuthorImageURL(activity.getActor().getImage().getUrl());
            card.setAuthorDisplayName(activity.getActor().getDisplayName());
        }
        card.setProviderContentURL(activity.getUrl());
        card.setProviderContentId(activity.getId());
        card.setAddress(activity.getAddress());
        card.setPlaceName(activity.getPlaceName());
        card.setGeoCode(activity.getGeocode());

        // dates
        DateTime pubDatetime = activity.getPublished();
        Date pubDate = null;
        if (pubDatetime != null) {
            pubDate = new Date(pubDatetime.getValue());
            card.setProviderPublished(pubDate);
        }
        DateTime uptDatetime = activity.getUpdated();
        Date uptDate = null;
        if (uptDatetime != null) {
            uptDate = new Date(uptDatetime.getValue());
            card.setProviderUpdated(uptDate);
        }

        // attachments
        if (activity.getObject() != null) {
            List<Attachments> listAttachment = activity.getObject().getAttachments();
            if (listAttachment != null) {
                Attachment d1attachment = null;
                for (Attachments attachments : listAttachment) {
                    d1attachment = createAttachment(attachments);
                    card.addAttachment(d1attachment);
                }
            }

            Attachment attachment = extractDriveAttachment(card);
            if (attachment != null) {
                card.addAttachment(attachment);
            }
        }
        
        card.addCategory(C2_CATEGORY);
        return card;
    }
    
    /**
     * Creates an attachment (D1) based on an attachment from Google Plus
     */
    private Attachment createAttachment(final Attachments attachments) throws Exception {
        Attachment attachment = null;
        if (attachments.getObjectType() == null) {
            return null;
        }
        if (attachments.getObjectType().equals("photo")) {
            attachment = new Attachment();
            attachment.setType("photo");
            setAttachmentImageData(attachments, attachment);
        } else if (attachments.getObjectType().equals("video")) {
            attachment = new Attachment();
            attachment.setType("video");
            setAttachmentImageData(attachments, attachment);
            try {
                attachment.setEmbedType(attachments.getEmbed().getType());
                attachment.setEmbedURL(attachments.getEmbed().getUrl());
            } catch (Exception e) {
                throw new Exception("Video attachment embedded object is NULL.", e);
            }
        } else if (attachments.getObjectType().equals("article")) {
            attachment = new Attachment();
            attachment.setType("article");
            setAttachmentImageData(attachments, attachment);
        }

        if (attachment != null) {
            // Commented because type is now set by Jackson
            // attachment.setType( attachments.getObjectType() );
            attachment.setDisplayName(attachments.getDisplayName());
            attachment.setContent(attachments.getContent());
            attachment.setContentURL(attachments.getUrl());
        }
        return attachment;
    }
    
    /**
     * Set the attachment image data.
     * 
     * @param attachment
     *            The google plus attachment
     * @param cardAttachment
     *            The card attachment
     */
    private void setAttachmentImageData(final Attachments attachment, final Attachment cardAttachment) {
        if (attachment != null && cardAttachment != null && attachment.getImage() != null) {
            String originalUrl = "";
            String mimeType = "";
            Long fullImageHeight = null;
            Long fullImageWidth = null;
            Image image = attachment.getImage();
            if ( "photo".equals(cardAttachment.getType()) ) {
                FullImage fullImage = attachment.getFullImage();
                originalUrl = this.getOriginalImageUrl(image, fullImage);
                mimeType = fullImage.getType();
                fullImageHeight = fullImage.getHeight();
                fullImageWidth = fullImage.getWidth();
            } else {
                originalUrl = image.getUrl();
                mimeType = image.getType();
                fullImageHeight = image.getHeight();
                fullImageWidth = image.getWidth();
            }
            cardAttachment.setImageType(mimeType);
            cardAttachment.setImageHeight(fullImageHeight);
            cardAttachment.setImageWidth(fullImageWidth);
            cardAttachment.setOriginalImageURL(originalUrl);
        }
    }
    
    private Attachment extractDriveAttachment(final Card card) {
        Attachment attachment = null;

        Pattern pattern = Pattern.compile(GOOGLE_DRIVE_URL_REGEX);
        Matcher matcher = pattern.matcher(card.getContent());
        matcher.find();
        if (matcher.matches()) {
            String driveUrl = matcher.group();
            attachment = new Attachment();
            attachment.setType( "drive" );
            attachment.setContentURL(driveUrl);
        }

        return attachment;
    }
    
    /**
     * Given an Image and a FullImage object, return a string with the URL
     * pointing to the image with original size.
     * 
     * This is necessary since Image and FullImage objects point go images that
     * are smaller than the originaly uploaded one.
     * 
     * @param image
     *            The image object
     * @param fullImage
     *            The object representing the original uploaded image
     * @return URL string pointing to the image with original size
     */
    private String getOriginalImageUrl(Image image, FullImage fullImage) {
        String imageDimensionUrlPart = "/w" + fullImage.getWidth() + "-" + "h" + fullImage.getHeight();
        return image.getUrl().replaceFirst("/w[0-9]{1,4}-h[0-9]{1,4}", imageDimensionUrlPart);
    }
    
    /**
     * Get Content without specifics html tags.
     * 
     * @param activity
     *            The activity
     * @return The activity content string withou HTML tags
     */
    private String getContent(final Activity activity) {
        if (activity.getObject() == null || activity.getObject().getContent() == null) {
            return null;
        }
        String content = HashtagsUtils.removeHashtagsHref(activity.getObject().getContent());

        // Annotation in an additional content added by the person who shared
        // this activity, applicable only when resharing an activity.
        String annotation = HTMLUtils.cleanHTMLTags(activity.getAnnotation());
        if (annotation != null) {
            content = content + " " + annotation;
        }

        return content;
    }
    
    /**
     * Get Title without specifics html tags and hashtags.
     *
     * @param text
     *            The text to be processed
     * @return The title without specifics html tags and hashtags.
     */
    private String generateTitle(String text) {
        String title = "";

        Integer titleSize = 140;

        title = HashtagsUtils.removeHashtags(text);

        String[] sentences = StringUtils.split(title, ".?!");
        if ((sentences != null) && (sentences.length > 0)) {
            String firstPhrase = sentences[0];
            //add the point, exclamation or question mark
            if (firstPhrase.length() < title.length()) {
                firstPhrase += title.charAt(firstPhrase.length());
            }
            title = firstPhrase;
        } else {
            title = StringUtils.replace(title, "\n", " ");
            title = StringUtils.replace(title, "\r", " ");
            title = StringUtils.replace(title, "  ", " ");
            // title = ExtraStringUtils.stringEncode(title, "UTF-8");
        }

        if (title.length() > titleSize) {
            title = title.substring(0,titleSize - 3) + "...";
        }

        return title;
    }
    
    /**
     * Returns the description based on the content
     *
     * @param content
     *            The string content
     * @return The description of the given content
     */
    private String extractDescription(String content) {
        
        boolean ignoreFirstPhrase = true;
        StringBuffer sb = new StringBuffer();
        Integer maxSize = 140;
        
        content = HashtagsUtils.removeHashtags(content);
        content = HTMLUtils.cleanHTMLTags(content);

        //no content
        if (StringUtils.isBlank(content)) {
            return "";
        }

        if (!content.endsWith(".")) {
            content = content + ".";
        }

        StringTokenizer strToken = new StringTokenizer(content, ".");
        // only one phrase
        if (strToken.countTokens() == 1) {
            return content;
        } else if (ignoreFirstPhrase) {
            strToken.nextToken(); // moves to the second phrase
        }
        String phrase = null;
        int count = 1;
        while (strToken.hasMoreTokens()) {
            phrase = strToken.nextToken();
            if (ignoreFirstPhrase && !phrase.startsWith(" ")) {
                continue;
            }
            phrase = phrase.trim();
            sb.append(phrase + ". ");
            count++;
            if (count > 2) {
                break;
            }
        }
        if (sb.toString().trim().length() == 0) {
            sb.append(content);
        }

        String result = sb.toString().trim();
        result = StringUtils.replace(result, "\n", " ");
        result = StringUtils.replace(result, "\r", " ");
        result = StringUtils.replace(result, "  ", " ");
        if (result.length() > maxSize) {
            result = result.substring(0,maxSize-3) + "...";
        }

        return result;
    }

    /**
     * Get the user id for all the publishers
     */
    private List<String> getPublishersIds() {
        List<String> result = configurationServices.getValues("publisher_ids");
        return result;
    }

    /**
     * List the public activities based on the userId
     * 
     * @param userId
     *            Google Plus user id
     * @param maxResults
     *            Maximum number of results
     */
    private List<Activity> listActivities(String userId, Integer maxResults) throws IOException {
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

    /**
     * Triggers the connector execution
     * 
     * @param args
     *            Command line parameters
     */
    public static void main(String args[]) {
        try {
        	Injector injector = Guice.createInjector(new CommonModule());
            GooglePlusConnector conn = injector.getInstance(GooglePlusConnector.class);
            conn.execute();
        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(-1);
        }

        System.out.println( "Done!" );
        System.exit(0);
    }

}
