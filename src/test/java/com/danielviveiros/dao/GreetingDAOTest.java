package com.danielviveiros.dao;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.danielviveiros.dao.GreetingDAO;
import com.danielviveiros.dao.ObjectifyGreetingDAO;
import com.danielviveiros.entity.Greeting;
import com.danielviveiros.test.AbstractTestClass;
import com.google.appengine.api.users.User;

public class GreetingDAOTest extends AbstractTestClass {
	
	@Override
	public void setup() {
		dao = super.getInstance(ObjectifyGreetingDAO.class);
	}

	private GreetingDAO dao;

	@Test
	public void shouldBeAbleToInsertAGreeting() {
		final Greeting greeting = new Greeting( new User("foo@ciandt.com", "ciandt.com"),
				"Testing", new Date());
		Long id = dao.insert(greeting);
		Assert.assertNotNull(id);
	}

}
