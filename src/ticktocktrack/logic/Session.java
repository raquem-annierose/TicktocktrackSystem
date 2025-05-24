package ticktocktrack.logic;

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
        if (currentStage != null) {
            currentStage.close();
        }

        // Launch the login screen (HomePage)
        try {
            HomePage homePage = new HomePage();
            Stage homeStage = new Stage();
            homePage.start(homeStage);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to open HomePage after logout.");
        }
    }

    
    public static int getSenderUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }

}

