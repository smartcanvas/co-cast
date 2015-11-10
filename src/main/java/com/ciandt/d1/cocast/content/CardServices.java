package com.ciandt.d1.cocast.content;

import com.ciandt.d1.cocast.configuration.ConfigurationServices;
import com.ciandt.d1.cocast.util.APIServices;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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
	private ConfigurationServices configurationServices;

    //TODO: melhorar isso aqui
    private static final String BASE_URI = "https://d1-tst.appspot.com";
    
    /**
     * Search for a card. This method is better because it returns the numbers of likes, dislikes and so on.
     */
    public String searchCards(String query, String localeCode, Long personId) {
        
        String tenant = configurationServices.get("tenant");
        
        String apiSpecificPath = "/brain/" + tenant + "/card/bucket/cards?q=" + query;
        if ( localeCode != null ) {
            apiSpecificPath += "&locale=" + localeCode;
        } else {
            throw new RuntimeException( "Locale is required for searches");
        }
        if ( personId != null ) {
            apiSpecificPath += "&personId=" + personId;
        }
        
        //Builder builder = APIUtil.createBuilder(apiSpecificPath, queryParam);
        Builder builder = apiServices.createBuilder(BASE_URI, apiSpecificPath, null);
        
        //invoke the API
        String response = builder.get(String.class);
        //logger.info( "Search cards response = " + response);
        
        return response;
    }
    
    /**
     * Search for a card. This method is better because it returns the numbers of likes, dislikes and so on.
     */
    public String getCard(String mnemonic) {
        
        String tenant = configurationServices.get("tenant");
        String apiSpecificPath = "/sc2/" + tenant + "/h/brain/card/bucket/cards/" + mnemonic;
        
        //Builder builder = APIUtil.createBuilder(apiSpecificPath, queryParam);
        Builder builder = apiServices.createBuilder(BASE_URI, apiSpecificPath, null);
        
        //invoke the API
        String response = builder.get(String.class);
        //logger.info( "Card for mnemonic " + mnemonic + " = " + response);
        
        return response;
    }
}
