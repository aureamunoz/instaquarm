package org.instaquarm.uploading;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsNot.not;

@Disabled
@QuarkusTest
class PictureRepositoryTest {

    @Test
    void testPicturesEndpoint() throws IOException {
        //Create a new Picture
        byte[] image = getClass().getResourceAsStream("/sunset.jpeg").readAllBytes();
        var pictureRequest = new UploadController.PictureRequest("selfie","test", image);

        given().auth().preemptive().basic("admin", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .body(pictureRequest)
                .when().post("/upload")
                .then()
                .statusCode(201).body(containsString("selfie"))
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
                        containsString("\"title\":\"selfie\""),
                        containsString("\"created\":")
                );
    }
}
