package fr.btn.repositories;

import fr.btn.entities.ClientEntity;
import fr.btn.entities.MailEntity;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestTransaction
public class MailRepositoryTest {
    @Inject
    MailRepository mailRepository;

    @Inject
    ClientRepository clientRepository;

    private static MailEntity testMail;
    private static ClientEntity testClient;

    @BeforeEach
    void init() {
        testClient = ClientEntity
                .builder()
                .name("TEST CLIENT")
                .email("test@mail.com")
                .apiKey("TEST_KEY")
                .status("ACTIVE")
                .createdDate(LocalDate.now())
                .quota(0)
                .build();

        testMail = MailEntity
                .builder()
                .subject("TEST SUBJECT")
                .recipient("test@recipient.com")
                .date(LocalDateTime.now())
                .sender(testClient)
                .build();
    }

    @Test
    @Order(1)
    void persistMail() {
        clientRepository.persist(testClient);
        mailRepository.persist(testMail);

        assertTrue(mailRepository.isPersistent(testMail));
    }

    @Test
    @Order(2)
    void getMailCountByMonth() {
        clientRepository.persist(testClient);
        mailRepository.persist(testMail);

        long result = mailRepository.getMailCountByMonth(testClient.getApiKey(), LocalDate.now().getMonth().getValue());

        assertEquals(1, result);
    }
}
