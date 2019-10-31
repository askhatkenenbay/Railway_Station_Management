package railway_system.dao;


import railway_system.connection.ConnectionPool;
import railway_system.connection.ConnectionPoolException;
import railway_system.entity.Ticket;
import railway_system.entity.Station;
import railway_system.entity.Train;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public interface BaseDao {
    //write method signatures here to access
    int getTypeOfUser(int user_id);

    int checkToken(String token);//must return user_id, else = -1
    void setToken(String username, String token);

    ArrayList<Train> getAllTrains(char weekDay, int from_id, int to_id);

    Station getStationById(int station_id);

    //return all tickets for a given date and a given train
    ArrayList<Ticket> getAllTickets(String date, int train_id);

    boolean authenticated(String username, String password);

    boolean registerIndividual(String fname, String lname, String email, String login, String password);

    ArrayList<Station> getAllStations();

    boolean buyTicket(int user_id, int train_id, int wagon_number, int place, String date);

    ArrayList<Ticket> getTicketsOfUser(int user_id);

    boolean checkAgent(int user_id);

    boolean createTicket(int place, int carriage_number, double price, String seat_type, String date, int train_id);

    boolean deleteTicket(int place, int wagon_num, String date, int train_id);

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
