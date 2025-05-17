package ticktocktrack.logic;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ticktocktrack.database.DatabaseViewClassList;
import ticktocktrack.database.Session;
import ticktocktrack.database.UsersModel;
import ticktocktrack.gui.TeacherViewClassListCenterPanel;

import java.util.ArrayList;
import java.util.List;

public class ViewClassList {

	public static List<String[]> getCourses() {
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user session found.");
            return new ArrayList<>();
        }
        return DatabaseViewClassList.getCoursesByTeacherId();
    }

    // Update a class with new course and section info
    public static void editCourse(String oldCourseName, String oldSection, String newCourseName, String newSection) {
        DatabaseViewClassList.updateCourse(oldCourseName, oldSection, newCourseName, newSection);
    }

    // Delete a class by course name and section
    public static void deleteCourse(String courseName, String section) {
        DatabaseViewClassList.deleteCourse(courseName);
    }


    // ====== GUI event handlers ======

    public static void handleEditCourse(String oldCourseName, String oldSection) {
        TextField courseNameField = new TextField(oldCourseName);
        TextField sectionField = new TextField(oldSection);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Edit Class");
        alert.setHeaderText("Edit the class details:");
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
        confirm.setTitle("Delete Class");
        confirm.setHeaderText("Are you sure you want to delete the class?\n" + courseName + " - " + section);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteCourse(courseName, section);
                TeacherViewClassListCenterPanel.updateClassListPanel();
            }
        });
    }

    // Paging logic to move between pages in GUI

    public static void goToNextPage() {
        TeacherViewClassListCenterPanel.incrementPage();
    }

    public static void goToPreviousPage() {
        TeacherViewClassListCenterPanel.decrementPage();
    }

    // Map program to short name (if used in GUI)
    public static String mapProgramToShortName(String programName) {
        if (programName == null) {
            return "N/A"; // Return "N/A" if programName is null
        }

        // Trim the programName to remove leading/trailing spaces
        programName = programName.trim();

        // Check if "–" exists in the string and limit the program name to before the "–"
        int dashIndex = programName.lastIndexOf("–");
        if (dashIndex != -1) {
            programName = programName.substring(0, dashIndex).trim();
        }

        // Mapping of full names to short names
        java.util.Map<String, String> programMap = new java.util.HashMap<>();
        programMap.put("BS in Electronics Engineering", "BSECE");
        programMap.put("BS in Mechanical Engineering", "BSME");
        programMap.put("BS in Accountancy", "BSA");
        programMap.put("BSBA major in Human Resource Development Management", "BSBA-HRDM");
        programMap.put("BSBA major in Marketing Management", "BSBA-MM");
        programMap.put("BS in Entrepreneurship", "BSENTREP");
        programMap.put("BS in Information Technology", "BSIT");
        programMap.put("BS in Applied Mathematics", "BSAM");
        programMap.put("Bachelor in Secondary Education major in English", "BSED-ENGLISH");
        programMap.put("Bachelor in Secondary Education major in Mathematics", "BSED-MATH");
        programMap.put("BS in Office Administration", "BSOA");

        // Check if the program is in the map and return the corresponding short name
        String shortName = programMap.get(programName);

        // If the program is not found in the map, return a shorter version of the original name
        if (shortName == null) {
            return programName.length() > 15 ? programName.substring(0, 15) + "..." : programName;
        }

        return shortName;
    }

}
