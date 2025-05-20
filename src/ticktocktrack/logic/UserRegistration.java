package ticktocktrack.logic;

import ticktocktrack.database.DatabaseRegistrationManager;

import java.sql.Connection;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class UserRegistration {

	public static boolean registerFaculty(String username, String email, String role, 
            String password, String confirmPassword,
            String firstName, String lastName) {

    String passwordHash = DatabaseRegistrationManager.hashPassword(password);
    if (passwordHash == null) {
        return false;
    }

    boolean isRegistered = false;
    if ("Admin".equalsIgnoreCase(role) || "Teacher".equalsIgnoreCase(role)) {
        try (Connection connection = DatabaseRegistrationManager.getConnection()) {
            int currentAdminId = DatabaseRegistrationManager.getCurrentAdminId(connection);
            isRegistered = DatabaseRegistrationManager.registerFaculty(username, role, email, passwordHash,
                    firstName, lastName, currentAdminId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    return isRegistered;
}

	
    public static void registerStudent(String username, String email, String password, String confirmPassword,
                                       String firstName, String middleName, String lastName,
                                       String yearLevel, String program, String section) {
        if (!password.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Error", "Passwords do not match!");
            return;
        }

        String passwordHash = DatabaseRegistrationManager.hashPassword(password);
        if (passwordHash == null) {
            showAlert(AlertType.ERROR, "Error", "Error hashing the password.");
            return;
        }

        boolean isRegistered = DatabaseRegistrationManager.registerUser(
        	    username, "Student", email, passwordHash,
        	    firstName, middleName, lastName,
        	    yearLevel, program, section 
        	);


        if (isRegistered) {
            showAlert(AlertType.INFORMATION, "Registration Successful", "Student successfully registered!");
        } else {
            showAlert(AlertType.ERROR, "Registration Failed", "Registration failed. Please try again.");
        }
    }	

    private static void showAlert(AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Center the alert BEFORE showing
            double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();

            alert.getDialogPane().applyCss();
            alert.getDialogPane().layout();

            double alertWidth = alert.getDialogPane().getScene().getWindow().getWidth();
            double alertHeight = alert.getDialogPane().getScene().getWindow().getHeight();

            double centerX = (screenWidth - alertWidth) / 2;
            double centerY = (screenHeight - alertHeight) / 2;

            alert.getDialogPane().getScene().getWindow().setX(700);
            alert.getDialogPane().getScene().getWindow().setY(400);

            // Show alert and wait for user to close
            alert.showAndWait();
        });
    }

}
