package railway_system.dao;

import javafx.util.Pair;
import railway_system.connection.ConnectionPool;
import railway_system.entity.Seat;
import railway_system.entity.Train;
import railway_system.entity.TrainLeg;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


public class MainDaoImpl implements MainDao {
    private static final String GET_TRAINS_BY_WEEKDAYS = "SELECT DISTINCT T1.arrival_time as T1arrival_time, T1.departure_time as T1departure_time,\n" +
            "T1.order as T1order, T1.station_id as T1station_id, T1.train_id as T1train_id, T1.arrival_day as T1arrival_day,\n" +
            "T2.arrival_time, T2.departure_time, T2.order, T2.station_id, T2.train_id, T2.arrival_day\n" +
            "from train_leg T1, train_leg T2, train T, week_day W1\n" +
            "where T1.train_id = T2.train_id and T.id = T1.train_id and T.is_active = 1 and \n" +
            "W1.train_id = T1.train_id and W1.weekId = ? and T1.`order` < T2.`order` and T1.order =\n " +
            "(SELECT Min(`order`) as Min from train_leg Q where Q.train_id=T1.train_id) \n" +
            "and T2.order = \n" +
            "(SELECT Max(`order`) as Min from train_leg Q where Q.train_id=T2.train_id)";

    private static final String GET_TRAINS_BY_WEEKDAYS_TO_ID = "SELECT DISTINCT T1.arrival_time as T1arrival_time, T1.departure_time as T1departure_time,\n" +
            "T1.order as T1order, T1.station_id as T1station_id, T1.train_id as T1train_id, T1.arrival_day as T1arrival_day,\n" +
            "T2.arrival_time, T2.departure_time, T2.order, T2.station_id, T2.train_id, T2.arrival_day\n" +
            "from train_leg T1, train_leg T2, train T, week_day W1\n" +
            "where T1.train_id = T2.train_id and T.id = T1.train_id and T.is_active = 1 and T2.station_id = ? and\n" +
            "W1.train_id = T1.train_id and T1.`order` < T2.`order` and W1.weekId = ? and T1.order = (SELECT Min(`order`) as Min from train_leg Q where Q.train_id=T1.train_id )";

    private static final String GET_TRAINS_BY_WEEKDAYS_FROM_ID = "SELECT DISTINCT T1.arrival_time as T1arrival_time, T1.departure_time as T1departure_time,\n" +
            "T1.order as T1order, T1.station_id as T1station_id, T1.train_id as T1train_id, T1.arrival_day as T1arrival_day,\n" +
            "T2.arrival_time, T2.departure_time, T2.order, T2.station_id, T2.train_id, T2.arrival_day\n" +
            " from train_leg T1, week_day W1, train_leg T2, train T where T1.train_id=T2.train_id and\n" +
            "T1.train_id = W1.train_id and T.id = T1.train_id and T.is_active = 1 and MOD(W1.weekId+T1.arrival_day,6) = ? \n" +
            "and T1.station_id = ? \n" +
            "and T1.`order` < T2.`order` and T2.order = (SELECT Max(`order`) as Max from train_leg Q where Q.train_id=T1.train_id );";

    private static final String GET_TRAINS_BY_WEEKDAYS_FROM_ID_TO_ID = "select DISTINCT T1.arrival_time as T1arrival_time, T1.departure_time as T1departure_time,\n" +
            "T1.order as T1order, T1.station_id as T1station_id, T1.train_id as T1train_id, T1.arrival_day as T1arrival_day,\n" +
            "T2.arrival_time, T2.departure_time, T2.order, T2.station_id, T2.train_id, T2.arrival_day\n" +
            "from train_leg T1, train_leg T2, train T, week_day W1 where T1.train_id = T2.train_id\n" +
            "and W1.train_id = T1.train_id and T.id = T1.train_id and T.is_active = 1 and T1.station_id = ? and T2.station_id = ? and\n" +
            "T1.`order` < T2.`order` and W1.weekId = ?";

    private static final String AUTHENTICATE_INDIVIDUAL = "select * from individual where login=? and password=?";
    private static final String IS_MANAGER = "select * from employee where individual_id=? and type='manager'";
    private static final String IS_BELONG_TO = "select * from ticket where id=? and individual_id=?";
    private static final String IS_TRAIN_ACTIVE = "select is_active from train where is_active=1 and id=?";
    private static final String IS_AGENT = "select * from employee where type='agent' and individual_id=?";
    private static final String GET_SEAT = "select * from seat_instance where\n" +
            "date = ?  and seat_number=? and\n" +
            "wagon_number = ? and train_leg_train_id =?\n" +
            "and train_leg_order>=? and train_leg_order<=? and ticket_id IS NOT NULL";

