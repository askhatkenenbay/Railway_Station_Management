package railway_system.server;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/agent")
public class AgentService {
    public AgentService(){}

    @Secured
    @POST
    @Path("/reject")
    public Response rejectRefund(@Context SecurityContext securityContext, @FormParam("ticket-id") int ticketId, @FormParam("train-id") int trainId){
        return null;

    }
}
