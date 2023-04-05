package org.instaquarm.uploading;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.instaquarm.uploading.client.SquarerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.logging.Logger;

@RestController
@RolesAllowed("user")
@RequestMapping("/pictures")
public class PictureController {

    private static final Logger LOGGER = Logger.getLogger(PictureController.class);

    @Inject
    SquarerClient squarerClient;

    public record PictureRequest(String title, String user, byte[] image) {};

    @GetMapping
    public List<Picture> findAll(){
        return Picture.findAll().list();
    }

    @PostMapping("/new")
    @Transactional
    public Response add(PictureRequest request) throws InterruptedException {
        Picture picture = new Picture(request.title,request.user,request.image);
        Picture.persist(picture);
        try {
            squarerClient.makeItSquare(picture);
            return Response.ok(picture).status(201).build();
        } catch (TimeoutException e) {
            return Response.ok(picture).status(201).build();
        }catch(CircuitBreakerOpenException ex){
            return Response.serverError().entity(ex).build();
        } catch (RuntimeException e){ //when retry>4
            return Response.serverError().status(503).build();
        }

    }

}
