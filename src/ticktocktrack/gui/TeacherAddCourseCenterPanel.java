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

    // Remove the duplicate method definition from here
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
        sectionField.setPromptText("Section");
        sectionField.setPrefWidth(260);
        sectionField.setStyle("-fx-background-color: #eeeeee; -fx-border-color: transparent transparent black transparent; -fx-border-width: 1px;");

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

            // Create the course
            boolean success = AddCourse.createCourse(courseName, section);
            if (success) {
                int courseId = DatabaseAddCourse.getCourseId(courseName, section); // Get the course ID
                if (courseId != -1) {
                    // Fetch students after creating the course
                    List<Student> students = DatabaseAddCourse.getStudents();
                    showStudentSelectionDialog(students, courseName, section, courseId); // Pass courseId here
                }
            }
        });
        // Enable the Create button if both fields are filled
        courseField.textProperty().addListener((observable, oldValue, newValue) -> updateCreateButtonState(createBtn, courseField, sectionField));
        sectionField.textProperty().addListener((observable, oldValue, newValue) -> updateCreateButtonState(createBtn, courseField, sectionField));

        buttonBox.getChildren().addAll(cancelBtn, createBtn);
        dialog.getChildren().addAll(title, courseField, sectionField, buttonBox);
        overlay.getChildren().add(dialog);

        return overlay;
    }

    private static void updateCreateButtonState(Button createBtn, TextField courseField, TextField sectionField) {
        String courseName = courseField.getText().trim();
        String section = sectionField.getText().trim();

        boolean enable = !courseName.isEmpty() && !section.isEmpty();
        createBtn.setDisable(!enable);

        // Update style when enabled/disabled
        if (enable) {
            createBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #0097A7; -fx-font-weight: bold;");
        } else {
            createBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaa;");
        }
    }

    private static void showStudentSelectionDialog(List<Student> students, String courseName, String section, int courseId) {
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

        List<Student> filteredStudents = students.stream()
                .filter(student -> section.equalsIgnoreCase(student.getSection()))
                .filter(student -> "All".equals(yearLevelComboBox.getValue()) || student.getYearLevel().equalsIgnoreCase(yearLevelComboBox.getValue()))
                .collect(Collectors.toCollection(ArrayList::new)); // Use ArrayList for a mutable list

        // Filter button for applying year level filter
        Button filterBtn = new Button("Filter by Year Level");
        filterBtn.setStyle("-fx-background-color: #0097A7; -fx-text-fill: white;");
        filterBtn.setOnAction(e -> {
            // Create a new list to hold the filtered students
            List<Student> newFilteredStudents = students.stream()
                .filter(student -> section.equalsIgnoreCase(student.getSection()))
                .filter(student -> "All".equals(yearLevelComboBox.getValue()) || student.getYearLevel().equalsIgnoreCase(yearLevelComboBox.getValue()))
                .toList();

            // Clear the existing list and add the filtered students
            filteredStudents.clear();
            filteredStudents.addAll(newFilteredStudents);

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

        studentDialog.setScene(new Scene(layout, 500, 550));
        studentDialog.show();
    }

    private static void populateStudentRows(List<Student> filteredStudents, VBox allStudentsBox, ListView<Student> selectedListView) {
        for (Student student : filteredStudents) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            // Show name + year + section
            String displayText = student.getFirstName() + " " + student.getLastName() +
                    " | Year: " + student.getYearLevel() +
                    " | Section: " + student.getSection();

            Text studentInfo = new Text(displayText);
            Button addBtn = new Button("Add");
            addBtn.setStyle("-fx-background-color: #0097A7; -fx-text-fill: white;");

            addBtn.setOnAction(e -> {
                if (!selectedListView.getItems().contains(student)) {
                    selectedListView.getItems().add(student);
                }
            });

            row.getChildren().addAll(studentInfo, addBtn);
            allStudentsBox.getChildren().add(row);
        }
    }


}
