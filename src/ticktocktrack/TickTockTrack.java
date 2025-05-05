package ticktocktrack;

import ticktocktrack.gui.HomePage;
import ticktocktrack.database.DatabaseConnection;
import javafx.stage.Stage;

public class TickTockTrack {

    // We will make a static DatabaseConnection instance accessible to other classes if needed
    private static DatabaseConnection dbConnection;

    public static void main(String[] args) {
        // Initialize database connection
        dbConnection = new DatabaseConnection();
       
        
        // Now launch the GUI
        HomePage.main(args);
    }

    public static void setPrimaryStage(Stage stage) {
    }

    // Optional: expose DatabaseConnection to other classes
    public static DatabaseConnection getDbConnection() {
        return dbConnection;
    }
}
