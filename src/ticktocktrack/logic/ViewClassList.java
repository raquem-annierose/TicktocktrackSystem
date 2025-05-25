package ticktocktrack.logic;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ticktocktrack.database.DatabaseViewClassList;
import ticktocktrack.gui.TeacherViewClassListCenterPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides functionalities to view, edit, delete, and manage class lists for teachers.
 * Includes GUI event handlers for editing and deleting courses,
 * as well as utility methods for pagination and program name mapping.
 */
public class ViewClassList {

    /**
     * Retrieves the list of courses assigned to the current logged-in teacher.
     * Each course is represented as a String array (e.g., course name, section).
     * 
     * @return a list of String arrays representing courses, or an empty list if no user session exists.
     */
    public static List<String[]> getCourses() {
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user session found.");
            return new ArrayList<>();
        }
        return DatabaseViewClassList.getCoursesByTeacherId();
    }

    /**
     * Updates a course entry with new course name and section.
     * 
     * @param oldCourseName the original course name
     * @param oldSection the original section name
     * @param newCourseName the new course name to update
     * @param newSection the new section to update
     */
    public static void editCourse(String oldCourseName, String oldSection, String newCourseName, String newSection) {
        DatabaseViewClassList.updateCourse(oldCourseName, oldSection, newCourseName, newSection);
    }

    /**
     * Deletes a course given its course name and section.
     * 
     * @param courseName the course name to delete
     * @param section the section of the course to delete
     */
    public static void deleteCourse(String courseName, String section) {
        DatabaseViewClassList.deleteCourse(courseName, section);
    }


    // ====== GUI Event Handlers ======

    /**
     * Handles the GUI event for editing a course.
     * Displays a confirmation dialog with editable fields for course name and section.
     * If confirmed, updates the course and refreshes the class list UI.
     * 
     * @param oldCourseName the current course name
     * @param oldSection the current section
     * @param teacherId the teacher's ID to refresh the class list panel
     */
    public static void handleEditCourse(String oldCourseName, String oldSection, int teacherId) {
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
                TeacherViewClassListCenterPanel.updateClassListPanel(teacherId);
            }
        });
    }

    /**
     * Handles the GUI event for deleting a course.
     * Shows a confirmation dialog, and if confirmed, deletes the course and refreshes the class list UI.
     * 
     * @param courseName the course name to delete
     * @param section the section to delete
     * @param teacherId the teacher's ID to refresh the class list panel
     */
    public static void handleDeleteCourse(String courseName, String section, int teacherId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Class");
        confirm.setHeaderText("Are you sure you want to delete the class?\n" + courseName + " - " + section);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteCourse(courseName, section);
                TeacherViewClassListCenterPanel.updateClassListPanel(teacherId);
            }
        });
    }

    /**
     * Advances the class list UI to the next page.
     */
    public static void goToNextPage() {
        TeacherViewClassListCenterPanel.incrementPage();
    }

    /**
     * Moves the class list UI to the previous page.
     */
    public static void goToPreviousPage() {
        TeacherViewClassListCenterPanel.decrementPage();
    }

    /**
     * Maps a full program name to its short code equivalent.
     * If the program name contains a dash "–", it uses the substring before the dash.
     * Returns a default abbreviation or truncates the name if no mapping is found.
     * 
     * @param programName the full name of the academic program
     * @return the short name or abbreviation of the program
     */
    public static String mapProgramToShortName(String programName) {
        if (programName == null) {
            return "N/A"; // Return "N/A" if programName is null
        }

        programName = programName.trim();

        int dashIndex = programName.lastIndexOf("–");
        if (dashIndex != -1) {
            programName = programName.substring(0, dashIndex).trim();
        }

        java.util.Map<String, String> programMap = new java.util.HashMap<>();
        programMap.put("BS in Electronics Engineering", "BSECE");
        programMap.put("BS in Mechanical Engineering", "BSME");
        programMap.put("BS in Accountancy", "BSA");
        programMap.put("BSBA major in Human Resource Development Management", "BSBA-HRDM");
        programMap.put("BSBA major in Marketing Management", "BSBA-MM");
        programMap.put("BS in Entrepreneurship", "BSENTREP");
        programMap.put("BS in Information Technology", "BSIT");
        programMap.put("Diploma Information Technology", "DIT");
        programMap.put("BS in Applied Mathematics", "BSAM");
        programMap.put("Bachelor in Secondary Education major in English", "BSED-ENGLISH");
        programMap.put("Bachelor in Secondary Education major in Mathematics", "BSED-MATH");
        programMap.put("BS in Office Administration", "BSOA");

        String shortName = programMap.get(programName);

        if (shortName == null) {
            return programName.length() > 15 ? programName.substring(0, 15) + "..." : programName;
        }

        return shortName;
    }

}