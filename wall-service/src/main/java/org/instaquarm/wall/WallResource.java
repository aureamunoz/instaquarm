package org.instaquarm.wall;


import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.util.List;

@Path("/wall")
public class WallResource {

    private final PictureRepository repository;

    WallResource(PictureRepository repository) {
        this.repository = repository;
    }

    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    @Path("/stream")
    public Multi<Picture> stream() {
        return repository.stream();
    }

}
