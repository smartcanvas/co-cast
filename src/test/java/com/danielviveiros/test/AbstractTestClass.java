package com.danielviveiros.test;

import org.junit.After;
import org.junit.Before;

import com.danielviveiros.config.CommonModule;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Abstract Test
 * 
 * @author fabioap
 *
 */
public abstract class AbstractTestClass {
	
	private Injector injector; 
	
	final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig(),
			new LocalMemcacheServiceTestConfig());
	
	public AbstractTestClass() {
		this.injector = Guice.createInjector(new CommonModule());
	}
	
	@Before
	public void helperSetup() {
		helper.setUp();
	}

	@After
	public void helperTearDown() {
		helper.tearDown();
	}
	
	@Before
	public abstract void setup();
	
	protected <T> T getInstance(Class<T> type) {
        return this.injector.getInstance(type);
    }
		

}
