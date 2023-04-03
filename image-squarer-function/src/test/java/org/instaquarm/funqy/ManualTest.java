package org.instaquarm.funqy;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Manual test of the function.
 */
@Disabled
public class ManualTest {

    @Test
    public void testSquarerFun() throws Exception {
        var img = Files.readAllBytes(new File("/Users/clement/Downloads/chaine.jpg").toPath());
        SquarerRequest request = new SquarerRequest("clement", img, "chaine.jpg");

        var s = given()
                .contentType("application/json")
                .accept("application/json")
                .body(request)
                .when()
                .post("https://vaekn02h31.execute-api.us-east-1.amazonaws.com/stage/squarer")
                .then()
                .statusCode(200)
                .extract().as(SquarerResponse.class);
        Assertions.assertEquals("clement", s.owner);
        Assertions.assertEquals("chaine.jpg", s.title);
        Assertions.assertTrue(img.length > s.image.length);
        Files.write(new File("target/dump.jpg").toPath(), s.image, StandardOpenOption.CREATE);
    }

    @Test
    public void dumpSquarePictures() throws IOException {
        var list = List.of(new File("/Users/clement/Downloads/sea.jpeg"),
                new File("/Users/clement/Downloads/desert.jpeg"),
                new File("/Users/clement/Downloads/mountain.jpeg"),
                new File("/Users/clement/Downloads/flowers.jpeg"),
                new File("/Users/clement/Downloads/sunset.jpeg"));
        for (File file : list) {
            invokeFunction(file, file.getName());
        }
    }

    private void invokeFunction(File file, String name) throws IOException {
        var img = Files.readAllBytes(file.toPath());
        SquarerRequest request = new SquarerRequest("clement", img, name);

        var s = given()
                .contentType("application/json")
                .accept("application/json")
                .body(request)
                .when()
                .post("https://vaekn02h31.execute-api.us-east-1.amazonaws.com/stage/squarer")
                .then()
                .statusCode(200)
                .extract().as(SquarerResponse.class);

        write(s);
    }

    private void write(SquarerResponse s) throws IOException {
        String name = s.title;
        Files.write(new File(name).toPath(), s.image);
    }


    @Test
    public void encode() throws IOException {
        var bytes = Files.readAllBytes(new File("src/test/resources/img-placeholder-rectangle.jpg").toPath());
        String base64String = Base64.encodeBase64String(bytes);
        System.out.println(base64String);
    }

}
