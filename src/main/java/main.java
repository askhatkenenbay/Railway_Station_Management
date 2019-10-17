import railway_system.connection.ConnectionPool;
import railway_system.connection.ConnectionPoolException;
import railway_system.dao.BaseDaoImpl;
import railway_system.entity.Station;
import railway_system.entity.Train;

import java.sql.Connection;
import java.util.List;

public class main {
    public static void main(String[] args){
        List<Train> asd = new BaseDaoImpl().getAllTrains('S',0,5);

        for(Train curr : asd){
            System.out.println(curr);
        }
    }
}
