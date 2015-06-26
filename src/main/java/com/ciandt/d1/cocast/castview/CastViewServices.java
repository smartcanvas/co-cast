package com.ciandt.d1.cocast.castview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.ciandt.d1.cocast.configuration.ConfigurationRequiredException;
import com.ciandt.d1.cocast.configuration.ConfigurationUtils;
import com.ciandt.d1.cocast.util.HTMLUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Class responsible for delivering cast views to be rendered in the screen 
 * 
 * @author Daniel Viveiros
 */
@Singleton
public class CastViewServices {
	
	@Inject
	private Logger logger;
	
	@Inject
	private CardServices cardServices;
	
	@Inject
	private CastViewObjectCache castViewObjectCache;
	
	@Inject
	private CastViewObjectDAO castViewObjectDAO;
	
	@Inject
	private WhatsHotCastView whatsHotCastView;
	
	@Inject
	private WhatsNewCastView whatsNewCastView;
	
	@Inject
	private CastedCastView castedCastView;
	
	private List<String> supportedTypes;
	
	private static ConfigurationUtils configurationServices = ConfigurationUtils.getInstance();
	
	private boolean firstTime;
	
	/**
	 * Constructor
	 */
	public CastViewServices() {
		firstTime = true;
		supportedTypes = new ArrayList<String>();
		supportedTypes.add("post");
		supportedTypes.add("photo");
		supportedTypes.add("article");
	}

	/**
	 * Return the cast objects to be shown in a castable device
	 */
	public List<CastViewObject> getCastViewObjects( String mnemonic ) {
		CastView castView = null;
		
		if (firstTime) {
			//register the observers
			castViewObjectCache.registerObserver(whatsHotCastView);
			castViewObjectCache.registerObserver(whatsNewCastView);
			castViewObjectCache.registerObserver(castedCastView);
			
			castViewObjectCache.notifyObservers();
			firstTime = false;
		}
		
		if ( mnemonic.equals(whatsHotCastView.getMnemonic() ) ) {
			castView = whatsHotCastView;
		} else if ( mnemonic.equals(whatsNewCastView.getMnemonic() ) ) {
			castView = whatsNewCastView;
		} else if ( mnemonic.equals(castedCastView.getMnemonic() ) ) {
			castView = castedCastView;
		}
		
		if (castView != null) {
			return castView.castObjects();
		} else {
			throw new RuntimeException( "Invalid mnemonic: " + mnemonic );
		}
	}

	/**
	 * Searches for cards inside Smart Canvas and refreshes the cache to serve new information
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public void fetchContent() throws IOException {
		String cardJson = cardServices.searchCards("c2", "pt-br", null);
		
		List<CastViewObject> listObjects = new ArrayList<CastViewObject>();
		ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(cardJson);
        logger.info( "# de cards returned = " + node.size() );
        //logger.info( "Json returned = " + node );
        
        Iterator<JsonNode> nodes = node.elements();
        CastViewObject castViewObject = null;
        while ( nodes.hasNext() ) {
        	JsonNode innerNode = nodes.next();
        	castViewObject = this.createCastViewObject( innerNode );
        	if ((castViewObject != null) && isSupported(castViewObject)) { 
	        	this.enrichData(castViewObject);
	        	if ( !StringUtils.isEmpty(castViewObject.getTitle())) {
		        	if (castViewObject.getType() == null) {
		        		logger.info("Unable to define the type. Json = " + innerNode);
		        	}
		        	this.changeCastViewObjectImage( castViewObject );
		        	castViewObjectDAO.save(castViewObject);
		        	listObjects.add(castViewObject);
	        	}
        	} else {
        		logger.info("Discarding card: " + innerNode );
        	}
        }
        
        //updates the cache
        castViewObjectCache.loadCache(listObjects);
	}
	
	/**
	 * Reload the content in cache
	 */
	public void loadContent() throws IOException {
		List<CastViewObject> listObjects = castViewObjectDAO.findAll();
		//updates the cache
        castViewObjectCache.loadCache(listObjects);
	}
	
	/**
	 * Checks if this card is supported by C2 or not
	 */
	private Boolean isSupported( CastViewObject obj ) {
		return (obj.getType() != null) && (supportedTypes.contains(obj.getType()));
	}
	
