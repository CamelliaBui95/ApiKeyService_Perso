package fr.btn.resources;

import fr.btn.WiremockMailService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(WiremockMailService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientResourceTest {

    @Test
    @Order(2)
    void getAll() {
        given()
                .when()
                .get("/clients")
                .then()
                .body("size()", equalTo(1))
                .body("id", hasItems(1))
                .body("name", hasItems("TEST CLIENT"))
                .body("email", hasItems("test@mail.com"))
                .body("quota", hasItems(10))
                .statusCode(200);
    }

    @Test
    @Order(3)
    void getClientById() {
        given()
                .when()
                .get("/clients/1")
                .then()
                .body("id", equalTo(1))
                .body("name", equalTo("TEST CLIENT"))
                .body("email", equalTo("test@mail.com"))
                .body("quota", equalTo(10))
                .statusCode(200);
    }

    @Test
    @Order(1)
    void createNewClient() {

        String message = given()
                            .formParam("name", "TEST CLIENT")
                            .formParam("email", "test@mail.com")
                            .formParam("quota", 10)
                            .when()
                            .post("/clients")
                            .then()
                            .contentType("text/plain")
                            .statusCode(HttpStatus.SC_CREATED)
                            .extract().asString();

        assertEquals(message, "Api Key has been sent by mail.");
    }

    @Test
    @Order(4)
    void renewApiKey() {
        given()
                .queryParam("email", "test@mail.com")
                .when()
                .put("/clients/new_key")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(5)
    void renewQuota() {
        given()
                .formParam("email", "test@mail.com")
                .formParam("quota", 0)
                .when()
                .put("/clients/new_quota")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("quota", equalTo(0))
                .body("email", equalTo("test@mail.com"));
    }

    @Test
    @Order(6)
    void createNewClientWithInvalidEmail() {
        given()
                .formParam("name", "TEST CLIENT")
                .formParam("email", "test@mail")
                .formParam("quota", 10)
                .when()
                .post("/clients")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(7)
    void createNewClientWithExistingEmail() {
        given()
                .formParam("name", "TEST CLIENT 2")
                .formParam("email", "test@mail.com")
                .formParam("quota", 10)
                .when()
                .post("/clients")
                .then()
                .statusCode(HttpStatus.SC_NOT_ACCEPTABLE);
    }

    @Test
    @Order(8)
    void getClientByInvalidId() {
        given()
                .when()
                .get("/clients/2")
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }
    @Test
    @Order(9)
    void renewApiKeyWithInvalidEmail() {
        given()
                .when()
                .put("/clients/new_key")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Order(10)
    void renewApiKeyWithEmailThatDoesNotExist() {
        given()
                .queryParam("email", "test2@mail.com")
                .when()
                .put("/clients/new_key")
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Order(11)
    void renewQuotaWithInvalidEmailOrQuota() {
        given()
                .formParam("email", "")
                .formParam("quota", 15)
                .when()
                .put("/clients/new_quota")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        given()
                .formParam("email", "test@mail.com")
                .when()
                .put("/clients/new_quota")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}