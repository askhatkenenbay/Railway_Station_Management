import railway_system.connection.ConnectionPool;
import railway_system.connection.ConnectionPoolException;

import java.sql.Connection;

public class main {
    public static void main(String[] args){
        Connection connection = ConnectionPool.INSTANCE.getConnection();
        if(connection!=null){
            System.out.println("asd");
        }
        try {
            ConnectionPool.INSTANCE.releaseConnection(connection);
        } catch (ConnectionPoolException e) {
            e.printStackTrace();
        }
    }
}
