package ticktocktrack.logic;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ticktocktrack.database.DatabaseViewClassList;
import ticktocktrack.database.DatabaseAddCourse;
import ticktocktrack.gui.TeacherViewClassListCenterPanel;

import java.util.List;

public class ViewClassList {

    public static List<String[]> getCourses() {
        return DatabaseViewClassList.getCourses();
    }

    public static void editCourse(String oldCourseName, String oldSection, String newCourseName, String newSection) {
        DatabaseViewClassList.updateCourse(oldCourseName, oldSection, newCourseName, newSection);
    }

    public static void deleteCourse(String courseName, String section) {
        DatabaseViewClassList.deleteCourse(courseName, section);
    }

    public static void addNewCourse(String courseName, String section) {
        DatabaseAddCourse.addCourse(courseName, section);
    }

    // ========== HANDLER Methods ==========

    public static void handleEditCourse(String oldCourseName, String oldSection) {
        TextField courseNameField = new TextField(oldCourseName);
        TextField sectionField = new TextField(oldSection);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Edit Course");
        alert.setHeaderText("Edit the course:");
        alert.getDialogPane().setContent(new VBox(10, courseNameField, sectionField));

        ButtonType saveButton = new ButtonType("Save");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(saveButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == saveButton) {
                editCourse(oldCourseName, oldSection, courseNameField.getText(), sectionField.getText());
                TeacherViewClassListCenterPanel.updateClassListPanel();
            }
        });
    }

    public static void handleDeleteCourse(String courseName, String section) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Course");
        confirm.setHeaderText("Are you sure you want to delete?");
        confirm.setContentText(courseName + " - " + section);

        if (confirm.showAndWait().get() == ButtonType.OK) {
            deleteCourse(courseName, section);
            TeacherViewClassListCenterPanel.updateClassListPanel();
        }
    }

    public static void goToNextPage() {
        TeacherViewClassListCenterPanel.incrementPage();
    }

    public static void goToPreviousPage() {
        TeacherViewClassListCenterPanel.decrementPage();
    }
}
