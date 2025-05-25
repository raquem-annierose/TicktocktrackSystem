package ticktocktrack.logic;

import javafx.scene.control.Alert;
import ticktocktrack.database.DatabaseRegisterClass;

/**
 * Handles the logic for registering a class associated with a teacher.
 */
public class RegisterClass {

    /**
     * Attempts to create a new class for the currently logged-in teacher.
     * Performs validation on inputs and checks for duplicates.
     * 
     * @param courseName The name of the course.
     * @param section The section identifier.
     * @param program The program or degree associated with the class.
     * @return true if the class was created successfully; false otherwise.
     */
    public static boolean createClass(String courseName, String section, String program) {
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user logged in.");
            return false;
        }

        Integer teacherId = currentUser.getTeacherId();
        if (teacherId == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Current user is not a teacher.");
            return false;
        }

        if (courseName == null || courseName.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Course name is required.");
            return false;
        }

        // ❗ Check if class already exists
        if (DatabaseRegisterClass.classExists(teacherId, courseName, section, program)) {
            showAlert(Alert.AlertType.WARNING, "Duplicate Class", "A class with the same Course Name, Section, and Program already exists.");
            return false;
        }

        boolean success = DatabaseRegisterClass.addClass(teacherId, courseName, section, program);
        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create class.");
            return false;
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "Class created successfully.");
        return true;
    }

    /**
     * Displays a JavaFX alert dialog with the specified type, title, and message.
     * The alert is positioned manually on the screen.
     * 
     * @param type The type of alert (e.g., ERROR, WARNING, INFORMATION).
     * @param title The title of the alert window.
     * @param message The content message of the alert.
     */
    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Force showing the window before setting position
        alert.setOnShown(e -> {
            // Get the underlying stage (window)
            javafx.stage.Stage stage = (javafx.stage.Stage) alert.getDialogPane().getScene().getWindow();

            // Set manual position (example: x=500, y=300)
            stage.setX(730); // Set X position
            stage.setY(440); // Set Y position
        });

        alert.showAndWait();
    }
}
