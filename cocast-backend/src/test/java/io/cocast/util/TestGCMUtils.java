package io.cocast.util;

import io.cocast.ext.people.Person;
import io.cocast.test.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * GCMUtils
 */
public class TestGCMUtils extends BaseTest {

    private GCMUtils gcmUtils;

    @Before
    public void setUp() {
        gcmUtils = super.getInstance(GCMUtils.class);
    }

    @Test
    public void shouldReadConfiguration() throws Exception {
        String strTo = "dIBN3VDc3xo:APA91bGur8HBtfRW--5KSraYHwyX2-2JT4WSo6cZZaO57Xlf8YolCdIHUGy6yyMBDChPquIUNSSAicP9cNx7Q88JZyuKgPKUXCPY99gmktNsohGWiankOyzS5cRHDfgOtYUbXhpOv4hY";
        //String strTo = "cBhXSuH3Bls:APA91bHVPbszK7ZdAyWcKQmcbKsdFp_wvLLn8rkVAnX5O3hj027kJfOpiVScxQUSSMCz1OwIh7kxlyQpHoAnUAoX4uxxUTfVeFmKjZEVnxKESU_3p-rN4zpH2aIiC0A6eLzDaPOkDJE8";
        Person person = new Person();
        person.setEmail("viveiros@ciandt.com");
        person.setDisplayName("Daniel Viveiros");
        Map<String, Object> data = new HashMap<>();
        data.put("type", "match");
        data.put("person", person);
        GCMUtils.GCMMessage message = new GCMUtils.GCMMessage(strTo, data);
        gcmUtils.send("teamwork", message);
    }
}
