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
public class MyTest {

    @Test
    public void testSquarerFun() throws Exception {
        var img = Files.readAllBytes(new File("/Users/clement/Downloads/IMG_2431.JPG").toPath());
        SquarerRequest request = new SquarerRequest("clement", img, List.of("a", "b"));

        var s = given()
                .contentType("application/json")
                .accept("application/json")
                .body(request)
                .when()
                .post("https://vaekn02h31.execute-api.us-east-1.amazonaws.com/stage")
                .then()
                .statusCode(200)
                .extract().as(SquarerResponse.class);
        Assertions.assertEquals("clement", s.user);
        Assertions.assertEquals(List.of("a", "b"), s.tags);
        Assertions.assertTrue(img.length > s.picture.length);
        Files.write(new File("target/dump.jpg").toPath(), s.picture, StandardOpenOption.CREATE);
    }


    @Test
    public void encode() throws IOException {
        var bytes = Files.readAllBytes(new File("src/test/resources/img-placeholder-rectangle.jpg").toPath());
        String base64String = Base64.encodeBase64String(bytes);
        System.out.println(base64String);
    }

}
