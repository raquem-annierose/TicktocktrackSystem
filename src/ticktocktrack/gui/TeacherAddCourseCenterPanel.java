package ticktocktrack.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ticktocktrack.database.DatabaseAddCourse;
import ticktocktrack.database.Student;
import ticktocktrack.logic.AddCourse;

public class TeacherAddCourseCenterPanel {

    public static Pane createAddCourseDialog(Pane root) {
        Pane overlay = new Pane();
        overlay.setPrefSize(1300, 750);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");

        VBox dialog = new VBox(12);
        dialog.setPrefWidth(350);
        dialog.setMaxWidth(350);
        dialog.setPadding(new Insets(20));
        dialog.setStyle("-fx-background-color: white;");
        dialog.setEffect(new DropShadow(8, Color.gray(0.3)));
        dialog.setMaxHeight(Region.USE_PREF_SIZE);

        dialog.setLayoutX(380);
        dialog.setLayoutY(230);

        // Title
        Text title = new Text("Add Course");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web("#02383E"));

        // Fields
        TextField courseField = new TextField();
        courseField.setPromptText("Course Name (required)");
        courseField.setPrefWidth(260);
        courseField.setStyle("-fx-background-color: #eeeeee; -fx-border-color: transparent transparent black transparent; -fx-border-width: 1px;");

        TextField sectionField = new TextField();
        sectionField.setPromptText("Section (required)");
        sectionField.setPrefWidth(260);
        sectionField.setStyle("-fx-background-color: #eeeeee; -fx-border-color: transparent transparent black transparent; -fx-border-width: 1px;");

        // New Program ComboBox (NEW)
        ComboBox<String> programComboBox = new ComboBox<>();
        programComboBox.getItems().addAll(
            "BSECE – BS in Electronics Engineering",
            "BSME – BS in Mechanical Engineering",
            "BSA – BS in Accountancy",
            "BSBA-HRDM – BSBA major in Human Resource Development Management",
            "BSBA-MM – BSBA major in Marketing Management",
            "BSENTREP – BS in Entrepreneurship",
            "BSIT – BS in Information Technology",
            "BSAM – BS in Applied Mathematics",
            "BSED-ENGLISH – Bachelor in Secondary Education major in English",
            "BSED-MATH – Bachelor in Secondary Education major in Mathematics",
            "BSOA – BS in Office Administration"
        );
        programComboBox.setPromptText("Select Program");
        programComboBox.setPrefWidth(260);

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #0097A7;");
        cancelBtn.setOnAction(e -> root.getChildren().remove(overlay));

        Button createBtn = new Button("Create");
        createBtn.setDisable(true); // Start with disabled button
        createBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaa;");

        createBtn.setOnAction(e -> {
            String courseName = courseField.getText().trim();
            String section = sectionField.getText().trim();
            String program = programComboBox.getValue();

            // Ensure the program is selected before creating the course
            if (program != null && !program.isEmpty()) {
                // Create the course
                boolean success = AddCourse.createCourse(courseName, section, program);
                if (success) {
                    int courseId = DatabaseAddCourse.getCourseId(courseName, section, program); // Get the course ID
                    if (courseId != -1) {
                        // Fetch students after creating the course
                        List<Student> students = DatabaseAddCourse.getStudents();
                        showStudentSelectionDialog(students, courseName, section, courseId, program); // Pass program to student dialog
                    }
                }
            }
        });

