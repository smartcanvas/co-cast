package com.ciandt.dcoder.c2.resources;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.ciandt.dcoder.c2.util.ConfigurationUtils;
import com.ciandt.dcoder.c2.util.Constants;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Servlet to ingest content coming from google plus inside Smart Canvas
 * 
 * @author Daniel Viveiros
 */
@SuppressWarnings("serial")
@Singleton
public class ContentIngestionServlet extends HttpServlet {
	
	@Inject
	private Logger logger;
	
	private static ConfigurationUtils configurationServices = ConfigurationUtils.getInstance();
    
    /**
	 * Executes the servlet
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		
		long initTime = System.currentTimeMillis();
		logger.info( "Executing ContentIngestionServlet" );
		
		//gets the publishers
        List<String> publisherIds = getPublishersIds();
        if ( publisherIds != null ) {
            logger.info("We found " + publisherIds.size() + " publisher(s)");
            for (String publisherId: publisherIds) {
		
				//put the date in a task queue
				Queue queue = QueueFactory.getQueue(Constants.INGESTION_QUEUE);
				queue.add(withUrl("/tasks/contentingestion/inbound")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN).method(TaskOptions.Method.POST)
				        .payload(publisherId));
            }
        }
		
		long endTime = System.currentTimeMillis();
		logger.info( "Process finalized in " + (endTime - initTime) + " msecs");
	}
	
    /**
     * Get the user id for all the publishers
     */
    private List<String> getPublishersIds() {
        List<String> result = configurationServices.getValues("publisher_ids");
        return result;
    }

}
