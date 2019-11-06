package railway_system.server;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.CrossOrigin;
import railway_system.dao.BaseDao;
import railway_system.dao.BaseDaoImpl;
import railway_system.entity.Ticket;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.ArrayList;

@Path("/passenger")
@CrossOrigin(origins = "http://localhost:3000")
public class PassengerService {
    @Context
    SecurityContext securityContext;
    public PassengerService(){}

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response registerPassenger(@FormParam("fname") String fname, @FormParam("lname") String lname,
                                      @FormParam("email") String email, @FormParam("login") String login,
                                      @FormParam("password") String password){
        password = Encryptor.encrypInput(password);

        BaseDao baseDao = new BaseDaoImpl();
        if(baseDao.registerIndividual(fname, lname, email, login, password)){
            return Response.ok(Response.Status.ACCEPTED).build();
        }else{
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @GET
    @Path("/tickets")
    @Secured
    public Response getTickets(@Context SecurityContext securityContext){
        Gson gson = new Gson();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        BaseDao baseDao = new BaseDaoImpl();
        ArrayList<Ticket> tickets = baseDao.getTicketsOfUser(user_id);

        Ticket[] arr = tickets.toArray(new Ticket[tickets.size()]);
        String json = gson.toJson(arr, Ticket[].class);
        return Response.ok(json).build();
    }

    @GET
    @Path("/type")
    @Secured

    public Response getType(@Context SecurityContext securityContext){
        Gson gson = new Gson();
        BaseDao baseDao = new BaseDaoImpl();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        int type = baseDao.getTypeOfUser(user_id);

        if(type == -1){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String json = "{ \"type\": " + type + " }";
        return Response.ok(json).build();

    }
}
