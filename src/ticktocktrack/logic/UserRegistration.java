package ticktocktrack.logic;

import ticktocktrack.database.DatabaseRegistrationManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class UserRegistration {

    // Method to register a faculty member
    public static void registerFaculty(String username, String email, String role, String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            String passwordHash = DatabaseRegistrationManager.hashPassword(password);

            if (passwordHash != null) {
                boolean isRegistered = false;
                if (role.equals("Admin") || role.equals("Teacher")) {
                    isRegistered = DatabaseRegistrationManager.registerUser(username, role, email, passwordHash, null, null, null, null, null);
                }

                if (isRegistered) {
                    showAlert(AlertType.INFORMATION, "Registration Successful", "Faculty successfully registered!");
                } else {
                    showAlert(AlertType.ERROR, "Registration Failed", "Registration failed. Please try again.");
                }
            } else {
                showAlert(AlertType.ERROR, "Error", "Error hashing the password.");
            }
        } else {
            showAlert(AlertType.ERROR, "Error", "Passwords do not match!");
        }
    }

    // Method to register a student
    public static void registerStudent(String username, String email, String password, String confirmPassword,
                                       String firstName, String middleName, String lastName, String yearLevel, String section) {
        if (password.equals(confirmPassword)) {
            String passwordHash = DatabaseRegistrationManager.hashPassword(password);

            if (passwordHash != null) {
                boolean isRegistered = DatabaseRegistrationManager.registerStudent(username, "Student", email, passwordHash,
                        firstName, middleName, lastName, yearLevel, section);

                if (isRegistered) {
                    showAlert(AlertType.INFORMATION, "Registration Successful", "Student successfully registered!");
                } else {
                    showAlert(AlertType.ERROR, "Registration Failed", "Registration failed. Please try again.");
                }
            } else {
                showAlert(AlertType.ERROR, "Error", "Error hashing the password.");
            }
        } else {
            showAlert(AlertType.ERROR, "Error", "Passwords do not match!");
        }
    }

 // Method to show standard styled alerts with adjustable position
    private static void showAlert(AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Optional: Set the owner window if you have one (can help positioning properly)
            // alert.initOwner(mainStage);

            // Show the alert first so we can adjust its position
            alert.show();

            // Now adjust position (example: center screen or custom coordinates)
            double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();

            double alertWidth = alert.getDialogPane().getScene().getWindow().getWidth();
            double alertHeight = alert.getDialogPane().getScene().getWindow().getHeight();

            // Example: center of the screen
            double centerX = (screenWidth - alertWidth) / 2;
            double centerY = (screenHeight - alertHeight) / 2;

            // Set new position
            alert.getDialogPane().getScene().getWindow().setX(centerX);
            alert.getDialogPane().getScene().getWindow().setY(centerY);
        });
    }

}
