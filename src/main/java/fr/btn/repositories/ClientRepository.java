package fr.btn.repositories;

import fr.btn.entities.ClientEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ClientRepository implements PanacheRepositoryBase<ClientEntity, Integer> {
    public ClientEntity findClientByApiKey(String apiKey) {
        return find("apiKey = ?1", apiKey).firstResult();
    }

    public ClientEntity findClientByEmail(String email) {
        return find("email = ?1", email).firstResult();
    }

    public long countClientsByEmail(String email) {
        return count("email=?1", email);
    }

    public long countClientsByApiKey(String apiKey) {
        return count("apiKey = ?1", apiKey);
    }
}


