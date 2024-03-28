package fr.btn.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.btn.entities.ClientEntity;
import fr.btn.entities.MailEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClientDto {
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonProperty(index = 1)
    private Integer id;

    @JsonProperty(index = 2)
    private String name;

    @JsonProperty(index = 3)
    private String email;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonProperty(index = 4)
    private Integer quota;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonProperty(index = 5)
    private LocalDate createdDate;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonProperty(index = 6)
    private String status;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonProperty(index = 7)
    private List<ClientMailDto> mails;

    public ClientDto(ClientEntity clientEntity, boolean withMails) {
        this.id = clientEntity.getId();
        this.name = clientEntity.getName();
        this.email = clientEntity.getEmail();
        this.quota = clientEntity.getQuota();
        this.createdDate = clientEntity.getCreatedDate();
        this.status = clientEntity.getStatus();

        if(withMails) {
            this.mails = new ArrayList<>();

            for (MailEntity mailEntity : clientEntity.getMails()) {
                ClientMailDto mailDto = new ClientMailDto();

                mailDto.id = mailEntity.getId();
                mailDto.subject = mailEntity.getSubject();
                mailDto.sender = mailEntity.getSender().getName();
                mailDto.recipient = mailEntity.getRecipient();
                mailDto.date = mailEntity.getDate();

                this.mails.add(mailDto);
            }
        }
    }

    public static List<ClientDto> toDtoList(List<ClientEntity> clientEntities) {
        List<ClientDto> clientDtos = new ArrayList<>();

        for(ClientEntity clientEntity : clientEntities)
           clientDtos.add(new ClientDto(clientEntity, false));

        return clientDtos;
    }

    @Getter
    public class ClientMailDto {
        Integer id;
        String subject;
        String sender;
        String recipient;
        LocalDateTime date;
    }
}
