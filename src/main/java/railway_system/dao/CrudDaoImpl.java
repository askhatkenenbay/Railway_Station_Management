package railway_system.dao;

import railway_system.connection.ConnectionPool;
import railway_system.entity.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CrudDaoImpl implements CrudDao {
    private static final String CREATE_TRAIN = "INSERT INTO train values(?,?,?,?)";
    private static final String CREATE_TRAIN_TYPE = "INSERT INTO train_type values (?,?,?,?,?)";
    private static final String CREATE_STATION = "INSERT INTO station values(?,?,?,?)";
    private static final String CREATE_TRAIN_LEG = "INSERT INTO train_leg values(?,?,?,?,?)";
    private static final String CREATE_SEAT = "INSERT INTO seats values(?,?,?,?)";
    private static final String CREATE_INDIVIDUAL = "INSERT INTO individual VALUES(?,?,?,?,?,?,?,?,?,?)";
    private static final String CREATE_EMPLOYEE = "INSERT INTO employee VALUES(?,?,?,?,?,?,?)";
    private static final String SELECT_WAGON_AMOUNT_CAPACITY = "select wagon_amount,wagon_capacity from train_type,train where train.id=? and train.train_type_id=train_type.id;";
    private static final String SELECT_TRAINID_AND_ORDER_BY_TRAINID = "select train_id, `order` from train_leg where train_id=?";
    @Override
    public void createTrainType(TrainType trainType) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_TRAIN_TYPE);
            preparedStatement.setInt(1,trainType.getId());
            preparedStatement.setString(2,trainType.getName());
            preparedStatement.setInt(3,trainType.getWagonAmount());
            preparedStatement.setInt(4,trainType.getWagonCapacity());
            preparedStatement.setInt(5,trainType.getPrice());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Cannot create new train type");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public void createStation(Station station) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_STATION);
            preparedStatement.setInt(1,station.getId());
            preparedStatement.setString(2,station.getName());
            preparedStatement.setString(3,station.getCity());
            preparedStatement.setString(4,station.getState());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("cannot create new station");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public void createTrain(Train train) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_TRAIN);
            preparedStatement.setInt(1,train.getId());
            preparedStatement.setString(2,train.getCompanyName());
            preparedStatement.setString(3,train.getWeekDays());
            preparedStatement.setInt(4,train.getTrainTypeId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("cannot create new train");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public void createTrainLeg(int trainId,List<Integer> listOfStationId, List<String> arriveTime, List<String> departureTime) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_TRAIN_LEG);
            preparedStatement.setInt(5,trainId);
            for (int i = 0; i < listOfStationId.size(); i++) {
                preparedStatement.setString(1,arriveTime.get(i));
                preparedStatement.setString(2,departureTime.get(i));
                preparedStatement.setInt(3,i);
                preparedStatement.setInt(4,listOfStationId.get(i));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("cannot create new train leg");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public  void createSeats(int traindId){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement statement = null;
        PreparedStatement tempStatement = null;
        ResultSet resultSet = null;
        ResultSet tempResultSet = null;
        int wagonAmount;
        int wagonCapacity;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            statement = connection.prepareStatement(SELECT_WAGON_AMOUNT_CAPACITY);
            statement.setInt(1,traindId);
            resultSet = statement.executeQuery();
            resultSet.next();
            wagonAmount = resultSet.getInt("wagon_amount");
            wagonCapacity = resultSet.getInt("wagon_capacity");
            tempStatement = connection.prepareStatement(SELECT_TRAINID_AND_ORDER_BY_TRAINID);
            tempStatement.setInt(1,traindId);
            tempResultSet = tempStatement.executeQuery();
            preparedStatement = connection.prepareStatement(CREATE_SEAT);
            while(tempResultSet.next()){
                for(int i=0;i<wagonAmount;i++){
                    for (int j = 0; j < wagonCapacity; j++) {
                        preparedStatement.setInt(1,j+1);
                        preparedStatement.setInt(2,i+1);
                        preparedStatement.setInt(3,tempResultSet.getInt("order"));
                        preparedStatement.setInt(4,tempResultSet.getInt("train_id"));
                        preparedStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (tempResultSet != null) {
                    tempResultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(tempStatement);
            close(statement);
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public void createIndividual(Individual individual) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_INDIVIDUAL);
            preparedStatement.setInt(1,individual.getId());
            preparedStatement.setString(2,individual.getFirstName());
            preparedStatement.setString(3,individual.getSecondName());
            preparedStatement.setString(4,individual.getEmail());
            preparedStatement.setString(5,individual.getLogin());
            preparedStatement.setString(6,individual.getPassword());
            preparedStatement.setString(7,individual.getActivation());
            preparedStatement.setString(8,individual.getRemember());
            preparedStatement.setString(9,individual.getReset());
            preparedStatement.setInt(10,individual.getActivated());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Cannot create new individual");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }


    @Override
    public void createEmployee(Employee employee) throws DaoException {
       Connection connection = null;
       PreparedStatement preparedStatement = null;
       try{
           connection = ConnectionPool.INSTANCE.getConnection();
           preparedStatement = connection.prepareStatement(CREATE_EMPLOYEE);
           preparedStatement.setInt(1,employee.getPaymentForHour());
           preparedStatement.setString(2,employee.getType());
           preparedStatement.setString(3,employee.getWorkStartTime());
           preparedStatement.setString(4,employee.getWorkEndTime());
           preparedStatement.setString(5,employee.getWorkDays());
           preparedStatement.setInt(6,employee.getId());
           preparedStatement.setInt(7,employee.getIndividualId());
           preparedStatement.executeUpdate();
       } catch (SQLException e) {
           e.printStackTrace();
           throw new DaoException("Cannot create new employee");
       } finally {
           close(preparedStatement);
           close(connection);
       }
    }


    @Override
    public void createTicket(int trainID, int fromId, int toId, String date, int userID, String fname, String lname, String docType, String docId) {

    }

}
