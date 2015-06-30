package com.ciandt.d1.cocast.castview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.ciandt.d1.cocast.configuration.ConfigurationRequiredException;
import com.ciandt.d1.cocast.configuration.ConfigurationServices;
import com.ciandt.d1.cocast.content.CardServices;
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
	private CastViewDAO castViewDAO;
	
	@Inject
    private ConfigurationServices configurationServices;
	
	@Inject
	private Map<String, CastViewStrategy> mapCastViewStrategy;
	
	private List<String> supportedTypes;
	
	
	/**
	 * Constructor
	 */
	public CastViewServices() {
		supportedTypes = new ArrayList<String>();
		supportedTypes.add("post");
		supportedTypes.add("photo");
		supportedTypes.add("article");
	}

	/**
	 * Return the cast objects to be shown in a castable device
	 */
	public List<CastViewObject> getCastViewObjects( String mnemonic ) {
	    
	    CastView castView = castViewDAO.findByMnemonic(mnemonic);
	    if (castView == null) {
	        throw new RuntimeException( "Cast view not found for mnemonic: " + mnemonic );
	    }
	    
	    CastViewStrategy castViewStrategy = mapCastViewStrategy.get(castView.getStrategy());
	    return castViewStrategy.castObjects(castView);
	}

	/**
	 * Searches for cards inside Smart Canvas and refreshes the cache to serve new information
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public void fetchContent() throws IOException {
	    String searchTerm = configurationServices.get("search_term");
	    String locale = configurationServices.get("search_locale");
	    
	    logger.info( "Fetching content from Smart Canvas. Term = " + searchTerm + ", locale = " + locale );
	    
		String cardJson = cardServices.searchCards( searchTerm, locale, null);
		
		List<CastViewObject> listObjects = new ArrayList<CastViewObject>();
		ObjectMapper mapper = new ObjectMapper();
        JsonNode bucketList = mapper.readTree(cardJson).get("buckets");
        logger.info( "# de buckets returned = " + bucketList.size() );
        if( (bucketList == null) || (bucketList.size()==0) ) {
            return;
        }
        
        Iterator<JsonNode> buckets = bucketList.elements();
        CastViewObject castViewObject = null;
        while ( buckets.hasNext() ) {
        	JsonNode bucket = buckets.next();
        	JsonNode cardList = bucket.get("cards");
        	if ((cardList == null) || (cardList.size() == 0)) {
        	    continue;
        	}
        	Iterator<JsonNode> cards = cardList.elements();
        	while (cards.hasNext()) {
        	    JsonNode card = cards.next();
            	castViewObject = this.createCastViewObject( card );
            	if ((castViewObject != null) && isSupported(castViewObject)) { 
    	        	this.enrichData(castViewObject);
    	        	if ( !StringUtils.isEmpty(castViewObject.getTitle())) {
    		        	if (castViewObject.getType() == null) {
    		        		logger.info("Unable to define the type. Json = " + card);
    		        	}
    		        	this.changeCastViewObjectImage( castViewObject );
    		        	castViewObjectDAO.save(castViewObject);
    		        	listObjects.add(castViewObject);
    	        	}
            	} else {
            		logger.info("Discarding card: " + card );
            	}
        	}
        }
        
        //updates the cache
        castViewObjectCache.loadCache(listObjects);
	}
	
	/**
	 * Reload the content in cache
	 */
	public void reload() throws IOException {
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
		if (authorBlock.get("providerUserId") != null) {
		    castViewObject.setProviderUserId(authorBlock.get("providerUserId").asText());
		}
		castViewObject.setDate(new Date(authorBlock.get("publishDate").asLong()));
		
		//content
		if (contentBlock != null) {
		    if ( (contentBlock.get("content") != null) && !"null".equals((contentBlock).get("content").asText() )) {
		        castViewObject.setContent(contentBlock.get("content").asText());
		    } else {
		        castViewObject.setContent("");
		    }
		    if ( (contentBlock.get("summary") != null) && !"null".equals((contentBlock).get("summary").asText() )) {
		        castViewObject.setSummary( contentBlock.get("summary").asText() );
		    } else {
		        castViewObject.setSummary("");
		    }
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
		if (userActivitiesBlock.get("likeCounter") != null) {
		    castViewObject.setLikeCounter(userActivitiesBlock.get("likeCounter").asInt());
		} else {
		    castViewObject.setLikeCounter(0);
		}
		if (userActivitiesBlock.get("pinCounter") != null) {
		    castViewObject.setPinCounter(userActivitiesBlock.get("pinCounter").asInt());
		} else {
		    castViewObject.setPinCounter(0);
		}
		if (userActivitiesBlock.get("totalCounter") != null) {
		    castViewObject.setShareCounter( userActivitiesBlock.get("totalCounter").asInt() );
		} else {
		    castViewObject.setShareCounter(0);
		}
		
		//categories
		JsonNode categoriesNode = node.get("categoryNames");
		String strCategories = getCategories( categoriesNode );
        castViewObject.setCategoryNames(strCategories);
        String castHashtag = configurationServices.get("casted_hashtag");
        if (castViewObject.getCategoryNames() != null && castViewObject.getCategoryNames().contains(castHashtag)) {
            castViewObject.setIsCasted(true);
        } else {
            castViewObject.setIsCasted(false);
        }
		
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
		JsonNode node = null;
		if (cardJson != null) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode bucketList = mapper.readTree(cardJson).get("buckets");
	        if( (bucketList == null) || (bucketList.size()==0) ) {
	            return;
	        }
	        
	        Iterator<JsonNode> buckets = bucketList.elements();
	        if ( buckets.hasNext() ) {
	            JsonNode cardList = buckets.next().get("cards");
	            if( (cardList == null) || (cardList.size()==0) ) {
	                return;
	            } else {
	                node = cardList.get(0);
	            }
	        }
	        
	        
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
			String castHashtag = configurationServices.get("casted_hashtag");
			if (castViewObject.getCategoryNames() != null && castViewObject.getCategoryNames().contains(castHashtag)) {
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
