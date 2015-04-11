package com.ciandt.dcoder.c2.service;

import java.util.logging.Logger;

import com.ciandt.dcoder.c2.entity.Card;
import com.ciandt.dcoder.c2.util.APIServices;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * Services that uses Smart Canvas Card API to manage cards 
 * 
 * @author Daniel Viveiros
 */
@Singleton
public class CardServices {

	@Inject
	private APIServices apiServices;
	
	@Inject
	private Logger logger;
    
    /**
     * Create a new person inside Smart Canvas
     */
    public Card createCard( Card card ) {
        
        String apiSpecificPath = "/card/v2/cards";

        Builder builder = apiServices.createBuilderForPojo(apiSpecificPath);
        ClientResponse response = builder.put(ClientResponse.class, card);

        logger.info( "Create card response:");
        logger.info( ">> Status = " + response.getStatus());
        logger.info( ">> Object = " + response);
        
        return card;
    }
    
    /**
     * Search for a card. This method is better because it returns the numbers of likes, dislikes and so on.
     */
    public String searchCards(String query, String localeCode, Long personId) {
        
        String apiSpecificPath = "/sc2/d-coder/h/brain/card/v3/cards?q=" + query;
        if ( localeCode != null ) {
            apiSpecificPath += "&locale=" + localeCode;
        } else {
            throw new RuntimeException( "Locale is required for searches");
        }
        if ( personId != null ) {
            apiSpecificPath += "&personId=" + personId;
        }
        
        //Builder builder = APIUtil.createBuilder(apiSpecificPath, queryParam);
        Builder builder = apiServices.createBuilder("https://d1-prd.appspot.com",
                apiSpecificPath, null);
        
        //invoke the API
        String response = builder.get(String.class);
        logger.info( "Search cards response = " + response);
        
        return response;
    }
    
    /**
     * Search for a card. This method is better because it returns the numbers of likes, dislikes and so on.
     */
    public String getCard(String mnemonic) {
        
        String apiSpecificPath = "/sc2/d-coder/h/brain/card/v3/cards/" + mnemonic;
        
        //Builder builder = APIUtil.createBuilder(apiSpecificPath, queryParam);
        Builder builder = apiServices.createBuilder("https://d1-prd.appspot.com",
                apiSpecificPath, null);
        
        //invoke the API
        String response = builder.get(String.class);
        logger.info( "Card for mnemonic " + mnemonic + " = " + response);
        
        return response;
    }
  
}
