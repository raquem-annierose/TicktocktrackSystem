package ticktocktrack.logic;

import javafx.scene.control.Alert;
import ticktocktrack.database.DatabaseAddCourse;

public class AddCourse {

    public static void createCourse(String courseName, String section) {
        if (courseName == null || courseName.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Course name is required.");
            return;
        }

        // Check if course already exists
        boolean alreadyExists = DatabaseAddCourse.courseExists(courseName, section);

        if (alreadyExists) {
            showAlert(Alert.AlertType.ERROR, "Error", "Course already exists! Try a different name or section.");
        } else {
            boolean success = DatabaseAddCourse.addCourse(courseName, section);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Course added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add course. Please try again.");
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
