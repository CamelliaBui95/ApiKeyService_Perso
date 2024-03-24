package fr.btn.dtos;

import fr.btn.entities.ClientEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiClientDto {
    private int quota;
    private String status;

    public ApiClientDto(ClientEntity clientEntity) {
        this.quota = clientEntity.getQuota();
        this.status = clientEntity.getStatus();
    }
}
