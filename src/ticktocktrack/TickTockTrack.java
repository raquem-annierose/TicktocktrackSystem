package ticktocktrack;

import ticktocktrack.gui.HomePage;
import ticktocktrack.database.DatabaseConnection;
import javafx.stage.Stage;

/**
 * The main entry point for the TickTockTrack application.
 * <p>
 * This class initializes the database connection and launches the graphical user interface (GUI).
 * It also provides a static method to access the database connection across the application.
 * </p>
 */
public class TickTockTrack {

    /**
     * A static instance of the {@link DatabaseConnection} used for managing the application's database operations.
     */
    private static DatabaseConnection dbConnection;

    /**
     * The main method that starts the TickTockTrack application.
     * <p>
     * This method initializes the database connection and launches the GUI by calling the {@link HomePage#main(String[])} method.
     * </p>
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        // Initialize database connection
        dbConnection = new DatabaseConnection();
       
        // Now launch the GUI
        HomePage.main(args);
    }

    /**
     * Sets the primary stage of the JavaFX application.
     * <p>
     * This method is currently a placeholder and does not contain any logic. 
     * It can be implemented in the future if stage manipulation is required.
     * </p>
     *
     * @param stage the primary stage of the application
     */
    public static void setPrimaryStage(Stage stage) {
        // Placeholder for setting the primary stage
    }

    /**
     * Provides access to the database connection instance.
     * <p>
     * This method is used to expose the {@link DatabaseConnection} instance 
     * to other classes that require database operations.
     * </p>
     *
     * @return the static {@link DatabaseConnection} instance
     */
    public static DatabaseConnection getDbConnection() {
        return dbConnection;
    }
}
