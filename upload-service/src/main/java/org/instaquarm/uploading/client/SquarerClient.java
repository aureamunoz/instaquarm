package org.instaquarm.uploading.client;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.instaquarm.uploading.Picture;
import org.instaquarm.uploading.PictureController;
import org.jboss.logging.Logger;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Random;

@ApplicationScoped
public class SquarerClient {

    private static final Logger LOGGER = Logger.getLogger(SquarerClient.class);

    @ConfigProperty(name = "chaos")
    Optional<Boolean> chaosMode;

    @RestClient
    SquarerRestClient squarerRestClient;

    @Retry(maxRetries = 4)
    @Timeout(10000)
    @CircuitBreaker(requestVolumeThreshold = 4)
    public Picture makeItSquare(Picture picture) throws InterruptedException {
        boolean chaos = chaosMode.orElse(Boolean.FALSE);

        if(chaos) {
            //for Fault tolerance demo purpose
            maybeFail("UploadController#makeItSquare() failed");
            LOGGER.infof("UploadController#makeItSquare() returning successfully");
            Thread.sleep(new Random().nextInt(3000));
        }

        return squarerRestClient.makeItSquare(picture);
    }

    private void maybeFail(String failureLogMessage) {
        if (new Random().nextBoolean()) {
            LOGGER.error(failureLogMessage);
            throw new RuntimeException("Resource failure.");
        }
    }

}
