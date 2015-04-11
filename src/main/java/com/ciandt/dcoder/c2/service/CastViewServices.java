package com.ciandt.dcoder.c2.service;

import java.io.IOException;
import java.util.logging.Logger;

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

	/**
	 * Searches for cards inside Smart Canvas and refreshes the cache to serve new information
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public void refreshCardCache() throws JsonProcessingException, IOException {
		String cardJson = cardServices.searchCards("c2", "br-pt", null);
		
		logger.info( cardJson );
		
		ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(cardJson);
        
        logger.info( "# de cards returned = " + node.size() );
	}
}
