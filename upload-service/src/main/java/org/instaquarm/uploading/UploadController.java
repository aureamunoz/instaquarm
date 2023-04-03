package org.instaquarm.uploading;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.instaquarm.uploading.client.SquarerRestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.logging.Logger;

@RestController
@RolesAllowed("user")
@RequestMapping("/upload")
public class UploadController {

    private static final Logger LOGGER = Logger.getLogger(UploadController.class);

    @RestClient
    SquarerRestClient squarerRestClient;

    public record PictureRequest(String title, String user, byte[] image) {};

    private AtomicLong counter = new AtomicLong(0);

    @Retry(maxRetries = 4)
    @GetMapping
    public List<Picture> findAll(){

        final Long invocationNumber = counter.getAndIncrement();

        maybeFail(String.format("UploadController#findAll() invocation #%d failed", invocationNumber));

        LOGGER.infof("UploadController#findAll() invocation #%d returning successfully", invocationNumber);
        return Picture.findAll().list();
    }

    private void maybeFail(String failureLogMessage) {
        if (new Random().nextBoolean()) {
            LOGGER.error(failureLogMessage);
            throw new RuntimeException("Resource failure.");
        }
    }

    @PostMapping
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackAdd")
    @Transactional
    public Response add(PictureRequest request) throws InterruptedException {
        Picture picture = new Picture(request.title,request.user,request.image);
        Picture.persist(picture);
        callSquarerFunction(picture);
        return Response.ok(picture).status(201).build();
    }

    private void callSquarerFunction(Picture picture) throws InterruptedException {
        Thread.sleep(new Random().nextInt(500));
        squarerRestClient.makeItSquare(picture);
    }

    public Response fallbackAdd(PictureRequest request) {
        LOGGER.info("Falling back to UploadController#fallbackAdd()");
        Picture picture = new Picture(request.title,request.user,request.image);
        Picture.persist(picture);
        return Response.ok(picture).status(201).build();

    }
}
