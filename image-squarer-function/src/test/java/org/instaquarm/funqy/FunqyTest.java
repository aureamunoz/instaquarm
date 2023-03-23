package org.instaquarm.funqy;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
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
        SquarerRequest request = new SquarerRequest("clement", img, List.of("a", "b"));
        var s = given()
                .contentType("application/json")
                .accept("application/json")
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(SquarerResponse.class);
        Assertions.assertEquals("clement", s.user);
        Assertions.assertEquals(List.of("a", "b"), s.tags);
        Assertions.assertTrue(img.length > s.picture.length);
    }



}

