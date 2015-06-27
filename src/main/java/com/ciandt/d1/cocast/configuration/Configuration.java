package com.ciandt.d1.cocast.configuration;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Class that handles configuration. Co-cast will try to first find a configuration inside datastore.
 * If it's not there, it will try to find a property file named co-cast.properties
 * 
 * @author <a href="mailto:viveiros@ciandt.com">Daniel Viveiros</a>
 */
@SuppressWarnings("serial")
@Entity
public class Configuration implements Serializable {
    
    /* Configuration Name */
    @Id
    private String key;
    
    /* Value */
    private String value;
    
    /* Description (optional) */
    private String description;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Configuration [name=" + key + ", value=" + value + ", description=" + description + "]";
    }
    
    
    
    

}