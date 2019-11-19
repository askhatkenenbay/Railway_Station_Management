package railway_system.dao;

import railway_system.connection.ConnectionPool;
import railway_system.connection.ConnectionPoolException;
import railway_system.entity.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public interface CrudDao extends CloseDao{

    void createTrainType(TrainType trainType) throws DaoException;
    void createStation(Station station) throws DaoException;
    //create Train from list
    void createTrainLeg(int trainId,List<Integer> listOfStationId, List<String> arriveTime, List<String> departureTime)  throws DaoException ;
    void createSeats(int traindId);
    void createIndividual(Individual individual) throws DaoException;
    void createEmployee(Employee employee) throws DaoException;
    //buy ticket for someone
    //return ticket id
    int createTicket(Ticket ticket);
    //create a paycheck for employeeId paycheck.amount = employee.salary
    boolean createPaycheck(int employeeId, String date);
    //return train id
    int createTrain(String companyName, int typeId);
    //create weekday for a new train line
    boolean createWeekdays(int trainId, int weekId);
    //get Station by ID
    public Station getStation(int id);

    void setToken(String username, String token);
    List<Station> readStations();
    boolean updateTrainActivity(int trainId, int activity);


}
