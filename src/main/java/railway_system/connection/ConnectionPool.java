package railway_system.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public enum ConnectionPool {
    INSTANCE;
    private BlockingQueue<ProxyConnection> connectionQueue;
    private BlockingQueue<ProxyConnection> givenAwayConQueue;

    private String driver;
    private String url;
    private String user;
    private String password;
    private int poolSize;

    private static final String DB_DRIVER = "database.driver";
    private static final String DB_URL = "database.url";
    private static final String DB_USER = "database.user";
    private static final String DB_PASSWORD = "database.password";
    private static final String DB_POOL_SIZE = "database.poolsize";
    private static final int DEFAULT_DB_POOL_SIZE = 10;

    /**
     * Connection pool as enum. Since enums constructor is thread-safe,
     * connection pool is created in enums constructor
     */
    ConnectionPool() {
        //register driver
        DBResourceManager instance = DBResourceManager.getInstance();
        driver = instance.getString(DB_DRIVER);
        url = instance.getString(DB_URL);
        user = instance.getString(DB_USER);
        password = instance.getString(DB_PASSWORD);
        try {
            poolSize = Integer.parseInt(instance.getString(DB_POOL_SIZE));
        } catch (NumberFormatException e) {
            poolSize = DEFAULT_DB_POOL_SIZE;
        }
        //init pools param
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connectionQueue = new LinkedBlockingDeque<>(poolSize);
        givenAwayConQueue = new LinkedBlockingDeque<>(poolSize);
        //init connections
        Properties prop = getProperties();
        for (int i = 0; i < poolSize; i++) {
            try {
                Connection connection = DriverManager.getConnection(url, prop);
                connection.setAutoCommit(true);
                ProxyConnection toAdd = new ProxyConnection(connection);
                connectionQueue.add(toAdd);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is used to set up properties file used to connect with database
     *
     * @return properties file
     */
    private Properties getProperties() {
        Properties prop = new Properties();
        prop.put("user", user);
        prop.put("password", password);
        prop.put("autoReconnect", "true");
        prop.put("characterEncoding", "UTF-8");
        prop.put("useUnicode", "true");
        prop.put("useSSL", "true");
        prop.put("useJDBCCompliantTimezoneShift", "true");
        prop.put("useLegacyDatetimeCode", "false");
        prop.put("serverTimezone", "UTC");
        prop.put("serverSslCert", "classpath:server.crt");
        return prop;
    }

    /**
     * This method is used to get connection from ConnectionPoll
     *
     * @return Connection from ConnectionPool
     */
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = connectionQueue.remove();
            givenAwayConQueue.add((ProxyConnection) connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * This method is used to return connection back to ConnectionPool.
     * It checks whether input connection was taken from this ConnectionPool;
     *
     * @param connection Connection to be returned to ConnectionPool
     */
    public void releaseConnection(Connection connection) throws ConnectionPoolException {
        if (!(connection instanceof ProxyConnection)) {
            throw new ConnectionPoolException("Connection was not taken from ConnectionPool");
        }
        givenAwayConQueue.remove(connection);
        connectionQueue.add((ProxyConnection) connection);
    }

    /**
     * Called when server is shut down to destroy ConnectionPooL
     */
    public void destroyPool() {
        for (int i = 0; i < poolSize; i++) {
            try {
                connectionQueue.take().reallyClose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        deregisterDriver();
    }

    /**
     * Called to deregister all drivers
     */
    private void deregisterDriver() {
        DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
