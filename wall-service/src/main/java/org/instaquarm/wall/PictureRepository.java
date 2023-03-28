package org.instaquarm.wall;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Random;

@ApplicationScoped
public class PictureRepository {


    public Multi<Picture> stream() {
        return Multi.createFrom().<Picture>emitter(emitter -> {
            Random random = new Random();
            while (true) {
                nap();
                emitter.emit(Pictures.PICTURES.get(random.nextInt(Pictures.PICTURES.size() - 1)));
            }
        }).runSubscriptionOn(Infrastructure.getDefaultExecutor());
    }

    private static void nap() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
