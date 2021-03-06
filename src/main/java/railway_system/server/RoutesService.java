package railway_system.server;


import com.google.gson.Gson;
import javafx.util.Pair;
import railway_system.dao.*;
import railway_system.entity.*;
import railway_system.filters.Logged;
import railway_system.filters.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
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

    public RoutesService() {

    }

    @GET
    @Logged
    public Response getAll(@QueryParam("day") int day, @QueryParam("month") int month, @QueryParam("year") int year,
                           @QueryParam("from") int from_id, @QueryParam("to") int to_id) {
        Gson gson = new Gson();
        String dateString = String.valueOf(day) + '/' + month + '/' + year;
        Date date;
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
        ArrayList<Pair<TrainLeg, TrainLeg>> result;

        if (from_id == 0 && to_id == 0) {
            result = mainDao.getTrains(dayOfWeek);
        } else if (from_id == 0) { // always true if reached to_id != 0
            result = mainDao.getTrainsTo(dayOfWeek, to_id);
        } else if (to_id == 0) { // always true if reached from_id != 0
            result = mainDao.getTrainsFrom(dayOfWeek, from_id);
        } else {
            result = mainDao.getTrainsFromTo(dayOfWeek, from_id, to_id);
        }

        ArrayList<TrainInstance> trains = new ArrayList<>();
        for (Pair<TrainLeg, TrainLeg> legs : result) {
            trains.add(new TrainInstance(legs.getKey(), legs.getValue(), c));
        }
        TrainInstance[] arr = trains.toArray(new TrainInstance[trains.size()]);

        String json = gson.toJson(arr, TrainInstance[].class);
        return Response.ok(json).build();
    }

    @GET
    @Path("/{id: [0-9]+}/tickets")
    public Response getSeats(@QueryParam("day") int day, @QueryParam("month") int month, @QueryParam("year") int year,
                             @PathParam("id") String id, @QueryParam("fromOrder") int fromOrder, @QueryParam("toOrder") int toOrder) {
        Gson gson = new Gson();
        String date = String.valueOf(year) + '-' + month + '-' + day;
        int train_id = Integer.parseInt(id);
        TrainType trainType = new CrudDaoImpl().readTrainType(train_id);
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= trainType.getWagonAmount(); i++) {
            for (int j = 1; j <= trainType.getWagonCapacity(); j++) {
                seats.add(new MainDaoImpl().getSeat(i, j, date, train_id, fromOrder, toOrder));
            }
        }

        Seat[] arr = seats.toArray(new Seat[seats.size()]);
        String json = gson.toJson(arr, Seat[].class);
        return Response.ok(json).build();
    }

    @POST
    @Secured
    @Logged
    @Path("/{id: [0-9]+}/tickets")
    public Response buyTicket(@Context SecurityContext securityContext, @FormParam("wagon_number") int wagon_number,
                              @FormParam("place") int place, @FormParam("init-date") String init_date, @FormParam("departure-datetime") String departure_datetime,
                              @FormParam("arrival-datetime") String arrival_datetime, @PathParam("id") String id,
                              @FormParam("from-order") int from_order, @FormParam("to-order") int to_order,
                              @FormParam("from") int from_id, @FormParam("to") int to_id,
                              @FormParam("first-name") String first_name, @FormParam("last-name") String last_name,
                              @FormParam("document-type") String doctype, @FormParam("document-id") String doc_id) {

        MainDao mainDao = new MainDaoImpl();
        int train_id = Integer.parseInt(id);
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());

        if (!mainDao.getSeat(wagon_number, place, init_date, train_id, from_order, to_order).isAvailable() || to_order == from_order
                || !mainDao.checkIsActiveTrain(train_id)) {
            System.out.println("forbid");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Ticket ticket = new Ticket(0, train_id, from_id, to_id, user_id, first_name, last_name, doctype, doc_id, false, arrival_datetime, departure_datetime, place, wagon_number);
        CrudDao crudDao = new CrudDaoImpl();
        int ticket_id;
        try {
            ticket_id = crudDao.createTicket(ticket);
        } catch (DaoException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        mainDao.updateSeatInstances(init_date, place, wagon_number, from_order, to_order, train_id, ticket_id);
        return Response.ok(Response.Status.ACCEPTED).build();
    }
}