	/**
	 * Creates a new CastViewObject based on the JSON returned from the Card API search method
	 */
	private CastViewObject createCastViewObject( JsonNode node ) {
		//logger.info( "Processing card = " + node.toString());
		CastViewObject castViewObject = new CastViewObject();

		JsonNode contentBlock = getBlockByType( node, "content" );
		JsonNode authorBlock = getBlockByType( node, "author" );
		if (authorBlock == null) {
			return null;
		}
		
		JsonNode attachBlock = getBlockByType( node, "article" );
		if (attachBlock == null) {
			attachBlock = getBlockByType( node, "photo" );
		}
		if (attachBlock == null) {
			attachBlock = getBlockByType( node, "youtube" );
		}
		if (attachBlock == null) {
			attachBlock = getBlockByType( node, "vimeo" );
		}
		if (attachBlock == null) {
			attachBlock = getBlockByType( node, "googleplusvideo" );
		}
		//if null and size = 3, it's just a post
		if (attachBlock == null) {
			JsonNode blocksNode = node.get("blocks");
			if (blocksNode.size() == 3) {
				castViewObject.setType("post");
			}
		}
		
		JsonNode userActivitiesBlock = getBlockByType( node, "userActivity" );
		
		//basic info
		castViewObject.setMnemonic(node.get("mnemonic").asText());
		castViewObject.setProviderId(node.get("providerId").asText());
		
		//author
		castViewObject.setAuthorDisplayName( authorBlock.get("authorDisplayName").asText());
		castViewObject.setAuthorId(authorBlock.get("authorId").asText());
		castViewObject.setAuthorImageURL(authorBlock.get("authorImageURL").asText());
		castViewObject.setDate(new Date(authorBlock.get("publishDate").asLong()));
		
		//content
		if (contentBlock != null) {
			castViewObject.setContent(contentBlock.get("content").asText());
			castViewObject.setSummary( contentBlock.get("summary").asText() );
			castViewObject.setTitle( contentBlock.get("title").asText() );
		}
		
		//attach
		if (attachBlock != null) {
			castViewObject.setType(attachBlock.get("type").asText());
			castViewObject.setContentImageHeight(attachBlock.get("imageHeight").asInt());
			castViewObject.setContentImageWidth(attachBlock.get("imageWidth").asInt());
			if (attachBlock.get("imageURL") != null) {
				castViewObject.setContentImageURL(attachBlock.get("imageURL").asText());
			}
		}
		
		//activity
		castViewObject.setLikeCounter(userActivitiesBlock.get("likeCounter").asInt());
		castViewObject.setPinCounter(userActivitiesBlock.get("pinCounter").asInt());
		castViewObject.setShareCounter( userActivitiesBlock.get("totalCounter").asInt() );
		
		return castViewObject;
	}
	
	/**
	 * Enrich data inside this card
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	private void enrichData( CastViewObject castViewObject ) throws JsonProcessingException, IOException {
		if (castViewObject.getMnemonic() == null) {
			return;
		}
		
		String cardJson = cardServices.getCard( castViewObject.getMnemonic() );
		if (cardJson != null) {
			ObjectMapper mapper = new ObjectMapper();
	        JsonNode node = mapper.readTree(cardJson);
	        logger.info( "Node = " + node );
	        JsonNode contentNode = getBlockByType( node, "content" );
	        JsonNode authorNode = getBlockByType( node, "author" );
	        JsonNode userActivitiesBlock = getBlockByType( node, "userActivity" );
	        JsonNode categoriesNode = node.get("categoryNames");
	        String strCategories = getCategories( categoriesNode );

	        castViewObject.setCategoryNames(strCategories);
	        if (contentNode != null) {
	        	castViewObject.setProviderContentURL(contentNode.get("providerContentURL").asText());
	        	castViewObject.setContent(processContent(contentNode.get("content").asText()));
	        }
	        castViewObject.setProviderUserId(authorNode.get("providerUserId").asText());
	        //activity
			castViewObject.setLikeCounter(userActivitiesBlock.get("likeCounter").asInt());
			castViewObject.setPinCounter(userActivitiesBlock.get("pinCounter").asInt());
			castViewObject.setShareCounter( userActivitiesBlock.get("totalCounter").asInt() );
			
			Boolean isCasted = false;
			if (castViewObject.getCategoryNames() != null && castViewObject.getCategoryNames().contains("cast")) {
				isCasted = true;
			}
			castViewObject.setIsCasted(isCasted);
		}
	}
	
	/**
	 * Changes the cast view object image for a new and better one
	 */
	private void changeCastViewObjectImage( CastViewObject castViewObject ) {
		
		if (castViewObject.getMnemonic() == null) {
			logger.info("Card with mnemonic null detected. Title = " + castViewObject.getTitle() );
			return;
		}
		
		//try to find a configuration for a better image
		try {
			String newImageURL = configurationServices.get(castViewObject.getMnemonic());
			if (StringUtils.isEmpty(newImageURL)) {
				return;
			} else {
				castViewObject.setContentImageURL(newImageURL);
			}
		} catch ( ConfigurationRequiredException exc ) {
			logger.info("No better image found for card with mnemonic = " + castViewObject.getMnemonic() );
		}
	}
	
	/**
	 * Process content
	 */
	private String processContent( String content ) {
		Integer maxContentSize = 240; 
		if (content == null) {
			return "";
		}
		String newContent = StringUtils.trim(HTMLUtils.cleanHTMLTags(content).trim());
		if (newContent.length() <= maxContentSize) {
			return newContent;
		} else {
			return newContent.substring(0,maxContentSize) + "(...)";
		}
	}
	
	/**
	 * Return all categories related to a card
	 */
	private String getCategories( JsonNode categoriesNode ) {
		if ( categoriesNode == null ) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < categoriesNode.size(); i++ ) {
			sb.append(categoriesNode.get(i).asText());
			if (i != (categoriesNode.size() - 1)) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Return the node based on the block with specific type
	 */
	private JsonNode getBlockByType(JsonNode parent, String type) {
		JsonNode blocksNode = parent.get("blocks");
		if (blocksNode == null) {
			return null;
		}
		Iterator<JsonNode> nodes = blocksNode.elements();
        while ( nodes.hasNext() ) {
            JsonNode innerNode = nodes.next();
            if (type.equals(innerNode.get("type").asText())) {
            	return innerNode;
            }
        }
        
        return null;
	}
}
