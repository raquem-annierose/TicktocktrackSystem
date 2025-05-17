package ticktocktrack.logic;

import javafx.scene.control.Alert;
import ticktocktrack.database.DatabaseRegisterClass;

public class RegisterClass {


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
