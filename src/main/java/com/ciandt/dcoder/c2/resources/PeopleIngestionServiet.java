package com.ciandt.dcoder.c2.resources;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ciandt.dcoder.c2.entity.Person;
import com.ciandt.dcoder.c2.entity.Profile;
import com.ciandt.dcoder.c2.service.PeopleServices;
import com.ciandt.dcoder.c2.util.ConfigurationServices;
import com.ciandt.dcoder.c2.util.GooglePlusServices;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class PeopleIngestionServiet extends HttpServlet {
	
	@Inject
	private Logger logger;
	
	@Inject
    private ConfigurationServices configurationServices;
	
	@Inject
    private GooglePlusServices googlePlusServices;
	
	@Inject
	private PeopleServices peopleServices;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
	
	/**
	 * Executes the servlet
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		logger.info( "Executing PeopleIngestionServiet" );
		List<String> publisherIds = this.getPublishersIds();
		
		for ( String publisherId: publisherIds ) {
			com.google.api.services.plus.model.Person googlePlusPerson = googlePlusServices.getPerson(publisherId);
			if (googlePlusPerson != null) {
				try {
					Person person = createPerson( googlePlusPerson );
					peopleServices.createPerson(person);
					
					Profile profile = peopleServices.createProfileFromPerson(person, false, true);
					this.completeProfile( profile, googlePlusPerson );
					peopleServices.createProfile(person, profile);
				} catch (ParseException e) {
					logger.log(Level.SEVERE, "Error creating person for publisher id = " + publisherId, e);
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	/**
	 * Creates a Smart Canvas person based on Google Plus Person
	 * @throws ParseException 
	 */
	private Person createPerson( com.google.api.services.plus.model.Person googlePlusPerson ) throws ParseException {
		Person person = new Person();
		
		person.setId( Long.parseLong(googlePlusPerson.getId()) );
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
	}
	
	/**
     * Get the user id for all the publishers
     */
    private List<String> getPublishersIds() {
        List<String> result = configurationServices.getValues("publisher_ids");
        return result;
    }

}
