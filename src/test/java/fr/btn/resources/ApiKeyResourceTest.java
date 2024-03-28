package fr.btn.resources;

import fr.btn.entities.ClientEntity;
import fr.btn.entities.MailEntity;
import fr.btn.models.MailClient;
import fr.btn.repositories.ClientRepository;
import fr.btn.repositories.MailRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class ApiKeyResourceTest {
    private static final String API_KEY = "TEST_KEY";
    private static ClientEntity testClient;
    private static MailClient testMail;

    @Inject
    ClientRepository clientRepository;

    @Inject
    MailRepository mailRepository;

    @BeforeAll
    static void init() {
        testClient = ClientEntity
                .builder()
                .name("TEST CLIENT")
                .email("test@mail.com")
                .apiKey(API_KEY)
                .status("ACTIVE")
                .createdDate(LocalDate.now())
                .quota(0)
                .build();

        testMail = MailClient
                    .builder()
                    .subject("TEST SUBJECT")
                    .recipient("foo@quarkus.io")
                    .build();

    }

    @BeforeEach
    void setUp() {
        ClientEntity found = clientRepository.findClientByApiKey(API_KEY);
        if(found != null) {
            testClient.setId(found.getId());
            return;
        }

        clientRepository.persist(testClient);
    }

    @Test
    @Order(1)
    void getClientByApiKey() {
        given()
                .when()
                .get("/apiKey/" + API_KEY)
                .then()
                .statusCode(200)
                .body("quota", is(0))
                .body("status", is("ACTIVE"));
    }

    @Test
    @Order(2)
    void getClientByInvalidApiKey() {
        given()
                .when()
                .get("/apiKey/FALSE_KEY")
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Order(7)
    void getMailCountByMonth() {
        given()
                            .when()
                            .get("/apiKey/" + API_KEY + "/mail_count")
                            .then()
                            .contentType("text/plain")
                            .statusCode(200)
                            .body(is("1"));

        mailRepository.delete("sender.apiKey=?1", API_KEY);
        clientRepository.deleteById(testClient.getId());
    }

    @Test
    @Order(6)
    void getMailCountByMonthWithInvalidKey() {
        given()
                .when()
                .get("/apiKey/FALSE_KEY/mail_count")
                .then()
                .contentType(ContentType.TEXT)
                .statusCode(200)
                .body(is("0"));

    }

    @Test
    @Order(3)
    void saveMail() {
        given()
                .contentType("application/Json")
                .body(testMail)
                .when()
                .post("/apiKey/" + API_KEY)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(4)
    void saveMailWithMissingApiKey() {
        given()
                .contentType("application/Json")
                .body(testMail)
                .when()
                .post("/apiKey/" + null)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(5)
    void saveMailWithMissingMailClient() {
        given()
                .contentType("application/Json")
                .when()
                .post("/apiKey/" + API_KEY)
                .then()
                .statusCode(400);
    }
}