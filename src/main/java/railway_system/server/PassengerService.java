package railway_system.server;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.CrossOrigin;
import railway_system.dao.*;
import railway_system.entity.Individual;
import railway_system.entity.Ticket;
import railway_system.filters.Logged;
import railway_system.filters.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;

@Path("/passenger")
@CrossOrigin(origins = "http://localhost:3000")
public class PassengerService {
    @Context
    SecurityContext securityContext;
    public PassengerService(){}

    @POST
    @Logged
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response registerPassenger(@FormParam("fname") String fname, @FormParam("lname") String lname,
                                      @FormParam("email") String email, @FormParam("login") String login,
                                      @FormParam("password") String password){
        password = Encryptor.encrypInput(password);

        CrudDao crudDao = new CrudDaoImpl();
        Individual user = new Individual(0, fname, lname, email, login, password,"0","0","0",0);
        try {
            crudDao.createIndividual(user);
        } catch (DaoException e) {
            e.printStackTrace();
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok(Response.Status.ACCEPTED).build();
    }

    @GET
    @Path("/tickets")
    @Secured
    public Response getTickets(@Context SecurityContext securityContext){
        Gson gson = new Gson();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());

        CrudDao crudDao = new CrudDaoImpl();
        List<Ticket> tickets = crudDao.readTicketsOfUser(user_id);

        Ticket[] arr = tickets.toArray(new Ticket[tickets.size()]);
        String json = gson.toJson(arr, Ticket[].class);
        return Response.ok(json).build();
    }

    @GET
    @Path("/type")
    @Secured
    public Response getType(@Context SecurityContext securityContext){
        Gson gson = new Gson();
        MainDao mainDao = new MainDaoImpl();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        String type = "passenger";

        if(mainDao.checkManager(user_id)){
            type = "manager";
        }
        else if(mainDao.checkAgent(user_id)){
            type = "agent";
        }
        String json = "{ \"type\": \"" + type + "\" }";
        return Response.ok(json).build();

    }

    @POST
    @Secured
    @Logged
    @Path("/refund-request")
    public Response requestRefund(@Context SecurityContext securityContext, @FormParam("ticket-id") int ticketId){
        System.out.println("here");
        CrudDao crudDao = new CrudDaoImpl();
        MainDao mainDao = new MainDaoImpl();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());

        if(mainDao.isBelongTo(user_id, ticketId) && crudDao.updateTicketRefund(ticketId, 1)) {
            return Response.ok(Response.Status.ACCEPTED).build();
        }else{
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
