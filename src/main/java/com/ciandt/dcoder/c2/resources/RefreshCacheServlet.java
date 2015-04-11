package com.ciandt.dcoder.c2.resources;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ciandt.dcoder.c2.service.CastViewServices;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Refreshes the cache that will serve cards later
 * 
 * @author Daniel Viveiros
 */
@SuppressWarnings("serial")
@Singleton
public class RefreshCacheServlet extends HttpServlet {
	
	@Inject
	private Logger logger;
	
	@Inject
	private CastViewServices castServices;

	/**
	 * Executes the servlet
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		try {
			castServices.refreshCardCache();
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error refreshing cache", exc);
		}
	}
}
