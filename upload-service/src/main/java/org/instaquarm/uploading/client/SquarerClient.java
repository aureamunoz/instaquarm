package org.instaquarm.uploading.client;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.instaquarm.uploading.Picture;
import org.instaquarm.uploading.PictureController;
import org.jboss.logging.Logger;

import java.util.Random;

@ApplicationScoped
public class SquarerClient {

    private static final Logger LOGGER = Logger.getLogger(SquarerClient.class);

    @RestClient
    SquarerRestClient squarerRestClient;

    @Retry(maxRetries = 4)
    @Timeout(5000)
    public Picture makeItSquare(Picture picture) {
        maybeFail("UploadController#findAll() failed");
        LOGGER.infof("UploadController#findAll() returning successfully");
        return squarerRestClient.makeItSquare(picture);
    }

    private void maybeFail(String failureLogMessage) {
        if (new Random().nextBoolean()) {
            LOGGER.error(failureLogMessage);
            throw new RuntimeException("Resource failure.");
        }
    }
}
