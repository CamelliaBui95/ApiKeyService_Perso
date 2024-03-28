package fr.btn.resources;

import fr.btn.WiremockMailService;
import fr.btn.entities.ClientEntity;
import fr.btn.repositories.ClientRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(WiremockMailService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class ClientResourceTest {

    @InjectMock
    ClientRepository clientRepository;
    private static ClientEntity mockClient = ClientEntity
            .builder()
            .id(1)
            .createdDate(LocalDate.now())
            .name("TEST CLIENT")
            .email("test@mail.com")
            .quota(10)
            .status("ACTIVE")
            .mails(new ArrayList<>())
            .build();

    @BeforeEach
    void setUp() {
        List<ClientEntity> mockClients = new ArrayList<>();
        mockClients.add(mockClient);

        Mockito.when(clientRepository.listAll()).thenReturn(mockClients);
        Mockito.when(clientRepository.findById(1)).thenReturn(mockClient);
        Mockito.when(clientRepository.findClientByEmail("test@mail.com")).thenReturn(mockClient);
    }
    @Test
    @Order(3)
    void getAll() {
        given()
                .when()
                .get("/clients")
                .then()
                .log()
                .all()
                .body("size()", equalTo(1))
                .body("id", hasItems(1))
                .body("name", hasItems("TEST CLIENT"))
                .body("email", hasItems("test@mail.com"))
                .body("quota", hasItems(10))
                .statusCode(200);
    }

    @Test
    @Order(4)
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
    @Order(2)
    void createNewClientWithExistingEmail() {
        Mockito.when(clientRepository.countClientsByEmail("test@mail.com")).thenReturn(1L);

        given()
                .formParam("name", "TEST CLIENT")
                .formParam("email", "test@mail.com")
                .formParam("quota", 10)
                .when()
                .post("/clients")
                .then()
                .statusCode(HttpStatus.SC_NOT_ACCEPTABLE);
    }

    @Test
    @Order(5)
    void renewApiKey() {
        given()
                .queryParam("email", "test@mail.com")
                .when()
                .put("/clients/new_key")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(6)
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
    @Order(7)
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
    @Order(8)
    void getClientByInvalidId() {
        given()
                .when()
                .get("/clients/99")
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