        // Enable the Create button if all fields are filled
        courseField.textProperty().addListener((observable, oldValue, newValue) -> updateCreateButtonState(createBtn, courseField, sectionField, programComboBox));
        sectionField.textProperty().addListener((observable, oldValue, newValue) -> updateCreateButtonState(createBtn, courseField, sectionField, programComboBox));
        programComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateCreateButtonState(createBtn, courseField, sectionField, programComboBox));

        buttonBox.getChildren().addAll(cancelBtn, createBtn);
        dialog.getChildren().addAll(title, courseField, sectionField, programComboBox, buttonBox);
        overlay.getChildren().add(dialog);

        return overlay;
    }

    private static void updateCreateButtonState(Button createBtn, TextField courseField, TextField sectionField, ComboBox<String> programComboBox) {
        String courseName = courseField.getText().trim();
        String section = sectionField.getText().trim();
        String program = programComboBox.getValue();

        boolean enable = !courseName.isEmpty() && !section.isEmpty() && program != null && !program.isEmpty();
        createBtn.setDisable(!enable);

        // Update style when enabled/disabled
        if (enable) {
            createBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #0097A7; -fx-font-weight: bold;");
        } else {
            createBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaa;");
        }
    }

    private static void showStudentSelectionDialog(List<Student> students, String courseName, String section, int courseId, String program) {
        Stage studentDialog = new Stage();
        studentDialog.setTitle("Select Students");

        VBox allStudentsBox = new VBox(10);
        allStudentsBox.setPadding(new Insets(10));
        allStudentsBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");

        ListView<Student> selectedListView = new ListView<>();
        selectedListView.setPrefHeight(120);

        // Create ComboBox for selecting year level
        ComboBox<String> yearLevelComboBox = new ComboBox<>();
        yearLevelComboBox.getItems().addAll("All", "1st Year", "2nd Year", "3rd Year", "4th Year");
        yearLevelComboBox.setValue("All"); // Default value

        // Filter students based on section, program, and year level
        List<Student> filteredStudents = getFilteredStudents(students, section, program, "All");

        // Filter button for applying year level filter
        Button filterBtn = new Button("Filter by Year Level");
        filterBtn.setStyle("-fx-background-color: #0097A7; -fx-text-fill: white;");
        filterBtn.setOnAction(e -> {
            filteredStudents.clear(); // Clear existing filter
            filteredStudents.addAll(getFilteredStudents(students, section, program, yearLevelComboBox.getValue()));
            allStudentsBox.getChildren().clear(); // Clear the existing students list
            populateStudentRows(filteredStudents, allStudentsBox, selectedListView);
        });

        // Populate student rows based on the filtered list
        populateStudentRows(filteredStudents, allStudentsBox, selectedListView);

        // Done button action
        Button doneBtn = new Button("Done");
        doneBtn.setStyle("-fx-background-color: #00695C; -fx-text-fill: white; -fx-font-weight: bold;");
        doneBtn.setOnAction(e -> {
            for (Student selectedStudent : selectedListView.getItems()) {
                boolean enrolled = DatabaseAddCourse.enrollStudentInCourse(selectedStudent.getStudentId(), courseId, section);
                if (enrolled) {
                    System.out.println("Enrolled: " + selectedStudent.getFirstName() + " " + selectedStudent.getLastName());
                }
            }
            studentDialog.close();
        });

        // Layout for the dialog
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Text("Available Students (Section " + section + "):"),
                yearLevelComboBox,
                filterBtn,
                allStudentsBox,
                new Text("Selected Students:"),
                selectedListView,
                doneBtn
        );

        studentDialog.setScene(new Scene(layout, 600, 650));
        studentDialog.show();
    }

    private static List<Student> getFilteredStudents(List<Student> students, String section, String program, String yearLevel) {
        return students.stream()
                .filter(student -> {
                    // Handle null program gracefully
                    boolean programMatch = (student.getProgram() != null && student.getProgram().equalsIgnoreCase(program)) || program.equals("All");
                    boolean sectionMatch = student.getSection().equalsIgnoreCase(section) || section.equals("All");
                    boolean yearLevelMatch = student.getYearLevel().equalsIgnoreCase(yearLevel) || yearLevel.equals("All");

                    return programMatch && sectionMatch && yearLevelMatch;
                })
                .collect(Collectors.toList());
    }

    private static void populateStudentRows(List<Student> filteredStudents, VBox allStudentsBox, ListView<Student> selectedListView) {
        for (Student student : filteredStudents) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            // Build display text with full name (including middle name), username, year, and section
            String displayText = student.getFirstName() + " " +
                (student.getMiddleName() != null && !student.getMiddleName().isBlank() ? student.getMiddleName() + " " : "") +
                student.getLastName() +
                " [" + student.getUsername() + "]" +
                " | Year: " + student.getYearLevel() +
                " | Section: " + student.getSection();

            // Create student info text
            Text studentInfo = new Text(displayText);
            studentInfo.setWrappingWidth(440); // Adjust to make lines align nicely

            // Create "Add" button
            Button addBtn = new Button("Add");
            addBtn.setStyle("-fx-background-color: #0097A7; -fx-text-fill: white;");
            addBtn.setOnAction(e -> {
                if (!selectedListView.getItems().contains(student)) {
                    selectedListView.getItems().add(student);
                }
            });

            // Add button first, then student info
            row.getChildren().addAll(addBtn, studentInfo);
            allStudentsBox.getChildren().add(row);
        }
    }
}
