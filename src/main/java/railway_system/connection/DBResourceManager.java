package railway_system.connection;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DBResourceManager {
    private static DBResourceManager instance = new DBResourceManager();
    private ResourceBundle resourceBundle = null;
    private static final String DATABASE_PROPERTIES_FILE_NAME = "database";
    /**
     * @return its private static field DBResourceManager instance
     */
    public static DBResourceManager getInstance() {
        return instance;
    }
    /**
     * @param key parameter of database
     * @return values of given key
     */
    public String getString(String key) {
        if (key != null && key.length() != 0) {
            try {
                return resourceBundle.getString(key);
            } catch (MissingResourceException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * called while instantiating private field DBResourceManager instance
     * instantiate private field resourceBundle from file database_name.properties
     */
    private DBResourceManager() {
        try {
            resourceBundle = ResourceBundle.getBundle(DATABASE_PROPERTIES_FILE_NAME);
        } catch (ExceptionInInitializerError e) {
            e.printStackTrace();
        }
    }
}
