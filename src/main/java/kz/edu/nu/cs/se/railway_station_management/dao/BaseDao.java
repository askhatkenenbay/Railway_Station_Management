package kz.edu.nu.cs.se.railway_station_management.dao;

import kz.edu.nu.cs.se.railway_station_management.connection.ConnectionPool;
import kz.edu.nu.cs.se.railway_station_management.connection.ConnectionPoolException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface BaseDao {
    //write method signatures here to access

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
