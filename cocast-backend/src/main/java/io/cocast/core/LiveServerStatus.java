package io.cocast.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * Info that the server sends to the client regarding the status of the casting
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveServerStatus implements Serializable {

    public Date lastUpdated;
}
