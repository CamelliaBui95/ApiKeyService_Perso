package fr.btn.repositories;

import fr.btn.entities.ClientEntity;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.client.Client;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class ClientRepositoryTest {
    @Inject
    ClientRepository clientRepository;
    private static ClientEntity testClient;

    @BeforeAll
    static void init() {
        testClient = ClientEntity
                .builder()
                .name("TEST CLIENT")
                .email("test@mail.com")
                .apiKey("TEST_KEY")
                .status("ACTIVE")
                .createdDate(LocalDate.now())
                .quota(0)
                .build();
    }

    @Test
    @Order(1)
    void persistClient() {
        clientRepository.persist(testClient);

        assertTrue(clientRepository.isPersistent(testClient));
    }

    @Test
    @Order(2)
    void findClientById() {
        ClientEntity foundClient = clientRepository.findById(1);

        assertEquals(foundClient.getId(), 1);
        assertEquals(foundClient.getName(), testClient.getName());
        assertEquals(foundClient.getEmail(), testClient.getEmail());
    }
    @Test
    @Order(3)
    void findClientByApiKey() {
        ClientEntity foundClient = clientRepository.findClientByApiKey("TEST_KEY");

        assertEquals(testClient.getName(), foundClient.getName());
        assertEquals(testClient.getApiKey(), foundClient.getApiKey());
        assertEquals(testClient.getEmail(), foundClient.getEmail());
    }

    @Test
    @Order(4)
    void findClientByEmail() {
        ClientEntity foundClient = clientRepository.findClientByEmail("test@mail.com");

        assertEquals(testClient.getName(), foundClient.getName());
        assertEquals(testClient.getApiKey(), foundClient.getApiKey());
        assertEquals(testClient.getEmail(), foundClient.getEmail());
    }

    @Test
    @Order(5)
    void countClientsByEmail() {
        long result = clientRepository.countClientsByEmail("test@mail.com");

        assertEquals(1, result);
    }

    @Test
    @Order(6)
    void countClientsByApiKey() {
        long result = clientRepository.countClientsByApiKey("TEST_KEY");

        assertEquals(1, result);
    }

    @Test
    @Order(7)
    void listAllClients() {
        List<ClientEntity> allClients = clientRepository.listAll();

        assertEquals(allClients.get(allClients.size() - 1).getName(), testClient.getName());
    }

    @Test
    @Order(8)
    void updateClient() {
        ClientEntity existingClient = clientRepository.findById(1);

        String newKey = "UPDATED_TEST_API_KEY";
        existingClient.setApiKey(newKey);

        ClientEntity updatedClient = clientRepository.findClientByApiKey(newKey);

        assertNotNull(updatedClient);
    }
    @Order(10)
    @Test
    void deleteClientById() {
        ClientEntity found = clientRepository.findById(1);
        assertNotNull(found);

        clientRepository.deleteById(1);
        ClientEntity deletedClient = clientRepository.findById(1);

        assertNull(deletedClient);
    }

}