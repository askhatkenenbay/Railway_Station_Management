import railway_system.connection.ConnectionPool;
import railway_system.connection.ConnectionPoolException;
import railway_system.dao.BaseDaoImpl;
import railway_system.entity.Station;

import java.sql.Connection;
import java.util.List;

public class main {
    public static void main(String[] args){
        List<Station> asd = new BaseDaoImpl().getAllStations();
        for(Station curr : asd){
            System.out.println(curr);
        }
    }
}
