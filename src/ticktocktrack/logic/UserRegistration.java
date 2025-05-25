package ticktocktrack.logic;

import ticktocktrack.database.DatabaseRegistrationManager;

import java.sql.Connection;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Handles user registration logic for faculty and students.
 * Provides methods to register Admin, Teacher, and Student users,
 * including validation and password hashing.
 */
public class UserRegistration {

    /**
     * Registers a faculty user (Admin or Teacher) with the given details.
     * Passwords are hashed before storing.
     * Uses the current admin's ID as the creator.
     *
     * @param username        the desired username
     * @param email           the email address
     * @param role            the role, either "Admin" or "Teacher"
     * @param password        the password
     * @param confirmPassword the password confirmation (not validated here, should be done before calling)
     * @param firstName       the user's first name
     * @param lastName        the user's last name
     * @return true if registration succeeded, false otherwise
     */
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

    /**
     * Registers a student user with the given details.
     * Password and confirmation must match; password is hashed before storing.
     * Shows alerts for errors or success.
     *
     * @param username        the desired username
     * @param email           the email address
     * @param password        the password
     * @param confirmPassword the password confirmation
     * @param firstName       the student's first name
     * @param middleName      the student's middle name
     * @param lastName        the student's last name
     * @param yearLevel       the year level of the student
     * @param program         the academic program
     * @param section         the section
     */
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

    /**
     * Utility method to show a JavaFX alert dialog with specified type, title, and message.
     * Runs on the JavaFX Application Thread.
     *
     * @param alertType the type of alert (ERROR, INFORMATION, etc.)
     * @param title     the alert window title
     * @param message   the message to display in the alert
     */
    private static void showAlert(AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Center the alert window (manually positioning)
            double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();

            alert.getDialogPane().applyCss();
            alert.getDialogPane().layout();

            double alertWidth = alert.getDialogPane().getScene().getWindow().getWidth();
            double alertHeight = alert.getDialogPane().getScene().getWindow().getHeight();

            // These values (700,400) can be replaced with calculated center positions if preferred
            alert.getDialogPane().getScene().getWindow().setX(700);
            alert.getDialogPane().getScene().getWindow().setY(400);

            alert.showAndWait();
        });
    }

}