package com.ciandt.d1.cocast.castview;

import java.util.logging.Logger;

import com.ciandt.d1.cocast.card.Card;
import com.ciandt.d1.cocast.util.APIServices;
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
        //logger.info( "Search cards response = " + response);
        
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
        //logger.info( "Card for mnemonic " + mnemonic + " = " + response);
        
        return response;
    }
}
