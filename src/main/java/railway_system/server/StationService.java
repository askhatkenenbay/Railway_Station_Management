package railway_system.server;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/station")
public class StationService {

    public StationService(){

    }

    @GET
    public Response getAll(){
        //return all stations
        return Response.ok().build();
    }
}
