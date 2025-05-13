package ticktocktrack.logic;

import javafx.scene.control.Alert;
import ticktocktrack.database.DatabaseAddCourse;

public class AddCourse {

	public static boolean createCourse(String courseName, String section) {
	    if (courseName == null || courseName.trim().isEmpty()) {
	        showAlert(Alert.AlertType.ERROR, "Error", "Course name is required.");
	        return false;
	    }

	    // Check if course already exists
	    boolean alreadyExists = DatabaseAddCourse.courseExists(courseName, section);

	    if (alreadyExists) {
	        showAlert(Alert.AlertType.ERROR, "Error", "Course already exists! Try a different name or section.");
	        return false;
	    } else {
	        boolean success = DatabaseAddCourse.addCourse(courseName, section);
	        if (success) {
	            // Fetch the course ID after adding the course
	            int courseId = DatabaseAddCourse.getCourseId(courseName, section); // New method to get the course ID
	            if (courseId != -1) {
	                showAlert(Alert.AlertType.INFORMATION, "Success", "Course added successfully!");
	                return true; // Return true if the course was added successfully
	            } else {
	                showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve course ID.");
	                return false;
	            }
	        } else {
	            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add course. Please try again.");
	            return false;
	        }
	    }
	}

    private static void showAlert(Alert.AlertType type, String title, String message) {
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
