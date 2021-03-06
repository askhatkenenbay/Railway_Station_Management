package railway_system.dao;

import railway_system.entity.*;

import java.util.List;

public interface CrudDao extends CloseDao {

    void createTrainType(TrainType trainType) throws DaoException;

    void createStation(Station station) throws DaoException;

    //create Train from list
    void createTrainLeg(TrainLeg trainLeg) throws DaoException;

    void createSeats(int traindId);

    void createIndividual(Individual individual) throws DaoException;

    //void createEmployee(Employee employee) throws DaoException;
    //buy ticket for someone
    //return ticket id
    int createTicket(Ticket ticket) throws DaoException;

    //create a paycheck for employeeId paycheck.amount = employee.salary
    boolean createPaycheck(int employeeId, String date);

    //return train id
    int createTrain(String companyName, int typeId);

    //create weekday for a new train line
    boolean createWeekdays(int trainId, int weekId);

    void createSeatInstances(int trainId, String date) throws DaoException;

    //get Station by ID
    public Station getStation(int id);

    void setToken(String username, String token);

    boolean updateTicketRefund(int ticketId, int waiting_refund);

    List<Station> readStations();

    TrainType readTrainType(int trainId);

    //Return Tickets of user
    List<Ticket> readTicketsOfUser(int individual_id);

    //read all tickets where waiting_refund = 1;
    List<Ticket> readWaitingTickets();

    //Return all employees including fname and lname
    List<Employee> readAllEmployees();

    boolean updateTrainActivity(int trainId, int activity);
    //update all SeatInstances where Seat.ticket_id = ticket_id to ticket_id = NULL

    //delete ticket
    void deleteTicket(int ticket_id) throws DaoException;


}
