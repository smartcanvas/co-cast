package io.cocast.admin;

import com.google.inject.Singleton;
import io.cocast.util.APIResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * APIs for theme
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/admin/v1/themes")
@Singleton
public class ThemeResource {

    private static Logger logger = LogManager.getLogger(ThemeResource.class.getName());

    @Inject
    private ThemeRepository colorPaletteRepository;

    @POST
    public Response create(Theme theme) {

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
            logger.error("Error creating theme", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created("Theme create. Mnemonic = " + theme.getMnemonic()).getResponse();
    }

    @GET
    public Response list() {
        List<Theme> result;

        try {
            //calls the service
            result = colorPaletteRepository.list();
        } catch (Exception exc) {
            logger.error("Error listing colors", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(result).build();
    }

    @GET
    @Path("/{mnemonic}")
    public Response get(@PathParam("mnemonic") String mnemonic) {
        Theme result;

        try {
            //calls the service
            result = colorPaletteRepository.get(mnemonic);
        } catch (Exception exc) {
            logger.error("Error getting theme with menomonic = " + mnemonic, exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok(result).build();
    }

    @POST
    @Path("/clean")
    public Response cleanUpCache() {

        try {
            //calls the clean service
            colorPaletteRepository.cleanUpCache();
        } catch (Exception exc) {
            logger.error("Error cleaning up configuration cache", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return Response.ok().build();
    }

}
