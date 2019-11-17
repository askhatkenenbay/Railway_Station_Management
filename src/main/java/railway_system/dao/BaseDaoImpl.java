package railway_system.dao;

import railway_system.connection.ConnectionPool;
import railway_system.entity.Seat;
import railway_system.entity.Station;
import railway_system.entity.Ticket;
import railway_system.entity.Train;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseDaoImpl implements BaseDao {
    private static final String SELECT_CITY_NAME_ID_FROM_STATION = "SELECT city,name,station_id from station";
    private static final String SELECT_TRAIN_FROM_ID = "SELECT * from train WHERE origin_id=?";
    private static final String SELECT_TRAIN_FROM_ID_AND_TO_ID = "SELECT * from train WHERE origin_id=? and destination_id=?";
    private static final String SELECT_TRAIN_TO_ID = "SELECT * from train WHERE destination_id=?";
    private static final String SELECT_ALL_FROM_TRAIN = "SELECT * FROM train";
    private static final String SELECT_TICKET_BY_ID_AND_DATE = "select * from ticket where date=? and Train_ID=?";
    private static final String SELECT_TICKET_BY_USER_ID = "select Train_ID,place,carriage_number,price,seat_type,date,passenger_individual_ID from ticket where passenger_individual_ID=?";
    private static final String SELECT_BY_USERNAME_AND_PASSWORD_FROM_INDIVIDUAL = "select login,password from individual where login=? and password=?";
    private static final String INSERT_INTO_INDIVIDUAL = "Insert into individual(FName,LName,email,login,password) values (?,?,?,?,?)";
    private static final String UPDATE_INDIVIDUAL_LOGIN = "UPDATE individual set remember=? where login=?";
    private static final String COUNT_BY_REMEMBER_IN_INDIVIDUAL = "select ID from individual where remember=?";
    private static final String SELECT_FROM_TICKET = "select passenger_individual_ID from ticket where Train_ID=? and carriage_number=? and place=? and date=?";
    private static final String TURN_OFF_FOREIGN_KEY = "SET foreign_key_checks = ?";
    private static final String UPDATE_TICKET = "UPDATE ticket SET passenger_individual_ID=? where Train_ID=? and carriage_number=? and place=? and date=?";
    private static final String SELECT_STATION_BY_ID = "SELECT * from station where station_id=?";
    private static final String INSERT_TICKET = "INSERT INTO ticket(place,carriage_number,price,seat_type,date,Train_ID) values (?,?,?,?,?,?)";
    private static final String INDIVIDUAL_TYPE = "select type from individual where ID=?";
    private static final String DELETE_TICKET = "Delete from ticket where passenger_individual_ID=? and place=? and carriage_number=? and date=? and Train_ID=?";
    private static final String GET_USER_TYPE = "SELECT type FROM individual where ID=?";
    private static final String STATION_CITY = "city";
    private static final String STATION_NAME = "name";
    private static final String STATION_ID = "station_id";

    public ArrayList<Station> getAllStations() {
        Connection connection = null;
        ArrayList<Station> resultList = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            resultList = new ArrayList<>();
            preparedStatement = connection.prepareStatement(SELECT_CITY_NAME_ID_FROM_STATION);
            resultSet = preparedStatement.executeQuery();/*
            while (resultSet.next()) {
                resultList.add(new Station(resultSet.getString(STATION_CITY),
                        resultSet.getString(STATION_NAME), resultSet.getInt(STATION_ID)));
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
        return resultList;
    }

    @Override
    public boolean buyTicket(int user_id, int train_id, int wagon_number, int place, String date) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement checkerControl = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_FROM_TICKET);
            preparedStatement.setInt(1,train_id);
            preparedStatement.setInt(2,wagon_number);
            preparedStatement.setInt(3,place);
            preparedStatement.setString(4,date);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if(resultSet.getInt("passenger_individual_ID") != 0){
                return false;
            }else{
                checkerControl = connection.prepareStatement(TURN_OFF_FOREIGN_KEY);
                checkerControl.setInt(1, 0);
                checkerControl.executeUpdate();

                preparedStatement = connection.prepareStatement(UPDATE_TICKET);
                preparedStatement.setInt(1,user_id);
                preparedStatement.setInt(2,train_id);
                preparedStatement.setInt(3,wagon_number);
                preparedStatement.setInt(4,place);
                preparedStatement.setString(5,date);
                preparedStatement.executeUpdate();

                checkerControl = connection.prepareStatement(TURN_OFF_FOREIGN_KEY);
                checkerControl.setInt(1, 1);
                checkerControl.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(checkerControl);
            close(connection);
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public ArrayList<Ticket> getTicketsOfUser(int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ArrayList<Ticket> result = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            result = new ArrayList<>();
            preparedStatement = connection.prepareStatement(SELECT_TICKET_BY_USER_ID);
            preparedStatement.setInt(1,user_id);
            resultSet = preparedStatement.executeQuery();/*
            while(resultSet.next()){
                result.add(new Ticket(resultSet.getInt("Train_ID"), resultSet.getInt("place"),
                        resultSet.getInt("carriage_number"), resultSet.getDouble("price"),
                        resultSet.getString("seat_type"), resultSet.getString("date"), resultSet.getInt("passenger_individual_ID") == 0));
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
        return result;
    }

    @Override
    public boolean checkAgent(int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(INDIVIDUAL_TYPE);
            preparedStatement.setInt(1,user_id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("type")==1;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
        return false;
    }

    @Override
    public boolean createTicket(int place, int carriage_number, double price, String seat_type, String date, int train_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement checkerControl = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_TICKET);
            preparedStatement.setInt(1,place);
            preparedStatement.setInt(2,carriage_number);
            preparedStatement.setDouble(3,price);
            preparedStatement.setString(4,seat_type);
            preparedStatement.setString(5,date);
            preparedStatement.setInt(6,train_id);
            checkerControl = connection.prepareStatement(TURN_OFF_FOREIGN_KEY);
            checkerControl.setInt(1, 0);
            checkerControl.executeUpdate();
            preparedStatement.executeUpdate();
            checkerControl = connection.prepareStatement(TURN_OFF_FOREIGN_KEY);
            checkerControl.setInt(1, 1);
            checkerControl.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(checkerControl);
            close(connection);
        }
        return false;
    }

    @Override
    public boolean deleteTicket(int user_id, int place, int wagon_num, String date, int train_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(DELETE_TICKET);
            preparedStatement.setInt(1,user_id);
            preparedStatement.setInt(2,place);
            preparedStatement.setInt(3,wagon_num);
            preparedStatement.setString(4,date);
            preparedStatement.setInt(5,train_id);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
        return false;
    }


    @Override
    public int getTypeOfUser(int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_USER_TYPE);
            preparedStatement.setInt(1,user_id);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
        //mistake or no such user_id
        return -1;
    }

    @Override
    public int checkToken(String token) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(COUNT_BY_REMEMBER_IN_INDIVIDUAL);
            preparedStatement.setString(1, token);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    public void createTrain( int trainType, String weekdays, int trainId,List<Integer> listOfTrains, List<String> arriveTime, List<String> departureTime) {

    }

    @Override
    public void buyTicket(int trainID, int fromId, int toId, String date, int userID, String fname, String lname, String docType, String docId) {

    }

    @Override
    public void buyTicket(int trainID, int fromId, int toId, String date, int userID) {

    }

    @Override
    public ArrayList<Train> getAllTrains(char weekDay, int from_id, int to_id) {
        Connection connection = null;
        ArrayList<Train> resultList = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            resultList = new ArrayList<>();
            if (from_id == 0 && to_id != 0) {
                preparedStatement = connection.prepareStatement(SELECT_TRAIN_TO_ID);
                preparedStatement.setInt(1, to_id);
            } else if (from_id != 0 && to_id == 0) {
                preparedStatement = connection.prepareStatement(SELECT_TRAIN_FROM_ID);
                preparedStatement.setInt(1, from_id);
            } else if (from_id != 0) {
                preparedStatement = connection.prepareStatement(SELECT_TRAIN_FROM_ID_AND_TO_ID);
                preparedStatement.setInt(1, from_id);
                preparedStatement.setInt(2, to_id);
            } else {
                preparedStatement = connection.prepareStatement(SELECT_ALL_FROM_TRAIN);
            }
            resultSet = preparedStatement.executeQuery();/*
            while (resultSet.next()) {
                if (resultSet.getString("week_days").contains(String.valueOf(weekDay))) {
                    resultList.add(new Train(resultSet.getInt("ID"), resultSet.getString("arrival_time"),
                            resultSet.getString("departure_time"), resultSet.getInt("capacity_carriage"),
                            resultSet.getInt("number_carriage"), resultSet.getString("type_carriage"),
                            resultSet.getString("week_days"), resultSet.getInt("destination_id"),
                            resultSet.getInt("origin_id"), resultSet.getInt("arrival_day")));
                }
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
        return resultList;
    }

    @Override
    public ArrayList<Seat> getAllSeats(String date, int train_id, int fromOrder, int toOrder) {
        return null;
    }

    @Override
    public Station getStationById(int station_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Station result = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_STATION_BY_ID);
            preparedStatement.setInt(1,station_id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            //result = new Station(resultSet.getString("city"), resultSet.getString("name"), station_id);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
        return result;
    }

    @Override
    public ArrayList<Ticket> getAllTickets(String date, int train_id) {
        Connection connection = null;
        ArrayList<Ticket> resultList = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            resultList = new ArrayList<>();
            preparedStatement = connection.prepareStatement(SELECT_TICKET_BY_ID_AND_DATE);
            preparedStatement.setString(1, date);
            preparedStatement.setInt(2,train_id);
            resultSet = preparedStatement.executeQuery();/*
            while (resultSet.next()) {
                resultList.add(new Ticket(resultSet.getInt("Train_ID"), resultSet.getInt("place"),
                        resultSet.getInt("carriage_number"), resultSet.getDouble("price"),
                        resultSet.getString("seat_type"), resultSet.getString("date"), resultSet.getInt("passenger_individual_ID") == 0));
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
        return resultList;
    }

    @Override
    public boolean authenticated(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_BY_USERNAME_AND_PASSWORD_FROM_INDIVIDUAL);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
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
