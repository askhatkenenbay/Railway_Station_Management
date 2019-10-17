package railway_system.server;


import com.google.gson.Gson;
import railway_system.dao.BaseDaoImpl;
import railway_system.entity.Station;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/station")
public class StationService {

    public StationService(){

    }

    @GET
    public Response getAll(){
        //return all stations
        Gson gson = new Gson();
        ArrayList<Station> stations = new BaseDaoImpl().getAllStations();
        String json = gson.toJson(stations, ArrayList<>.class);




        return Response.ok().build();
    }
}