    private static final String UPDATE_TICKET = "INSERT INTO seat_instance values(?,?,?,?,?,?)";
    private static final String GET_ALL_AGENTS_EMAIL = "select email from employee,individual  where employee.type='agent' \n" +
            "and employee.individual_id=individual.id";
    private static final String GET_ALL_TRAINS = "select * from train";
    private static final String GET_EMAILS_FROM_TRAIN = "select individual.email from ticket,individual where ticket.departure_datetime > ? and ticket.train_id=?\n" +
            "and ticket.individual_id = individual.id";
    private static final String REFUND_SEAT_INSTANCE = "UPDATE ticket SET id=null WHERE id=?";
    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrainsFromTo(int weekDay, int from_id, int to_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_TRAINS_BY_WEEKDAYS_FROM_ID_TO_ID);
            preparedStatement.setInt(1, from_id);
            preparedStatement.setInt(2, to_id);
            preparedStatement.setInt(3, weekDay);
            resultSet = preparedStatement.executeQuery();
            return helperTrain(resultSet);
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
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrainsFrom(int weekDay, int from_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_TRAINS_BY_WEEKDAYS_FROM_ID);
            preparedStatement.setInt(1, weekDay);
            preparedStatement.setInt(2, from_id);
            resultSet = preparedStatement.executeQuery();
            return helperTrain(resultSet);
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
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrainsTo(int weekDay, int to_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_TRAINS_BY_WEEKDAYS_TO_ID);
            preparedStatement.setInt(1, to_id);
            preparedStatement.setInt(2, weekDay);
            resultSet = preparedStatement.executeQuery();
            return helperTrain(resultSet);
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
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrains(int weekDay) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_TRAINS_BY_WEEKDAYS);
            preparedStatement.setInt(1, weekDay);
            resultSet = preparedStatement.executeQuery();
            return helperTrain(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return new ArrayList<>();
    }

    private ArrayList<Pair<TrainLeg, TrainLeg>> helperTrain(ResultSet resultSet) throws SQLException {
        ArrayList<Pair<TrainLeg, TrainLeg>> result = new ArrayList<>();
        while (resultSet.next()) {
            Pair<TrainLeg, TrainLeg> toAdd = new Pair<>(new TrainLeg(resultSet.getInt("T1train_id"), resultSet.getInt("T1order"),
                    resultSet.getInt("T1station_id"), resultSet.getString("T1arrival_time"),
                    resultSet.getString("T1departure_time"), resultSet.getInt("T1arrival_day")),
                    new TrainLeg(resultSet.getInt("train_id"), resultSet.getInt("order"),
                            resultSet.getInt("station_id"), resultSet.getString("arrival_time"),
                            resultSet.getString("departure_time"), resultSet.getInt("arrival_day")));
            result.add(toAdd);
        }
        return result;
    }


    @Override
    public Seat getSeat(int wagon_num, int seat_num, String date, int train_id, int fromOrder, int toOrder) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_SEAT);
            preparedStatement.setString(1,date);
            preparedStatement.setInt(2,seat_num);
            preparedStatement.setInt(3,wagon_num);
            preparedStatement.setInt(4,train_id);
            preparedStatement.setInt(5,fromOrder);
            preparedStatement.setInt(6,toOrder);
            resultSet = preparedStatement.executeQuery();
            boolean available = true;
            if(resultSet.next()){
                available = false;
            }
                return new Seat(date,seat_num,wagon_num,train_id,available);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return new Seat(date,seat_num,wagon_num,train_id,false);
    }



    @Override
    public void updateSeatInstances(String date, int seat_num, int wagon_num, int from_order, int to_order, int train_id, int ticket_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_TICKET);
            preparedStatement.setString(1,date);
            preparedStatement.setInt(2,seat_num);
            preparedStatement.setInt(3,wagon_num);
            preparedStatement.setInt(5,train_id);
            preparedStatement.setInt(6,ticket_id);
            for (int i = from_order; i <=to_order; i++) {
                preparedStatement.setInt(4,i);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public void refundSeatInstances(int ticket_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(REFUND_SEAT_INSTANCE);
            preparedStatement.setInt(1,ticket_id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public boolean authenticated(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(AUTHENTICATE_INDIVIDUAL);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return false;
    }

    @Override
    public boolean checkIsActiveTrain(int train_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(IS_TRAIN_ACTIVE);
            preparedStatement.setInt(1, train_id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getBoolean("is_active");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return false;
    }

    @Override
    public boolean checkManager(int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(IS_MANAGER);
            preparedStatement.setInt(1, user_id);
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
    public boolean checkAgent(int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(IS_AGENT);
            preparedStatement.setInt(1,user_id);
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return false;
    }

    @Override
    public boolean isBelongTo(int user_id, int ticket_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(IS_BELONG_TO);
            preparedStatement.setInt(1, ticket_id);
            preparedStatement.setInt(2, user_id);
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return false;
    }

    @Override
    public List<String> getAgentsEmails() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<String> emailList = new LinkedList<>();
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_ALL_AGENTS_EMAIL);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                emailList.add(resultSet.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return emailList;
    }

    @Override
    public List<String> getPassengersEmails(int train_id, String date) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<String> emailList = new LinkedList<>();
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_EMAILS_FROM_TRAIN);
            preparedStatement.setString(1,date);
            preparedStatement.setInt(2,train_id);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                emailList.add(resultSet.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return emailList;
    }

    @Override
    public List<Train> getTrains() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(GET_ALL_TRAINS);
            resultSet = preparedStatement.executeQuery();
            return getTrainsHelper(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return new LinkedList<>();
    }
    private List<Train> getTrainsHelper(ResultSet resultSet) throws SQLException {
        List<Train> trainList = new LinkedList<>();
        while (resultSet.next()){
            trainList.add(new Train(resultSet.getInt("id"),resultSet.getString("company_name"),
                    resultSet.getInt("train_type_id"),resultSet.getBoolean("is_active")));
        }
        return trainList;
    }

}
