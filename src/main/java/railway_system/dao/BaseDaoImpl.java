package railway_system.dao;

import railway_system.connection.ConnectionPool;
import railway_system.entity.Station;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BaseDaoImpl implements BaseDao {
    private static final String SELECT_CITY_NAME_ID_FROM_STATION = "SELECT city,name,station_id from station";
    private static final String STATION_CITY = "city";
    private static final String STATION_NAME = "name";
    private static final String STATION_ID= "station_id";
    public ArrayList<Station> getAllStations(){
        Connection connection = null;
        ArrayList<Station> resultList = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            resultList = new ArrayList<>();
            preparedStatement = connection.prepareStatement(SELECT_CITY_NAME_ID_FROM_STATION);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                resultList.add(new Station(resultSet.getString(STATION_CITY),
                        resultSet.getString(STATION_NAME),resultSet.getInt(STATION_ID)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
       return resultList;
    }
}
