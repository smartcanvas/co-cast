package io.cocast.admin;

import io.cocast.util.APIResponse;
import io.cocast.util.AbstractResource;
import io.cocast.util.log.LogUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * APIs for theme
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/admin/v1/themes")
public class ThemeResource extends AbstractResource {

    private static Logger logger = LogManager.getLogger(ThemeResource.class.getName());

    @Inject
    private ThemeRepository colorPaletteRepository;

    @POST
    public Response create(@Context HttpServletRequest request, Theme theme) {

        long initTime = System.currentTimeMillis();

        //validate the parameter
        if ((theme == null) ||
                (theme.getMnemonic() == null) ||
                (theme.getPrimaryColor() == null) ||
                (theme.getSecondaryColor() == null) ||
                (theme.getAccentColor() == null) ||
                (theme.getPrimaryFont() == null) ||
                (theme.getSecondaryFont() == null) ||
                (theme.getPrimaryFontColor() == null) ||
                (theme.getSecondaryFontColor() == null) ||
                (theme.getAccentFontColor() == null)) {
            return APIResponse.badRequest("All fields are required for themes").getResponse();
        }

        try {
            //calls the creation service
            colorPaletteRepository.create(theme);
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "post", 0,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "post", 0, HttpServletResponse.SC_CREATED, initTime);
        return APIResponse.created("Theme create. Mnemonic = " + theme.getMnemonic()).getResponse();
    }

    @GET
    public Response list(@Context HttpServletRequest request) {
        long initTime = System.currentTimeMillis();
        List<Theme> result;

        try {
            //calls the service
            result = colorPaletteRepository.list();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "get", 0,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "get", result.size(), HttpServletResponse.SC_OK, initTime);
        return Response.ok(result).build();
    }

    @GET
    @Path("/{mnemonic}")
    public Response get(@Context HttpServletRequest request, @PathParam("mnemonic") String mnemonic) {

        long initTime = System.currentTimeMillis();

        Theme result;

        try {
            //calls the service
            result = colorPaletteRepository.get(mnemonic);
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "get", 0,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "get", 1, HttpServletResponse.SC_OK, initTime);
        return Response.ok(result).build();
    }

    @POST
    @Path("/clean")
    public Response cleanUpCache(@Context HttpServletRequest request) {
        long initTime = System.currentTimeMillis();

        try {
            //calls the clean service
            colorPaletteRepository.cleanUpCache();
        } catch (Exception exc) {
            String message = exc.getMessage();
            LogUtils.fatal(logger, message, exc);
            logResult(message, request, "post", 0,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, initTime);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        logResult("OK", request, "post", 0, HttpServletResponse.SC_OK, initTime);
        return Response.ok().build();
    }

    @Override
    protected String getModuleName() {
        return "admin";
    }

    @Override
    protected String getResourceName() {
        return "themes";
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
