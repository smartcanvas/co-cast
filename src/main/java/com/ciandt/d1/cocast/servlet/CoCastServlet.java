package com.ciandt.d1.cocast.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.ciandt.d1.cocast.castview.CastView;
import com.ciandt.d1.cocast.castview.CastViewDAO;
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
    private Logger logger;
    
    /**
     * Shows a specific cast view on co-cast
     * @throws ServletException 
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        
        //gets the current view
        String strCastView = (String) req.getAttribute("castView");
        if (StringUtils.isEmpty(strCastView)) {
            strCastView = req.getParameter("castView");
        }
        
        CastView currentCastView = castViewDAO.findByMnemonic(strCastView);
        if (currentCastView != null) {
            req.setAttribute("castViewMnemonic", currentCastView.getMnemonic());
            req.setAttribute("title", currentCastView.getTitle());
            req.setAttribute("nextCastView", currentCastView.getNextCastViewMnemonic());
            req.setAttribute("headerBackgroundColor", currentCastView.getHeaderBackgroundColor());
            req.setAttribute("headerColor", currentCastView.getHeaderColor());
            req.setAttribute("progressContainerColor", currentCastView.getProgressContainerColor());
            req.setAttribute("activeProgressColor", currentCastView.getActiveProgressColor());
            
            RequestDispatcher dispatcher = req.getRequestDispatcher("cocast.jsp");
            dispatcher.forward(req, resp);
        } else {
            String message = "Could not find cast view for mnemonic = " + strCastView; 
            logger.log( Level.SEVERE, message );
            throw new RuntimeException( message );
        }
        
    }

}
