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

public interface MainDao {
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

    //get Station by ID
    public Station getStation(int id);



    //return all seats of the given train_leg and on the given date
    ArrayList<Seat> getSeats(String date, int train_id, int fromOrder, int toOrder);

    Boolean refundTicket(int user_id, int train_id, int ticketId);

    default void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    default void close(Connection connection) {
        if (connection != null) {
            try {
                ConnectionPool.INSTANCE.releaseConnection(connection);
            } catch (ConnectionPoolException e) {
                e.printStackTrace();
            }
        }
    }
}
