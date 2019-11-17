package railway_system.server;


import com.google.gson.Gson;
import javafx.util.Pair;
import railway_system.dao.BaseDao;
import railway_system.dao.BaseDaoImpl;
import railway_system.dao.MainDao;
import railway_system.entity.Ticket;
import railway_system.entity.Train;
import railway_system.entity.TrainInstance;
import railway_system.entity.TrainLeg;

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
            trains.add( new TrainInstance(legs.getKey(), legs.getValue()));
        }

        TrainInstance[] arr = trains.toArray(new TrainInstance[trains.size()]);

        String json = gson.toJson(arr, TrainInstance[].class);
        return Response.ok(json).build();
    }

    @GET
    @Path("/{id: [0-9]+}/tickets")
    public Response getTicket(@QueryParam("day") int day, @QueryParam("month") int month, @QueryParam("year") int year,
                              @PathParam("id") String id){
        Gson gson = new Gson();
        String date = String.valueOf(year) + '-' + String.valueOf(month) + '-' + String.valueOf(day);
        int train_id = Integer.parseInt(id);
        ArrayList<Ticket> tickets = new BaseDaoImpl().getAllTickets(date, train_id);

        Ticket[] arr = tickets.toArray(new Ticket[tickets.size()]);
        String json = gson.toJson(arr, Ticket[].class);
        return Response.ok(json).build();
    }

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/{id: [0-9]+}/tickets")
    public Response buyTicket(@Context SecurityContext securityContext, @FormParam("wagon_number") int wagon_number,
                              @FormParam("place") int place, @FormParam("day") int day, @FormParam("month") int month,
                              @FormParam("year") int year, @PathParam("id") String id){
        int train_id = Integer.parseInt(id);
        Gson gson = new Gson();
        String date = String.valueOf(year) + '-' + String.valueOf(month) + '-' + String.valueOf(day);
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        if (new BaseDaoImpl().buyTicket(user_id, train_id, wagon_number, place, date)) {
            return Response.ok(Response.Status.ACCEPTED).build();
        }else{
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/{id: [0-9]+}/create-ticket")
    public Response createTicket(@Context SecurityContext securityContext, @FormParam("wagon_number") int wagon_number, @FormParam("place") int place,
                                 @FormParam("day") int day, @FormParam("month") int month, @FormParam("price") double price, @FormParam("seat_type") String seat_type,
                                 @FormParam("year") int year, @PathParam("id") String id){
        int train_id = Integer.parseInt(id);
        BaseDao baseDao = new BaseDaoImpl();
        String date = String.valueOf(year) + '-' + String.valueOf(month) + '-' + String.valueOf(day);
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        if(!baseDao.checkAgent(user_id)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        if(baseDao.createTicket(place, wagon_number, price, seat_type, date, train_id)) {
            return Response.ok(Response.Status.ACCEPTED).build();
        }else{
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
    }

    @DELETE
    @Secured
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/{train_id: [0-9]+}/{place: [0-9]+}/delete-ticket")
    public Response deleteTicket(@Context SecurityContext securityContext, @FormParam("wagon_number") int wagon_number,
                                 @FormParam("day") int day, @FormParam("month") int month, @FormParam("year") int year, @PathParam("train_id") String id, @PathParam("place") String place){
        int train_id = Integer.parseInt(id);
        int  place_num = Integer.parseInt(place);

        BaseDao baseDao = new BaseDaoImpl();
        String date = String.valueOf(year) + '-' + String.valueOf(month) + '-' + String.valueOf(day);
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        if(baseDao.deleteTicket(user_id, place_num, wagon_number, date, train_id)) {
            return Response.ok(Response.Status.ACCEPTED).build();
        }else{
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
    }

}
