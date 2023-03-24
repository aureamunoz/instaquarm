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
        //List all, should have all 3 fruits the database has initially:
        given()
                .accept("application/json")
                .when().get("/picture")
                .then()
                .statusCode(200)
                .body(
                        containsString("screenshot-1"),
                        containsString("screenshot-2"),
                        containsString("screenshot-3")
                );

        //Delete the Cherry:
        given()
                .when().delete("/picture/1")
                .then()
                .statusCode(204);

        //List all, cherry should be missing now:
        given()
                .accept("application/json")
                .when().get("/picture")
                .then()
                .statusCode(200)
                .body(
                        not(containsString("screenshot-1")),
                        containsString("screenshot-2"),
                        containsString("screenshot-3")
                );

        //Create a new Fruit
        given()
                .contentType("application/json")
                .accept("application/json")
                .body("{\"title\": \"screenshot-4\"}")
                .when().post("/picture")
                .then()
                .statusCode(201)
                .body(containsString("screenshot-4"))
                .body("id", notNullValue())
                .extract().body().jsonPath().getString("id");

        //List all, Orange should be present now:
        given()
                .accept("application/json")
                .when().get("/picture")
                .then()
                .statusCode(200)
                .body(
                        not(containsString("screenshot-1")),
                        containsString("screenshot-2"),
                        containsString("screenshot-3"),
                        containsString("screenshot-4")
                );
    }
}
