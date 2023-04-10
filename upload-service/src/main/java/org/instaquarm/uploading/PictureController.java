package org.instaquarm.uploading;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.instaquarm.uploading.client.SquarerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Inject
    SquarerClient squarerClient;

    public record PictureRequest(String title, String user, byte[] image) {};

    @GetMapping
    public List<Picture> findAll(){
        return Picture.findAll().list();
    }

    @PostMapping("/new")
    @Transactional
    public ResponseEntity<Picture> add(PictureRequest request) throws InterruptedException {
        Picture picture = new Picture(request.title,request.user,request.image);
        Picture.persist(picture);
        try {
            squarerClient.makeItSquare(picture);
            return new ResponseEntity<>(picture, HttpStatus.CREATED);
        } catch (TimeoutException e) {
            return new ResponseEntity<>(picture, HttpStatus.CREATED);
        }catch(CircuitBreakerOpenException ex){
            return new ResponseEntity<>(picture, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e){ //when retry>3
            return new ResponseEntity<>(picture, HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

}
