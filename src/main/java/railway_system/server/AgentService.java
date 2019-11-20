package railway_system.server;

import com.google.gson.Gson;
import railway_system.dao.*;
import railway_system.entity.Ticket;
import railway_system.filters.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;

@Path("/agent")
public class AgentService {
    public AgentService(){}

    boolean checkIsAgent(SecurityContext securityContext){
        MainDao mainDao = new MainDaoImpl();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        if(!mainDao.checkAgent(user_id)){
            return false;
        }
        return true;
    }

    @Secured
    @POST
    @Path("/reject-refund")
    public Response rejectRefund(@Context SecurityContext securityContext, @FormParam("ticket-id") int ticketId){
        if(!checkIsAgent(securityContext)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        CrudDao crudDao = new CrudDaoImpl();
        if(crudDao.updateTicketRefund(ticketId, 0)){
            return Response.ok(Response.Status.ACCEPTED).build();
        }
        return Response.ok(Response.Status.FORBIDDEN).build();
    }

    @Secured
    @DELETE
    @Path("/accept-refund")
    public Response acceptRefund(@Context SecurityContext securityContext, @FormParam("ticket-id") int ticketId){
        if(!checkIsAgent(securityContext)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        CrudDao crudDao = new CrudDaoImpl();
        try {
            crudDao.deleteTicket(ticketId);
        } catch (DaoException e) {
            e.printStackTrace();
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        return Response.ok(Response.Status.ACCEPTED).build();
    }

    @Secured
    @GET
    @Path("/refund-requests")
    public Response getRefundRequests(@Context SecurityContext securityContext){
        if(!checkIsAgent(securityContext)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        Gson gson = new Gson();
        List tickets = new CrudDaoImpl().readWaitingTickets();
        Ticket[] arr = (Ticket[]) tickets.toArray(new Ticket[tickets.size()]);
        String json = gson.toJson(arr, Ticket[].class);
        return Response.ok(json).build();
    }

    @Secured
    @POST
    @Path("/create-seat-instances")
    public Response createSeatInstances(@Context SecurityContext securityContext, @FormParam("train-id") int train_id, @FormParam("day") int day, @FormParam("month") int month, @FormParam("year") int year){
        String date = String.valueOf(year) + '-' + String.valueOf(month) + '-' + String.valueOf(day);
        CrudDao crudDao = new CrudDaoImpl();
        try {
            crudDao.createSeatInstances(train_id, date);
        } catch (DaoException e) {
            e.printStackTrace();
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        return Response.ok(Response.Status.ACCEPTED).build();
    }
}
