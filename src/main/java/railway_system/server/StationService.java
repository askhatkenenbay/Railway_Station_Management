package railway_system.server;


import com.google.gson.Gson;
import railway_system.dao.CrudDaoImpl;
import railway_system.entity.Station;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/station")
public class StationService {

    public StationService() {

    }

    @GET
    public Response getAll() {
        //return all stations
        Gson gson = new Gson();
        List<Station> stations = new CrudDaoImpl().readStations();
        Station[] arr = stations.toArray(new Station[stations.size()]);

        String json = gson.toJson(arr, Station[].class);
        return Response.ok(json).build();
    }
}
