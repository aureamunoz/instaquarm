package org.instaquarm.uploading;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pictures")
public class PictureController {

    @GetMapping
    @Consumes("application/json")
    public List<Picture> findAll(){
        return Picture.findAll().list();
    }

    @PostMapping
    @Transactional
    public Response add(Picture picture) {
        if (picture.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        Picture.persist(picture);
        return Response.ok(picture).status(201).build();
    }
}
