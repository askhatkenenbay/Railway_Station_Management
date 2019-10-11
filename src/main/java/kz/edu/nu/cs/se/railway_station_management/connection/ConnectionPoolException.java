package kz.edu.nu.cs.se.railway_station_management.connection;

public class ConnectionPoolException extends Exception {
    public ConnectionPoolException() {
        System.out.println("Problems with Connection Pool");
    }

    public ConnectionPoolException(String message) {
        super(message);
    }

    public ConnectionPoolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionPoolException(Throwable cause) {
        super(cause);
    }
}
