package railway_system.dao;

import javafx.util.Pair;
import railway_system.connection.ConnectionPool;
import railway_system.connection.ConnectionPoolException;
import railway_system.entity.Station;
import railway_system.entity.Seat;
import railway_system.entity.Train;
import railway_system.entity.TrainInstance;
import railway_system.entity.TrainLeg;

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





    //return all seats of the given train_leg and on the given date
    List<Seat> getSeatsInstance(String date, int train_id, int fromOrder, int toOrder);


    boolean refundTicket(int individual_id, int train_id, int ticketId);

    //check if train_id between from_order and to_order are null
    boolean checkIfAvailable(String date, int seat_number, int wagon_number, int from_order, int to_order, int train_id);

    //change seat instance ticket id between from_order and to_order
    void updateSeatInstances(String date, int seat_num, int wagon_num, int from_order, int to_order, int train_id, int ticket_id);

    boolean authenticated(String username, String password);

    //check if train.is_active = 1
    boolean checkIsActiveTrain(int train_id);


    //return true if user_id is manager
    boolean checkManager(int user_id);


}
