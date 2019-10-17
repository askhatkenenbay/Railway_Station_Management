package railway_system.dao;

import railway_system.connection.ConnectionPool;
import railway_system.entity.Station;
import railway_system.entity.Ticket;
import railway_system.entity.Train;

import java.sql.*;
import java.util.ArrayList;

public class BaseDaoImpl implements BaseDao {
    private static final String SELECT_CITY_NAME_ID_FROM_STATION = "SELECT city,name,station_id from station";
    private static final String SELECT_TRAIN_FROM_ID = "SELECT * from train WHERE origin_id=?";
    private static final String SELECT_TRAIN_FROM_ID_AND_TO_ID = "SELECT * from train WHERE origin_id=? and destination_id=?";
    private static final String SELECT_TRAIN_TO_ID = "SELECT * from train WHERE destination_id=?";
    private static final String SELECT_ALL_FROM_TRAIN = "SELECT * FROM train";
    private static final String SELECT_TICKET_BY_ID_AND_DATE = "select * from ticket where date=? and Train_ID=?";
    private static final String SELECT_BY_USERNAME_AND_PASSWORD_FROM_INDIVIDUAL = "select login,password from individual where login=? and password=?";
    private static final String INSERT_INTO_INDIVIDUAL = "Insert into individual(FName,LName,email,login,password) values (?,?,?,?,?)";
    private static final String UPDATE_INDIVIDUAL_LOGIN = "UPDATE individual set remember=? where login=?";
    private static final String COUNT_BY_REMEMBER_IN_INDIVIDUAL = "select count(*) from individual where remember=?";
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
    public int checkToken(String token) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(COUNT_BY_REMEMBER_IN_INDIVIDUAL);
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
        return -1;
    }

    @Override
    public void setToken(String username, String token) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_INDIVIDUAL_LOGIN);
            preparedStatement.setString(1, token);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
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
        Connection connection = null;
        ArrayList<Ticket> resultList = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            resultList = new ArrayList<>();
            preparedStatement = connection.prepareStatement(SELECT_TICKET_BY_ID_AND_DATE);
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.setInt(2, train_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                boolean isAvailable = resultSet.getInt("passenger_individual_ID") == 0;
                resultList.add(new Ticket(resultSet.getInt("Train_ID"), resultSet.getInt("place"),
                        resultSet.getInt("carriage_number"), resultSet.getDouble("price"),
                        resultSet.getString("seat_type"), resultSet.getString("date"), isAvailable));
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
    public boolean authenticated(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_BY_USERNAME_AND_PASSWORD_FROM_INDIVIDUAL);
            return preparedStatement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
        return false;
    }

    @Override
    public boolean registerIndividual(String fname, String lname, String email, String login, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_INTO_INDIVIDUAL);
            preparedStatement.setString(1, fname);
            preparedStatement.setString(2, lname);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, login);
            preparedStatement.setString(5, password);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }
}
