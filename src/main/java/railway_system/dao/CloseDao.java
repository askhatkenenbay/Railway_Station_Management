package railway_system.dao;

import railway_system.connection.ConnectionPool;
import railway_system.connection.ConnectionPoolException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface CloseDao {
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
