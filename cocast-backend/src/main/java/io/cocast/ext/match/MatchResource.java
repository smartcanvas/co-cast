package io.cocast.ext.match;

import com.google.inject.Singleton;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by dviveiros on 09/01/16.
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/ext/match/v1/matches")
@Singleton
public class MatchResource {
}
