package kz.edu.nu.cs.se.railway_station_management.dao;

public class DaoException extends Exception{
    public DaoException() {
        System.out.println("Problems with Dao");
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }
}
