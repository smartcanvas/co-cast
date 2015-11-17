package com.ciandt.d1.cocast.servlet;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ciandt.d1.cocast.util.Constants;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.apache.commons.lang.StringUtils;

import com.ciandt.d1.cocast.castview.CastView;
import com.ciandt.d1.cocast.castview.CastViewDAO;
import com.ciandt.d1.cocast.castview.CastViewObject;
import com.ciandt.d1.cocast.castview.CastViewServices;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Servlet to orchestrate the transition between different Cast Views
 * 
 * @author <a href="mailto:viveiros@ciandt.com">Daniel Viveiros</a>
 */
@SuppressWarnings("serial")
@Singleton
public class CoCastServlet extends HttpServlet {
    
    @Inject
    private CastViewDAO castViewDAO;
    
    @Inject
    private CastViewServices castViewServices;
    
    @Inject
    private Logger logger;
    
    /**
     * Shows a specific cast view on co-cast
     * @throws ServletException 
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        
        //gets the next cast view... first in the cache, later in the param string
        String strNextCastView = null;
        MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
        strNextCastView = (String) cache.get(Constants.NEXT_CASTVIEW_KEY);

        if ( strNextCastView == null ) {
            logger.info( "nextCastView not found in cache... getting from request attr or param");
            strNextCastView = (String) req.getAttribute("castView");
            if (StringUtils.isEmpty(strNextCastView)) {
                strNextCastView = req.getParameter("castView");
            }
        } else {
            logger.info( "nextCastView FOUND in cache! Using it!");
            cache.delete( Constants.NEXT_CASTVIEW_KEY );
        }
        logger.info("Next cast view = " + strNextCastView);
        
        CastView nextCastView = castViewDAO.findByMnemonic(strNextCastView);
        if (nextCastView == null) {
            String message = "Could not find cast view for mnemonic = " + strNextCastView;
            logger.log( Level.SEVERE, message );
            throw new RuntimeException( message );
        }

        logger.info("Next cast view = " + nextCastView);
        
        //checks if this view has content
        List<CastViewObject> objects = castViewServices.getCastViewObjects(nextCastView.getMnemonic());
        if ( (objects != null) && (objects.size() > 0)) {   
            logger.info("Found " + objects.size() + " cast view objects. Forwarding to cocast.jsp");
            
            req.setAttribute("castViewMnemonic", nextCastView.getMnemonic());
            req.setAttribute("title", nextCastView.getTitle());
            req.setAttribute("nextCastView", nextCastView.getNextCastViewMnemonic());
            req.setAttribute("headerBackgroundColor", nextCastView.getHeaderBackgroundColor());
            req.setAttribute("headerColor", nextCastView.getHeaderColor());
            req.setAttribute("progressContainerColor", nextCastView.getProgressContainerColor());
            req.setAttribute("activeProgressColor", nextCastView.getActiveProgressColor());
            
            RequestDispatcher dispatcher = req.getRequestDispatcher("cocast.jsp?time=" + System.currentTimeMillis());
            dispatcher.forward(req, resp);
            
        } else {
            logger.info("No cast view objects found. Forwarding to cocast?nextView=" + nextCastView.getNextCastViewMnemonic());
            req.setAttribute("castView", nextCastView.getNextCastViewMnemonic());
            RequestDispatcher dispatcher = req.getRequestDispatcher("/cocast");
            dispatcher.forward(req, resp);
        }
        
    }

}
