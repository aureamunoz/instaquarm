package org.instaquarm.uploading.client;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.instaquarm.uploading.Picture;
import org.jboss.logging.Logger;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@ApplicationScoped
public class SquarerClient {

    private static final Logger LOGGER = Logger.getLogger(SquarerClient.class);

    @ConfigProperty(name = "chaos")
    Optional<Boolean> chaosMode;

    @RestClient
    SquarerRestClient squarerRestClient;

    // Fault tolerance
    @Retry
    @Timeout(value = 30, unit = ChronoUnit.SECONDS)
    @CircuitBreaker
    // Metrics
    @Timed
    @Counted
    public Picture makeItSquare(Picture picture) throws InterruptedException {
        boolean chaos = chaosMode.orElse(Boolean.FALSE);
        if(chaos) {
            LOGGER.infof("We are in chaos mode");
            //for Fault tolerance demo purpose
            maybeFail();
            LOGGER.infof("UploadController#makeItSquare() returning successfully");
            Thread.sleep(new Random().nextInt(3000));
        }
        return squarerRestClient.makeItSquare(picture);
    }

    private void maybeFail() {
        if (new Random().nextBoolean()) {
            LOGGER.error("SquarerClient#makeItSquare() failed");
            throw new RuntimeException("Resource failure.");
        }
    }

}
