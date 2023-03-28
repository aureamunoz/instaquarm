package org.instaquarm.uploading;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsNot.not;

@QuarkusTest
class PictureRepositoryTest {

    @Test
    void testListAllFruits() {
        //List all
        given()
                .accept("application/json")
                .when().get("/pictures")
                .then()
                .statusCode(200)
                .body(
                        containsString("screenshot-1"),
                        containsString("screenshot-2"),
                        containsString("screenshot-3")
                );


        //Create a new Picture
        given()
                .contentType("application/json")
                .accept("application/json")
                .body("{\"title\": \"screenshot-4\"}")
                .when().post("/pictures")
                .then()
                .statusCode(201)
                .body(containsString("screenshot-4"))
                .body("id", notNullValue())
                .extract().body().jsonPath().getString("id");

        //List all
        given()
                .accept("application/json")
                .when().get("/pictures")
                .then()
                .statusCode(200)
                .body(
                        containsString("screenshot-1"),
                        containsString("screenshot-2"),
                        containsString("screenshot-3"),
                        containsString("screenshot-4")
                );
    }
}
