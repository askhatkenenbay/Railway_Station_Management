package railway_system.server;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/routes")
public class RoutesService {
    public RoutesService(){

    }

    @GET
    public Response getAll(@QueryParam("day") int day, @QueryParam("month") int month, @QueryParam("year") int year, @QueryParam("from") int from_id, @QueryParam("to") int to_id){
        return Response.ok().build();

    }

    @GET
    @Path("/{id: [0-9]+}/tickets")
    public Response getTicket(@QueryParam("day") int day, @QueryParam("month") int month, @QueryParam("year") int year){
        return Response.ok().build();
    }

    @POST
    @Path("/{id: [0-9]+}/tickets")
    public Response buyTicket(){
        return Response.ok().build();

    }

}
