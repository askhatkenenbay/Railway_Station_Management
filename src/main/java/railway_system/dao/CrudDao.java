package railway_system.dao;

import railway_system.connection.ConnectionPool;
import railway_system.connection.ConnectionPoolException;
import railway_system.entity.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public interface CrudDao {

    void createTrainType(TrainType trainType) throws DaoException;
    void createStation(Station station) throws DaoException;
    //create Train from list
    void createTrain(Train train) throws DaoException;
    void createTrainLeg(int trainId,List<Integer> listOfStationId, List<String> arriveTime, List<String> departureTime)  throws DaoException ;
    void createSeats(int traindId);
    void createIndividual(Individual individual) throws DaoException;
    void createEmployee(Employee employee) throws DaoException;
    void setToken(String username, String token);
    ArrayList<Station> getAllStations();
    //buy ticket for someone
    //return ticket id
    int createTicket(Ticket ticket);
    void createPaycheck(int employeeId, String date);

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
