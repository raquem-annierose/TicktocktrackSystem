package ticktocktrack.logic;

import javafx.scene.control.Alert;
import ticktocktrack.database.DatabaseRegisterClass;

public class RegisterClass {

	public static boolean createCourse(String courseName) {
	    if (courseName == null || courseName.trim().isEmpty()) {
	        showAlert(Alert.AlertType.ERROR, "Error", "Course name is required.");
	        return false;
	    }

	    if (DatabaseRegisterClass.courseExists(courseName)) {
	        showAlert(Alert.AlertType.ERROR, "Error", "Course already exists!");
	        return false;
	    }

	    boolean success = DatabaseRegisterClass.addCourse(courseName);
	    if (success) {
	        showAlert(Alert.AlertType.INFORMATION, "Success", "Course added successfully!");
	        return true;
	    } else {
	        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add course.");
	        return false;
	    }
	}

	
	public static boolean createClass(String courseName, int teacherId, String section, String program) {
	    if (courseName == null || courseName.trim().isEmpty()) {
	        showAlert(Alert.AlertType.ERROR, "Error", "Course name is required.");
	        return false;
	    }

	    // Ensure course exists
	    if (!DatabaseRegisterClass.courseExists(courseName)) {
	        if (!DatabaseRegisterClass.addCourse(courseName)) {
	            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add course.");
	            return false;
	        }
	    }

	    int courseId = DatabaseRegisterClass.getCourseId(courseName);
	    if (courseId == -1) {
	        showAlert(Alert.AlertType.ERROR, "Error", "Course not found after creation.");
	        return false;
	    }

	    boolean success = DatabaseRegisterClass.addClass(courseId, teacherId, section, program);
	    if (success) {
	        showAlert(Alert.AlertType.INFORMATION, "Success", "Class added successfully!");
	        return true;
	    } else {
	        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add class.");
	        return false;
	    }
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
