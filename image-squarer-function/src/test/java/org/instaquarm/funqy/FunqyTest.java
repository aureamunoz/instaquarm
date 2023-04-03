package org.instaquarm.funqy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class FunqyTest {

    @Test
    public void testSquarer() throws Exception {
        var img = Files.readAllBytes(new File("src/test/resources/img-placeholder-rectangle.jpg").toPath());
        String title = "a rectangle";
        SquarerRequest request = new SquarerRequest("clement", img, title);
        var s = given()
                .contentType("application/json")
                .accept("application/json")
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(SquarerResponse.class);
        Assertions.assertEquals("clement", s.owner);
        Assertions.assertEquals(title, s.title);
        Assertions.assertTrue(img.length > s.image.length);
    }

    @Test
    @Disabled
    public void dumpSquarePictures() throws IOException {
        var list = List.of(new File("/Users/clement/Downloads/sea.jpeg"),
                new File("/Users/clement/Downloads/desert.jpeg"),
                new File("/Users/clement/Downloads/mountain.jpeg"),
                new File("/Users/clement/Downloads/flowers.jpeg"),
                new File("/Users/clement/Downloads/sunset.jpeg")
        );
        for (File file : list) {
            invokeFunction(file, file.getName());
        }
    }

    private void invokeFunction(File file, String name) throws IOException {
        var img = Files.readAllBytes(file.toPath());
        SquarerRequest request = new SquarerRequest("clement", img, name);

        given()
                .contentType("application/json")
                .accept("application/json")
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(200);
    }



}

