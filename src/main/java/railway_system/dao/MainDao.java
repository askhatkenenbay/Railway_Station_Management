package railway_system.dao;

import javafx.util.Pair;
import railway_system.connection.ConnectionPool;
import railway_system.connection.ConnectionPoolException;
import railway_system.entity.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public interface MainDao extends CloseDao{
    //select all pairs <train_leg1, train_leg2>
    //where train_leg1.station_id == from_id and train_lef2.station_id == to_id and train_leg1.order < train_leg2.order
    //and train_leg1.train_id == train_leg2.train_id and Train(train_leg1.train_id).weekdays like "%weekDay%"
    ArrayList<Pair<TrainLeg, TrainLeg> > getTrainsFromTo(int weekDay, int from_id, int to_id);


    //select all pairs <train_leg1, train_leg2>
    //where train_leg1.station_id == from_id and train_leg1.order < train_leg2.order and train_leg2.order is max
    //and train_leg1.train_id == train_leg2.train_id and Train(train_leg1.train_id).weekdays like "%weekDay%"
    ArrayList<Pair<TrainLeg, TrainLeg>> getTrainsFrom(int weekDay, int from_id);


    //select all pairs <train_leg1, train_leg2>
    //where train_leg2.station_id == to_id and train_leg1.order < train_leg2.order and train_leg1.order is min
    //and train_leg1.train_id == train_leg2.train_id and Train(train_leg1.train_id).weekdays like "%weekDay%"
    ArrayList<Pair<TrainLeg, TrainLeg>> getTrainsTo(int weekDay, int to_id);


    //select all pairs <train_leg1, train_leg2>
    //where train_leg1.order < train_leg2.order and train_leg2.order is max and train_leg1 is min
    //and train_leg1.train_id == train_leg2.train_id and Train(train_leg1.train_id).weekdays like "%weekDay%"
    ArrayList<Pair<TrainLeg, TrainLeg>> getTrains(int weekDay);

    //return Seat, set available to 1, if ticketId of every seat fromOrder to toOrder is null
    Seat getSeat(int wagon_num, int seat_num, String date, int train_id, int fromOrder, int toOrder);




    //change seat instance ticket id between from_order and to_order
    void updateSeatInstances(String date, int seat_num, int wagon_num, int from_order, int to_order, int train_id, int ticket_id);

    //set ticket_id = NULL where seat_instance.ticket_id = ticket_id
    void refundSeatInstances(int ticket_id);

    boolean authenticated(String username, String password);

    //check if train.is_active = 1
    boolean checkIsActiveTrain(int train_id);


    //return true if user_id is manager
    boolean checkManager(int user_id);

    //return true if user if agent
    boolean checkAgent(int user_id);

    //check if Tickets.individual_id = user_id
    boolean isBelongTo(int user_id, int ticket_id);

    //return list of agents' emails
    List<String> getAgentsEmails();

    List<String> getPassengersEmails(int id, String date);

    //return all trains
    List<Train> getTrains();


}
