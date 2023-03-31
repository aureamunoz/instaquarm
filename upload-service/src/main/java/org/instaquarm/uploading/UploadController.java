package org.instaquarm.uploading;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RolesAllowed("user")
@RequestMapping("/upload")
public class UploadController {

    public record PictureRequest(String title, String user, byte[] image) {};

    @GetMapping
    public List<Picture> findAll(){
        return Picture.findAll().list();
    }

    @PostMapping
    @Transactional
    public Response add(PictureRequest request) {
        System.out.println(request.title +" picture has been uploaded by: "+request.user);
        Picture picture = new Picture();
        picture.title = request.title;
        picture.owner = request.user;
        picture.image = request.image;

        Picture.persist(picture);
        return Response.ok(picture).status(201).build();
    }
}
