package com.ciandt.dcoder.c2.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.ciandt.dcoder.c2.entity.Person;
import com.ciandt.dcoder.c2.entity.Profile;
import com.ciandt.dcoder.c2.util.APIServices;
import com.ciandt.dcoder.c2.util.GooglePlusServices;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * Class that handles Smart Canvas People API
 * 
 * @author Daniel Viveiros
 */
@Singleton
public class PeopleServices {
	
	@Inject
	private APIServices apiServices;
	
	@Inject
	private Logger logger;
	
	@Inject
    private GooglePlusServices googlePlusServices;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
	
	/**
	 * Lists all persons created inside Smart Canvas
	 */
	public String listPersons() {
	    
	    //creates the builder
	    String apiSpecificPath = "/people/v2/people";
		Builder builder = apiServices.createBuilder(apiSpecificPath); 
		
		//invoke the API
        String response = builder.get(String.class);
        logger.info( "List persons response = " + response);
        
        return response;
	}
	
	/**
     * Search for Smart Canvas persons
     */
    public String searchPersonByEmail(String email) {
        
        String apiSpecificPath = "/people/v2/people/search";
        
        //Parameter
        if ( !StringUtils.isEmpty(email) ) {
            apiSpecificPath += "?email=" + email;
        } 
        
        //creates the builder
        Builder builder = apiServices.createBuilder(apiSpecificPath);
        
        //invoke the API
        String response = builder.get(String.class);
        logger.info( "Search person response = " + response);
        
        return response;
    }
    
    /**
     * Create a new person inside Smart Canvas
     * @throws ParseException 
     */
    public Person createPerson(Person person) throws ParseException {
        
        String apiSpecificPath = "/people/v2/people";
        
        Builder builder = apiServices.createBuilderForPojo(apiSpecificPath);
        ClientResponse response = builder.put(ClientResponse.class, person);

        logger.info( "Create person response:");
        logger.info( ">> Status = " + response.getStatus());
        logger.info( ">> Object = " + response);
        
        return person;
    }
    
    /**
     * Create a new profile inside Smart Canvas
     */
    public Profile createProfile( Person person, Profile profile ) {
        
        String apiSpecificPath = "/people/v2/profiles";
        
        Builder builder = apiServices.createBuilderForPojo(apiSpecificPath);
        ClientResponse response = builder.put(ClientResponse.class, profile);

        logger.info( "Create profile response:");
        logger.info( ">> Status = " + response.getStatus());
        logger.info( ">> Object = " + response);
        
        return profile;
    }
    
    /**
     * Lists all profiles inside Smart Canvas
     */
    public void addRoleToProfile( Long profileId, String roleName ) {
        
        Profile profile = new Profile();
        
        //creates the builder
        String apiSpecificPath = "/people/v2/profiles/" + profileId + "/addrole/" + roleName;
        Builder builder = apiServices.createBuilderForPojo(apiSpecificPath); 
        
        //invoke the API
        ClientResponse response = builder.put(ClientResponse.class, profile);
        logger.info( "Add role " + roleName + " to profile " +  profileId + " = " + response.getStatus());
    }
    
    /**
     * Lists all profiles by providerid and roles
     */
    public String findProfileByRoleNameAndProviderId( String providerId, String roleName ) {
        //creates the builder
        String apiSpecificPath = "/people/v2/profiles/provider/" + providerId + "/role/" + roleName;
        Builder builder = apiServices.createBuilder(apiSpecificPath); 
        
        //invoke the API
        String response = builder.get(String.class);
        logger.info( "Find profiles by provider and role response = " + response);
        
        return response;
    }
       
    /**
     * Creates the profile object to be saved inside Smart Canvas
     */
    public Profile createProfileFromPerson( Person person, Boolean isModerator, Boolean isPublisher ) {
        
        Profile profile = new Profile();
        
        //Make this ID unique, I'll use the person id here
        profile.setId( person.getId() );
        profile.setPersonId( person.getId() );
        profile.setBirthdate( person.getBirthdate() );
        profile.setDisplayName(person.getDisplayName());
        profile.setEmail(person.getEmail());
        profile.setEmployerName(person.getCompany());
        profile.setGender(person.getGender());
        profile.setJobTitle(person.getPosition());
        profile.setLastUpdated(person.getLastUpdate());
        profile.setLocale(person.getLocale());
        profile.setMaritalStatus(person.getMaritalStatus());
        profile.setPosition(person.getPosition());
        
        if (isModerator) {
        	this.addRoleToProfile(profile.getId(), "MODERATOR");
        }
        
        if (isPublisher) {
        	this.addRoleToProfile(profile.getId(), "PUBLISHER");
        }
                
        return profile;
    }
    
    /**
     * Ingest a person from Google Plus to Smart Canvas
     * @throws IOException 
     */
    public void ingestPerson( String userId ) throws IOException {
    	
    	com.google.api.services.plus.model.Person googlePlusPerson = googlePlusServices.getPerson(userId);
		if (googlePlusPerson != null) {
			try {
				Person person = createPersonObject( googlePlusPerson );
				this.createPerson(person);
				
				Profile profile = this.createProfileFromPerson(person, false, true);
				this.completeProfile( profile, googlePlusPerson );
				this.createProfile(person, profile);
			} catch (ParseException e) {
				logger.log(Level.SEVERE, "Error creating person for publisher id = " + userId, e);
				e.printStackTrace();
			}
		}
    }
    
    
    /**
	 * Creates a Smart Canvas person based on Google Plus Person
	 * @throws ParseException 
	 */
	private Person createPersonObject( com.google.api.services.plus.model.Person googlePlusPerson ) throws ParseException {
		Person person = new Person();
		
		person.setId( googlePlusServices.getIdAsLong(googlePlusPerson.getId()) );
		person.setActive( true );
		if ( googlePlusPerson.getBirthday() != null ) {
			person.setBirthdate( sdf.parse(googlePlusPerson.getBirthday()) );
		}
		person.setCompany( "CI&T" );
		person.setDisplayName(googlePlusPerson.getDisplayName());
		person.setGender(googlePlusPerson.getGender());
		person.setLastUpdate( new Date() );
		if ( googlePlusPerson.getPlacesLived() != null ) {
			person.setLocale(googlePlusPerson.getPlacesLived().get(0).getValue());
		}
		person.setMaritalStatus(googlePlusPerson.getRelationshipStatus());
		person.setPosition(googlePlusPerson.getCurrentLocation());
		
		return person;
	}
	
	/**
	 * Complete profile based on Google Plus information
	 */
	private void completeProfile( Profile profile, com.google.api.services.plus.model.Person googlePlusPerson ) {
		profile.setBraggingRights( googlePlusPerson.getBraggingRights() );
		if ( googlePlusPerson.getCover() != null ) {
			profile.setCoverURL(googlePlusPerson.getCover().getCoverPhoto().getUrl() );
		}
		if (googlePlusPerson.getImage() != null ) {
			profile.setImageURL(googlePlusPerson.getImage().getUrl());
		}
		profile.setLastUpdated(new Date());
		profile.setProfileURL(googlePlusPerson.getUrl());
		profile.setProviderId("c2");
		profile.setProviderUserId(googlePlusPerson.getId());
		profile.setIntroduction(googlePlusPerson.getAboutMe());
	}
}
