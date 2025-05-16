package ticktocktrack.database;

import javafx.stage.Stage;
import ticktocktrack.gui.HomePage;

public class Session {
    private static UsersModel currentUser;

    public static void setCurrentUser(UsersModel user) {
        currentUser = user;
    }

    public static UsersModel getCurrentUser() {
        return currentUser;
    }

    public static void resetSession() {
        currentUser = null;
    }

    public static void logoutAndGoHome(Stage currentStage) {
        // Reset the current session
        Session.resetSession();

        // Print confirmation message
        System.out.println("Successfully logged out.");

        // Close the current window
        currentStage.close();

        // Launch the login screen (HomePage)
        HomePage homePage = new HomePage();
        Stage homeStage = new Stage();
        homePage.start(homeStage);
    }
}
