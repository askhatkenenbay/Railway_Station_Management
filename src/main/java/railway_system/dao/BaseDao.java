package railway_system.dao;


import railway_system.connection.ConnectionPool;
import railway_system.connection.ConnectionPoolException;
import railway_system.entity.Ticket;
import railway_system.entity.Train;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public interface BaseDao {
    //write method signatures here to access
    //return all train for given day and given stations
    public ArrayList<Train> getAllTrains(char weekDay, int from_id, int to_id);

    //return all tickets for a given date and a given train
    public ArrayList<Ticket> getAllTickets(String date, int train_id);

    public boolean authenticated(String username, String password);


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
