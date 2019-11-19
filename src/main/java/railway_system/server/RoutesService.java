package railway_system.server;


import com.google.gson.Gson;
import javafx.util.Pair;
import railway_system.dao.*;
import railway_system.entity.*;

import javax.json.bind.Jsonb;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Path("/routes")
public class RoutesService {
    @Context
    SecurityContext securityContext;

    public RoutesService(){

    }
    @GET
    public Response getAll(@QueryParam("day") int day, @QueryParam("month") int month, @QueryParam("year") int year,
                           @QueryParam("from") int from_id, @QueryParam("to") int to_id){
        Gson gson = new Gson();
        String dateString = String.valueOf(day) + '/' + String.valueOf(month) + '/' + String.valueOf(year);
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/M/yyyy").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        MainDao mainDao = new MainDaoImpl();
        ArrayList<Pair<TrainLeg, TrainLeg>> result = null;

        if(from_id == 0  && to_id == 0){
            result = mainDao.getTrains(dayOfWeek);
        }
        else if(from_id == 0 && to_id != 0){
            result = mainDao.getTrainsTo(dayOfWeek, to_id);
        }
        else if(from_id != 0 && to_id == 0){
            result = mainDao.getTrainsFrom(dayOfWeek, from_id);
        }
        else{
            result = mainDao.getTrainsFromTo(dayOfWeek, from_id, to_id);
        }

        ArrayList<TrainInstance> trains = new ArrayList<>();
        for(Pair<TrainLeg, TrainLeg> legs : result){
            trains.add( new TrainInstance(legs.getKey(), legs.getValue(), c));
        }

        TrainInstance[] arr = trains.toArray(new TrainInstance[trains.size()]);

        String json = gson.toJson(arr, TrainInstance[].class);
        return Response.ok(json).build();
    }

    @GET
    @Path("/{id: [0-9]+}/tickets")
    public Response getSeats(@QueryParam("day") int day, @QueryParam("month") int month, @QueryParam("year") int year,
                              @PathParam("id") String id, @QueryParam("fromOrder") int fromOrder, @QueryParam("toOrder") int toOrder){
        Gson gson = new Gson();
        String date = String.valueOf(year) + '-' + String.valueOf(month) + '-' + String.valueOf(day);
        int train_id = Integer.parseInt(id);
        TrainType trainType = new CrudDaoImpl().readTrainType(train_id);
        List<Seat> seats = new ArrayList<>();
        for(int i = 0; i < trainType.getWagonAmount(); i++){
            for(int j = 0; j < trainType.getWagonCapacity(); j++){
                seats.add(new MainDaoImpl().getSeat(i, j, date, train_id, fromOrder, toOrder));
            }
        }

        Seat[] arr = seats.toArray(new Seat[seats.size()]);
        String json = gson.toJson(arr, Seat[].class);
        return Response.ok(json).build();
    }

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/{id: [0-9]+}/tickets")
    public Response buyTicket(@Context SecurityContext securityContext, @FormParam("wagon_number") int wagon_number,
                              @FormParam("place") int place, @FormParam("init-date") String init_date, @FormParam("departure-datetime") String departure_datetime,
                              @FormParam("arrival-datetime") String arrival_datetime, @PathParam("id") String id,
                              @FormParam("from-order") int from_order, @FormParam("to-order") int to_order,
                              @FormParam("from") int from_id, @FormParam("to") int to_id,
                              @FormParam("first-name") String first_name, @FormParam("last-name") String last_name,
                              @FormParam("document-type") String doctype, @FormParam("document-id") String doc_id){

        MainDao mainDao = new MainDaoImpl();
        int train_id = Integer.parseInt(id);
        Gson gson = new Gson();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());

        if(!mainDao.checkIfAvailable(init_date, place, wagon_number, from_order, to_order, train_id)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }

        Ticket ticket = new Ticket(0, train_id, from_id, to_id, user_id, first_name, last_name, doctype, doc_id, false, arrival_datetime, departure_datetime);
        CrudDao crudDao = new CrudDaoImpl();
        int ticket_id = crudDao.createTicket(ticket);

        mainDao.updateSeatInstances(init_date, place, wagon_number, from_order, to_order, train_id, ticket_id);
        return Response.ok().build();
    }
}
