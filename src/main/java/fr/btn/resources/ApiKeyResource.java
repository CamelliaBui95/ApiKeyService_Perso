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
    @Operation(summary = "Return a client by its api key")
    public Response getClientByApiKey(@PathParam("apiKey") String apiKey) {

        if(apiKey == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        ClientEntity foundClient = clientRepository.findClientByApiKey(apiKey);

        if(foundClient == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(new ApiClientDto(foundClient)).build();
    }

    @GET
    @Path("/{apiKey}/mail_count")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Return number of mails of a given client for a given month")
    public Response getMailCountByMonth(@PathParam("apiKey") String apiKey) {
        if(apiKey == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        int result = mailRepository.getMailCountByMonth(apiKey, LocalDate.now().getMonth().getValue());

        return Response.ok(result).build();
    }

    @POST
    @Transactional
    @Path("{apiKey}")
    @Operation(summary = "Save mail in DB")
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
