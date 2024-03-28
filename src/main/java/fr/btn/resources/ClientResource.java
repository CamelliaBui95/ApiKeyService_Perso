package fr.btn.resources;

import fr.btn.dtos.ClientDto;
import fr.btn.models.Mail;
import fr.btn.entities.ClientEntity;
import fr.btn.repositories.ClientRepository;
import fr.btn.services.MailService;
import fr.btn.utils.Argon2;
import fr.btn.utils.Utils;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.util.List;

@Path("/clients")
@Tag(name="Client Service")
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {
    private static final String API_KEY="TEwLHA9MSQ2dE5CY0VhdU81QnBhNUtTb0lWd2lRJGlUNlgyWHFsN3g5VTNR";
    @Inject
    @RestClient
    MailService mailService;

    @Inject
    ClientRepository clientRepository;

    @GET
    public Response getAll() {
        List<ClientEntity> clientEntities = clientRepository.listAll();

        List<ClientDto> clientDtos = ClientDto.toDtoList(clientEntities);

        return Response.ok(clientDtos).build();
    }

    @GET
    @Path("{id}")
    public Response getClientById(@PathParam("id") Integer id) {

        ClientEntity foundClient = clientRepository.findById(id);

        if(foundClient == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(new ClientDto(foundClient, true)).build();
    }

    @POST
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response createNewClient(@FormParam("name") String name, @FormParam("email") String email, @FormParam("quota") Integer quota) {

        if(!Utils.validateEmail(email) || name == null || name.isEmpty() || quota == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        if(clientRepository.countClientsByEmail(email) > 0)
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();

        String apiKey = generateApiKey(email);

        ClientEntity newClientEntity = ClientEntity
                .builder()
                .name(name)
                .email(email)
                .apiKey(apiKey)
                .quota(quota)
                .status("ACTIVE")
                .createdDate(LocalDate.now())
                .build();

        clientRepository.persist(newClientEntity);

        try(Response res = sendMail(email, "New Client", String.format("Your new API KEY: %s", apiKey))) {
            if(res.getStatus() == 200)
                return Response.ok("Api Key has been sent by mail.").status(Response.Status.CREATED).build();

        } catch(Exception e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @PUT
    @Transactional
    @Path("/new_key")
    public Response renewApiKey(@QueryParam("email") String email) {
        if(!Utils.validateEmail(email))
            return Response.status(Response.Status.BAD_REQUEST).build();

        ClientEntity existingClient = clientRepository.findClientByEmail(email);

        if(existingClient == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        try {
            String newApiKey = generateApiKey(email);

            existingClient.setApiKey(newApiKey);
            return sendMail(email, "New Key", String.format("Your new API KEY: %s", newApiKey));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Response sendMail(String recipient, String subject, String content) {
        Mail mail = Mail
                .builder()
                .recipient(recipient)
                .subject(subject)
                .content(content)
                .build();

        return mailService.send(API_KEY, mail);
    }

    @PUT
    @Path("/new_quota")
    @Transactional
    public Response renewQuota(@FormParam("email") String email, @FormParam("quota") Integer quota) {
        if(!Utils.validateEmail(email) || quota == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        ClientEntity foundClient = clientRepository.findClientByEmail(email);

        if(foundClient == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        foundClient.setQuota(quota);

        return Response.ok(new ClientDto(foundClient, false)).build();
    }

    private String generateApiKey(String email) {
        String apiKey = "";

        do {
           apiKey = Argon2.getHashedData(email).substring(0, 16);
        } while(clientRepository.countClientsByApiKey(apiKey) > 0);

        return apiKey;
    }
}
