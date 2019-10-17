import railway_system.connection.ConnectionPool;
import railway_system.connection.ConnectionPoolException;
import railway_system.dao.BaseDaoImpl;
import railway_system.entity.Station;
import railway_system.entity.Train;

import java.sql.Connection;
import java.util.List;

public class main {
    public static void main(String[] args){

        System.out.println(new BaseDaoImpl().checkToken("token2"));
    }
}
