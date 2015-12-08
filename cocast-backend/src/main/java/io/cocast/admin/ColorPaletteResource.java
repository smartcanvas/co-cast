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
 * APIs for color palette
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/api/admin/v1/colors")
@Singleton
public class ColorPaletteResource {

    private static Logger logger = LogManager.getLogger(ColorPaletteResource.class.getName());

    @Inject
    private ColorPaletteRepository colorPaletteRepository;

    @POST
    public Response create(ColorPalette colorPalette) {

        //validate the parameter
        if ((colorPalette == null) ||
                (colorPalette.getMnemonic() == null) ||
                (colorPalette.getPrimaryColor() == null) ||
                (colorPalette.getSecondaryColor() == null) ||
                (colorPalette.getAccentColor() == null) ||
                (colorPalette.getPrimaryTextColor() == null) ||
                (colorPalette.getSecondaryTextColor() == null) ||
                (colorPalette.getAccentTextColor() == null)) {
            return APIResponse.badRequest("All fields are required for color palettes").getResponse();
        }

        try {
            //calls the creation service
            colorPaletteRepository.create(colorPalette);
        } catch (Exception exc) {
            logger.error("Error creating color palette", exc);
            return APIResponse.serverError(exc.getMessage()).getResponse();
        }

        return APIResponse.created("Color palette create. Mnemonic = " + colorPalette.getMnemonic()).getResponse();
    }

    @GET
    public Response list() {
        List<ColorPalette> result;

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
        ColorPalette result;

        try {
            //calls the service
            result = colorPaletteRepository.get(mnemonic);
        } catch (Exception exc) {
            logger.error("Error getting color palette with menomonic = " + mnemonic, exc);
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
