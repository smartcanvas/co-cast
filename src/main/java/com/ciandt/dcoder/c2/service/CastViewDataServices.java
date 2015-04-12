package com.ciandt.dcoder.c2.service;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import com.ciandt.dcoder.c2.dao.CastViewObjectDAO;
import com.ciandt.dcoder.c2.entity.CastViewObject;
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
public class CastViewDataServices {
	
	@Inject
	private Logger logger;
	
	@Inject
	private CardServices cardServices;
	
	@Inject
	private CastViewObjectDAO castViewObjectDAO;

	/**
	 * Searches for cards inside Smart Canvas and refreshes the cache to serve new information
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public void refreshCardCache() throws JsonProcessingException, IOException {
		String cardJson = cardServices.searchCards("c2", "pt-br", null);
		
		logger.info( cardJson );
		
		ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(cardJson);
        logger.info( "# de cards returned = " + node.size() );
        
        Iterator<JsonNode> nodes = node.elements();
        CastViewObject castViewObject = null;
        while ( nodes.hasNext() ) {
        	JsonNode innerNode = nodes.next();
        	castViewObject = this.createCastViewObject( innerNode );
        	this.enrichData(castViewObject);
        	castViewObjectDAO.save(castViewObject);
        }
	}
	
	/**
	 * Creates a new CastViewObject based on the JSON returned from the Card API search method
	 */
	private CastViewObject createCastViewObject( JsonNode node ) {
		CastViewObject castViewObject = new CastViewObject();
		JsonNode blocks = node.get("blocks");
		JsonNode contentBlock = blocks.get(0);
		JsonNode authorBlock = blocks.get(1);
		JsonNode attachBlock = blocks.get(2);
		JsonNode userActivitiesBlock = blocks.get(3);
		
		castViewObject.setId(node.get("id").asText());
		castViewObject.setAuthorDisplayName( authorBlock.get("authorDisplayName").asText());
		castViewObject.setAuthorId(authorBlock.get("authorId").asText());
		castViewObject.setAuthorImageURL(authorBlock.get("authorImageURL").asText());
		castViewObject.setContent(contentBlock.get("content").asText());
		castViewObject.setContentImageHeight(attachBlock.get("imageHeight").asInt());
		castViewObject.setContentImageWidth(attachBlock.get("imageWidth").asInt());
		castViewObject.setContentImageURL(attachBlock.get("imageURL").asText());
		castViewObject.setCreateDate(new Date(authorBlock.get("publishDate").asLong()));
		Boolean isCasted = (castViewObject.getContent() != null) && (castViewObject.getContent().contains("#cast"));
		castViewObject.setIsCasted(isCasted);
		castViewObject.setLikeCounter(userActivitiesBlock.get("like").asInt());
		castViewObject.setMnemonic(node.get("mnemonic").asText());
		castViewObject.setPinCounter(userActivitiesBlock.get("pin").asInt());
		castViewObject.setProviderId(node.get("providerId").asText());
		castViewObject.setShareCounter( userActivitiesBlock.get("totalCounter").asInt() );
		castViewObject.setSummary( contentBlock.get("summary").asText() );
		castViewObject.setTitle( contentBlock.get("title").asText() );
		
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
	        JsonNode contentNode = node.get(0);
	        JsonNode authorNode = node.get(1);
	        JsonNode categoriesNode = node.get("categoryNames");
	        String strCategories = getCategories( categoriesNode );
	        
	        castViewObject.setCreateDate(new Date(node.get("createDate").asLong()));
	        castViewObject.setUpdateDate(new Date(node.get("updateDate").asLong()));
	        castViewObject.setCategoryNames(strCategories);
	        castViewObject.setProviderContentURL(contentNode.get("providerContentURL").asText());
	        castViewObject.setProviderUserId(authorNode.get("providerUserId").asText());
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
}
