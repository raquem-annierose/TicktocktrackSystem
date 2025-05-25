package ticktocktrack.logic;

import javafx.stage.Stage;
import ticktocktrack.gui.HomePage;

/**
 * Manages the current user session throughout the application lifecycle.
 */
public class Session {
    private static UsersModel currentUser;

    /**
     * Sets the current logged-in user.
     * @param user The user to set as current session user.
     */
    public static void setCurrentUser(UsersModel user) {
        currentUser = user;
    }

    /**
     * Gets the current logged-in user.
     * @return The current user, or null if no user is logged in.
     */
    public static UsersModel getCurrentUser() {
        return currentUser;
    }

    /**
     * Resets the current session by clearing the current user.
     */
    public static void resetSession() {
        currentUser = null;
    }

    /**
     * Logs out the current user, closes the current window,
     * and opens the HomePage login screen.
     * 
     * @param currentStage The current JavaFX Stage (window) to be closed.
     */
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

    /**
     * Gets the user ID of the current logged-in user for use as a sender ID.
     * Returns -1 if no user is logged in.
     * 
     * @return The user ID of the current user or -1 if no user is logged in.
     */
    public static int getSenderUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }
}
