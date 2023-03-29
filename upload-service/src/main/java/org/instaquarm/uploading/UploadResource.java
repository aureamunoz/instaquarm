package org.instaquarm.uploading;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Path("/upload")
@RolesAllowed("user")
public class UploadResource {

    public record UploadRequest(String user, String title, byte[] picture) {};

    @POST
    public String upload(UploadRequest req) throws IOException {
        System.out.println(req.user + " " + req.title);
        Files.write(new File("target/dump.jpg").toPath(), req.picture);
        return "ok";
    }
}
