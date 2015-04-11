package com.ciandt.dcoder.c2.resources;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ciandt.dcoder.c2.service.GooglePlusConnector;
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
	
	@Inject
	private GooglePlusConnector connector;
    
    /**
	 * Executes the servlet
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		
		long initTime = System.currentTimeMillis();
		logger.info( "Executing ContentIngestionServlet" );
		
		try {
			connector.execute();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error ingestion content from Google Plus", e);
		}
		
		long endTime = System.currentTimeMillis();
		logger.info( "Process finalized in " + (endTime - initTime) + " msecs");
	}

}
