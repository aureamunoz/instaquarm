package org.instaquarm.uploading;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/upload")
@RolesAllowed("users")
public class UploadResource {

    @GET
    public String upload() {
        // TODO
        return "ok";
    }
}
