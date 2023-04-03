package org.instaquarm.uploading;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.instaquarm.uploading.PictureController.*;

@Disabled
@QuarkusTest
class PictureEndpointTest {

    @Test
    void testPicturesEndpoint() throws IOException {
        //Create a new Picture
        var image = getClass().getResourceAsStream("/sunset.jpeg").readAllBytes();
        var pictureRequest = new PictureRequest("sunset","test", image);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pictureRequest)
                .when().post("/upload")
                .then()
                .statusCode(201).body(containsString("sunset"))
                .body("id", notNullValue())
                .extract().body().jsonPath().getString("id");

        //List all
        given()
                .accept("application/json")
                .when().get("/upload")
                .then()
                .statusCode(200)
                .body(
                        containsString("\"id\":1"),
                        containsString("\"title\":\"sunset\""),
                        containsString("\"created\":")
                );
    }

}
