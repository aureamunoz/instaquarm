package org.instaquarm.wall;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;

import java.util.Random;

@ApplicationScoped
public class PictureRepository {

    private final Multi<Picture> stream;

    public PictureRepository(@Channel("images") Multi<Picture> stream) {
        this.stream = Multi.createBy()
                .replaying().upTo(5).ofMulti(stream);
    }

    public Multi<Picture> stream() {
        return stream;
    }
}
