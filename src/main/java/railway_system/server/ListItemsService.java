package railway_system.server;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/items")
public class ListItemsService {
    
    private List<String> list = new CopyOnWriteArrayList<String>();
    Pattern pattern = Pattern.compile("[\\p{Alnum}\\s]{5,}");
    
    public ListItemsService() {
        for (int i = 0; i < 20; i++) {
            list.add("Entry " + String.valueOf(i));
        }
        
        Collections.reverse(list);
    }
    
    @GET
    public Response getList() {
        Gson gson = new Gson();
        
        return Response.ok(gson.toJson(list)).build();
    }
    
    @GET
    @Path("{id: [0-9]+}")
    public Response getListItem(@PathParam("id") String id) {
        int i = Integer.parseInt(id);

        return Response.ok(list.get(i)).build();
    }
    
    @POST
    public Response postListItem(@FormParam("newEntry") String entry) {
        Matcher matcher = pattern.matcher(entry);
        if(matcher.matches()){
            list.add(0, entry);
            return Response.ok().build();
        }
        else{
            return Response.status(422).entity("Entry format is not correct, Only Alphanumeric characters with min length 5").build();
        }
    }


    @DELETE
    @Path("{id: [0-9]+}")
    public Response deleteListItem(@PathParam("id") String id){
        int i = Integer.parseInt(id);
        if(i < list.size()){
            list.remove(i);
        }
        return Response.ok().build();
    }

    @DELETE
    public Response deleteAllItems(){
        list.clear();
        return Response.ok().build();
    }





}
