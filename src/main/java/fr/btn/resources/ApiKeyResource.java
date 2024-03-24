package fr.btn.resources;

import fr.btn.dtos.ApiClientDto;
import fr.btn.models.MailClient;
import fr.btn.entities.ClientEntity;
import fr.btn.entities.MailEntity;
import fr.btn.repositories.ClientRepository;
import fr.btn.repositories.MailRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Path("/apiKey")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Api Key Service")
public class ApiKeyResource {
    @Inject
    ClientRepository clientRepository;

    @Inject
    MailRepository mailRepository;

    @GET
    @Path("{apiKey}")
    @Operation(description = "Return a client by its api key")
    public ApiClientDto getClientByApiKey(@PathParam("apiKey") String apiKey) {

        if(apiKey == null)
            return null;

        ClientEntity foundClient = clientRepository.findClientByApiKey(apiKey);

        if(foundClient == null)
            return null;

        return new ApiClientDto(foundClient);
    }

    @GET
    @Path("/{apiKey}/mail_count")
    @Operation(description = "Return number of mails of a given client for a given month")
    public int getMailCountByMonth(@PathParam("apiKey") String apiKey) {
        if(apiKey == null)
            return -1;

        return mailRepository.getMailCountByMonth(apiKey, LocalDate.now().getMonth().getValue());
    }

    @POST
    @Transactional
    @Path("{apiKey}")
    @Operation(description = "Save mail in DB")
    public Response saveMail(@PathParam("apiKey") String apiKey, MailClient mailClient) {
        if(apiKey == null || mailClient == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        ClientEntity clientEntity = clientRepository.findClientByApiKey(apiKey);
        if (clientEntity == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        MailEntity mailEntity = MailEntity
                .builder()
                .subject(mailClient.getSubject())
                .recipient(mailClient.getRecipient())
                .sender(clientEntity)
                .date(LocalDateTime.now())
                .build();

        try {
            mailRepository.persist(mailEntity);
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }
}
