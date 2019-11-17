package railway_system.dao;

import javafx.util.Pair;
import railway_system.connection.ConnectionPool;
import railway_system.entity.Seat;
import railway_system.entity.Station;
import railway_system.entity.TrainLeg;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class MainDaoImpl implements MainDao{
    private static final String GET_TRAINS_BY_WEEKDAYS = "SELECT T1.arrival_time as T1arrival_time, T1.departure_time as T1departure_time,\n" +
            "T1.order as T1order, T1.station_id as T1station_id, T1.train_id as T1train_id, T1.arrival_day as T1arrival_day,\n" +
            "T2.arrival_time, T2.departure_time, T2.order, T2.station_id, T2.train_id, T2.arrival_day\n" +
            "from train_leg T1, train_leg T2, week_day W1\n" +
            "where T1.train_id = T2.train_id and\n" +
            "W1.train_id = T1.train_id and W1.weekId = ? and T1.order =\n" +
            "(SELECT Min(`order`) as Min from train_leg Q where Q.train_id=T1.train_id) \n" +
            "and T2.order = \n" +
            "(SELECT Max(`order`) as Min from train_leg Q where Q.train_id=T2.train_id)";

    private static final String GET_TRAINS_BY_WEEKDAYS_TO_ID = "SELECT T1.arrival_time as T1arrival_time, T1.departure_time as T1departure_time,\n" +
            "T1.order as T1order, T1.station_id as T1station_id, T1.train_id as T1train_id, T1.arrival_day as T1arrival_day,\n" +
            "T2.arrival_time, T2.departure_time, T2.order, T2.station_id, T2.train_id, T2.arrival_day\n" +
            "from train_leg T1, train_leg T2, week_day W1\n" +
            "where T1.train_id = T2.train_id and T2.station_id = ? and\n" +
            "W1.train_id = T1.train_id and W1.weekId = ? and T1.order = (SELECT Min(`order`) as Min from train_leg Q where Q.train_id=T1.train_id )";

    private static final String GET_TRAINS_BY_WEEKDAYS_FROM_ID = "SELECT T1.arrival_time as T1arrival_time, T1.departure_time as T1departure_time,\n" +
            "T1.order as T1order, T1.station_id as T1station_id, T1.train_id as T1train_id, T1.arrival_day as T1arrival_day,\n" +
            "T2.arrival_time, T2.departure_time, T2.order, T2.station_id, T2.train_id, T2.arrival_day\n" +
            " from train_leg T1, week_day W1, train_leg T2 where T1.train_id=T2.train_id and\n" +
            "T1.train_id = W1.train_id and MOD(W1.weekId+T1.arrival_day,6) = ? \n" +
            "and T1.station_id = ? \n" +
            "and T2.order = (SELECT Max(`order`) as Max from train_leg Q where Q.train_id=T1.train_id );";

    private static final String GET_TRAINS_BY_WEEKDAYS_FROM_ID_TO_ID = "select T1.arrival_time as T1arrival_time, T1.departure_time as T1departure_time,\n" +
            "T1.order as T1order, T1.station_id as T1station_id, T1.train_id as T1train_id, T1.arrival_day as T1arrival_day,\n" +
            "T2.arrival_time, T2.departure_time, T2.order, T2.station_id, T2.train_id, T2.arrival_day\n" +
            "from train_leg T1, train_leg T2, week_day W1 where T1.train_id = T2.train_id\n" +
            "and W1.train_id = T1.train_id and T1.station_id = ? and T2.station_id = ? and\n" +
            "T1.`order` < T2.`order` and W1.weekId = ?";
    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrainsFromTo(int weekDay, int from_id, int to_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_TRAINS_BY_WEEKDAYS_FROM_ID_TO_ID);
            preparedStatement.setInt(1,from_id);
            preparedStatement.setInt(2,to_id);
            preparedStatement.setInt(3,weekDay);
            resultSet = preparedStatement.executeQuery();
            return helperTrain(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(preparedStatement);
            close(connection);
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrainsFrom(int weekDay, int from_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_TRAINS_BY_WEEKDAYS_FROM_ID);
            preparedStatement.setInt(1,weekDay);
            preparedStatement.setInt(2,from_id);
            resultSet = preparedStatement.executeQuery();
            return helperTrain(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(preparedStatement);
            close(connection);
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrainsTo(int weekDay, int to_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_TRAINS_BY_WEEKDAYS_TO_ID);
            preparedStatement.setInt(1,to_id);
            preparedStatement.setInt(2,weekDay);
            resultSet = preparedStatement.executeQuery();
            return helperTrain(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(preparedStatement);
            close(connection);
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrains(int weekDay) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_TRAINS_BY_WEEKDAYS);
            preparedStatement.setInt(1,weekDay);
            resultSet = preparedStatement.executeQuery();
            return helperTrain(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(preparedStatement);
            close(connection);
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    private ArrayList<Pair<TrainLeg, TrainLeg>> helperTrain(ResultSet resultSet) throws SQLException {
        ArrayList<Pair<TrainLeg, TrainLeg>> result = new ArrayList<>();
        while(resultSet.next()){
            Pair<TrainLeg, TrainLeg> toAdd = new Pair<>(new TrainLeg(resultSet.getInt("T1train_id"),resultSet.getInt("T1order"),
                    resultSet.getInt("T1station_id"), resultSet.getString("T1arrival_time"),
                    resultSet.getString("T1departure_time"),resultSet.getInt("T1arrival_day")),
                    new TrainLeg(resultSet.getInt("train_id"),resultSet.getInt("order"),
                            resultSet.getInt("station_id"), resultSet.getString("arrival_time"),
                            resultSet.getString("departure_time"),resultSet.getInt("arrival_day")));
            result.add(toAdd);
        }
        return result;
    }

    @Override
    public Station getStation(int id) {
        return null;
    }

    @Override
    public ArrayList<Seat> getSeats(String date, int train_id, int fromOrder, int toOrder) {
        return null;
    }

    @Override
    public Boolean refundTicket(int user_id, int train_id, int ticketId) {
        return null;
    }

    @Override
    public boolean checkIfAvailable(String date, int seat_number, int wagon_number, int from_order, int to_order, int train_id) {
        return false;
    }

    @Override
    public void updateSeatInstances(String date, int seat_num, int wagon_num, int from_order, int to_order, int train_id, int ticket_id) {

    }

    @Override
    public boolean authenticated(String username, String password) {
        return false;
    }

    @Override
    public Boolean checkManager(int user_id) {
        return null;
    }
}
