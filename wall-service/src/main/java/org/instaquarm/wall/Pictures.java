package org.instaquarm.wall;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;

public class Pictures {

    public static final List<Picture> PICTURES;


    static {
        // Preload
        var cl = Pictures.class.getClassLoader();
        try {
            // TODO Close streams...
            byte[] bytes1 = cl.getResourceAsStream("desert.jpeg").readAllBytes();
            byte[] bytes2 = cl.getResourceAsStream("flowers.jpeg").readAllBytes();
            byte[] bytes3 = cl.getResourceAsStream("mountain.jpeg").readAllBytes();
            byte[] bytes4 = cl.getResourceAsStream("sea.jpeg").readAllBytes();
            byte[] bytes5 = cl.getResourceAsStream("sunset.jpeg").readAllBytes();

            PICTURES = List.of(
                    new Picture("me", bytes1, List.of("desert")),
                    new Picture("me", bytes2, List.of("flowers")),
                    new Picture("me", bytes3, List.of("mountain")),
                    new Picture("me", bytes4, List.of("sea")),
                    new Picture("me", bytes5, List.of("sunset"))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
