package com.ciandt.d1.cocast.servlet;

import com.ciandt.d1.cocast.castview.CastViewServices;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exposed web service API for task queue operations.
 *
 * @author Daniel Viveiros
 */
@Singleton
@Path("/api/tasks")
public class TaskQueueResource {

    @Inject
    private Logger logger;

    @Inject
    private CastViewServices castServices;

    @POST
    @Path("/fetch/inbound")
    public Response fetchContent() {

        logger.info("[TaskQueueResource] Fetch content started");

        Long init = System.currentTimeMillis();

        try {
            castServices.fetchContent();
        } catch (Exception exc) {
            String message = "Error fetching content";
            logger.log(Level.SEVERE, message, exc);

        }

        Long end = System.currentTimeMillis();
        logger.info("Fetch content process ended. Time taken = " + (end - init) + " msecs");

        return Response.ok().build();
    }
}
