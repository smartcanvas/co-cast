package com.ciandt.d1.cocast.configuration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ciandt.d1.cocast.test.AbstractTestClass;

public class ConfigurationServicesTest extends AbstractTestClass {
    
    private ConfigurationServices configurationServices;
    private ConfigurationDAO configurationDAO;
    
    public ConfigurationServicesTest() {
        super();
    }

    @Override
    public void setup() {
        super.setup();
        configurationServices = super.getInstance(ConfigurationServices.class);
        configurationDAO = super.getInstance(ConfigurationDAO.class);
    }
    
    @Test
    public void shouldGetStringProperty() {
        String baseUri = configurationServices.get("base_uri");
        assertNotNull( baseUri );
    }
    
    @Test
    public void shouldSaveProperties() {
        Configuration configuration = new Configuration();
        configuration.setKey("teste.key");
        configuration.setValue("teste.value");
        configuration.setDescription("teste.description");
        configurationDAO.save(configuration);
        
        configurationServices.reload();
        String value = configurationServices.get("teste.key");
        assertEquals("teste.value", value);
    }

}
