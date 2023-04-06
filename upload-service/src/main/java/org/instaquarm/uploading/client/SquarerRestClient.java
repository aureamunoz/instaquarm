package org.instaquarm.uploading.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.instaquarm.uploading.Picture;


@RegisterRestClient(configKey = "squarer-function")
public interface SquarerRestClient {

    @POST
    @Path("/squarer")
    Picture makeItSquare(Picture squarePicture);
}
