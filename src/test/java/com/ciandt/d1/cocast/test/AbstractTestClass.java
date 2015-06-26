package com.ciandt.d1.cocast.test;

import org.junit.After;
import org.junit.Before;

import com.ciandt.d1.cocast.guice.CoCastModule;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Abstract Test for Co-Cast
 * 
 * @author Daniel Viveiros
 *
 */
public abstract class AbstractTestClass {
	
	protected Injector injector; 
	
	final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig(),
			new LocalMemcacheServiceTestConfig());
	
	public AbstractTestClass() {
		this.injector = Guice.createInjector(new CoCastModule());
	}
	
	@Before
    public void setup() {
	    this.helper.setUp();
	}

    @After
    public void shutdown() throws Exception {
        this.helper.tearDown();
    }
	
	protected <T> T getInstance(Class<T> type) {
        return this.injector.getInstance(type);
    }
		

}
