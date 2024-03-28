package fr.btn.dtos;

import fr.btn.entities.ClientEntity;
import lombok.*;

@Getter
@Setter
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
