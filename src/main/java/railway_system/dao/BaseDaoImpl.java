package railway_system.dao;

import railway_system.connection.ConnectionPool;
import railway_system.entity.Station;
import railway_system.entity.Ticket;
import railway_system.entity.Train;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BaseDaoImpl implements BaseDao {
    private static final String SELECT_CITY_NAME_ID_FROM_STATION = "SELECT city,name,station_id from station";
    private static final String SELECT_TRAIN_FROM_ID = "SELECT * from train WHERE origin_id=?";
    private static final String SELECT_TRAIN_FROM_ID_AND_TO_ID = "SELECT * from train WHERE origin_id=? and destination_id=?";
    private static final String SELECT_TRAIN_TO_ID = "SELECT * from train WHERE destination_id=?";
    private static final String SELECT_ALL_FROM_TRAIN = "SELECT * FROM train";
    private static final String STATION_CITY = "city";
    private static final String STATION_NAME = "name";
    private static final String STATION_ID = "station_id";

    public ArrayList<Station> getAllStations() {
        Connection connection = null;
        ArrayList<Station> resultList = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            resultList = new ArrayList<>();
            preparedStatement = connection.prepareStatement(SELECT_CITY_NAME_ID_FROM_STATION);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                resultList.add(new Station(resultSet.getString(STATION_CITY),
                        resultSet.getString(STATION_NAME), resultSet.getInt(STATION_ID)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
        return resultList;
    }

    @Override
    public ArrayList<Train> getAllTrains(char weekDay, int from_id, int to_id) {
        Connection connection = null;
        ArrayList<Train> resultList = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            resultList = new ArrayList<>();
            if (from_id == 0 && to_id != 0) {
                preparedStatement = connection.prepareStatement(SELECT_TRAIN_TO_ID);
                preparedStatement.setInt(1, to_id);
            } else if (from_id != 0 && to_id == 0) {
                preparedStatement = connection.prepareStatement(SELECT_TRAIN_FROM_ID);
                preparedStatement.setInt(1, from_id);
            } else if (from_id != 0 && to_id != 0) {
                preparedStatement = connection.prepareStatement(SELECT_TRAIN_FROM_ID_AND_TO_ID);
                preparedStatement.setInt(1, from_id);
                preparedStatement.setInt(2, to_id);
            } else {
                preparedStatement = connection.prepareStatement(SELECT_ALL_FROM_TRAIN);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("week_days").contains(String.valueOf(weekDay))) {
                    resultList.add(new Train(resultSet.getInt("ID"), resultSet.getString("arrival_time"),
                            resultSet.getString("departure_time"), resultSet.getInt("capacity_carriage"),
                            resultSet.getInt("number_carriage"), resultSet.getString("type_carriage"),
                            resultSet.getString("week_days"), resultSet.getInt("destination_id"),
                            resultSet.getInt("origin_id"), resultSet.getInt("arrival_day")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
        return resultList;
    }

    @Override
    public ArrayList<Ticket> getAllTickets(String date, int train_id) {
        return null;
    }

    @Override
    public boolean authenticated(String username, String password) {
        return false;
    }
}
