package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ticktocktrack.database.DatabaseRegisterClass;
import ticktocktrack.database.DatabaseViewClassList;
import ticktocktrack.logic.Student;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the process of enrolling students into specific classes by providing
 * a user interface for selecting and managing student enrollments.
 */
public class TeacherEnrollmentStudent {

	/**
	 * Displays a dialog to allow the teacher to select students for enrollment in a specified class.
	 *
	 * @param allStudents A list of all available students to select from.
	 * @param courseName  The name of the course for which students are being enrolled.
	 * @param section     The section associated with the course.
	 * @param classId     The unique identifier for the class in which students will be enrolled.
	 * @param program     The program or curriculum to which the class belongs.
	 */
    public static void showStudentSelectionDialog(List<Student> allStudents, String courseName, String section, int classId, String program) {
        Stage dialog = new Stage();
        dialog.setTitle("Enroll Students in: " + courseName);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        ComboBox<String> yearLevelComboBox = new ComboBox<>();
        yearLevelComboBox.getItems().addAll("All", "1st Year", "2nd Year", "3rd Year", "4th Year");
        yearLevelComboBox.setValue("All");

        ListView<Student> selectedListView = new ListView<>();
        selectedListView.setPrefHeight(150);

        VBox studentsVBox = new VBox(10);
        studentsVBox.setPadding(new Insets(10));
        studentsVBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");

        // Get students already enrolled in the class
        Set<Integer> enrolledStudentIds = DatabaseRegisterClass.getEnrolledStudentIds(classId);

        // Initially show un-enrolled + matching section/program
        List<Student> filteredStudents = filterStudents(allStudents, section, program, "All", enrolledStudentIds);
        populateStudentRows(filteredStudents, studentsVBox, selectedListView);
        
        


        Button filterBtn = new Button("Filter");
        filterBtn.setOnAction(e -> {
            studentsVBox.getChildren().clear();
            List<Student> updatedFilter = filterStudents(allStudents, section, program, yearLevelComboBox.getValue(), enrolledStudentIds);
            populateStudentRows(updatedFilter, studentsVBox, selectedListView);
        });

        Button doneBtn = new Button("Enroll");
        doneBtn.setStyle("-fx-background-color: #00695C; -fx-text-fill: white;");
        doneBtn.setOnAction(e -> {
            for (Student student : selectedListView.getItems()) {
                boolean success = DatabaseRegisterClass.enrollStudentInClass(
                    student.getStudentId(),  // directly use int, no parsing
                    classId
                );
                if (success) {
                    System.out.println("Enrolled: " + student.getFullName());
                }
            }
            dialog.close();
        });


        layout.getChildren().addAll(
                new Text("Select students to enroll in Section " + section + ":"),
                yearLevelComboBox,
                filterBtn,
                studentsVBox,
                new Text("Selected Students:"),
                selectedListView,
                doneBtn
        );

        Scene scene = new Scene(layout, 600, 700);
        dialog.setScene(scene);
        dialog.show();
    }

    /**
     * Filters a list of students based on specified criteria such as section, program,
     * year level, and exclusion of already enrolled student IDs.
     *
     * @param students     The list of all students to filter.
     * @param section      The section to filter by. Use "All" to include all sections.
     * @param program      The program to filter by. Use "All" to include all programs.
     * @param yearLevel    The year level to filter by. Use "All" to include all year levels.
     * @param enrolledIds  A set of student IDs that are already enrolled and should be excluded.
     * @return A filtered list of students matching the specified criteria.
     */
    private static List<Student> filterStudents(List<Student> students, String section, String program, String yearLevel, Set<Integer> enrolledIds) {
        return students.stream()
                .filter(s -> !enrolledIds.contains(s.getStudentId()))
                .filter(s -> program.equals("All") || s.getProgram().equalsIgnoreCase(program))
                .filter(s -> section.equals("All") || s.getSection().equalsIgnoreCase(section))
                .filter(s -> yearLevel.equals("All") || s.getYearLevel().equalsIgnoreCase(yearLevel))
                .collect(Collectors.toList());
    }

    /**
     * Populates a container with rows representing students and allows selection for enrollment.
     *
     * @param students           The list of students to display in rows.
     * @param container          The container to populate with student rows.
     * @param selectedListView   The list view that holds selected students.
     */
    private static void populateStudentRows(List<Student> students, VBox container, ListView<Student> selectedListView) {
        for (Student student : students) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            String display = student.getFullName() + " [" + student.getUsername() + "] | Year: " + student.getYearLevel() + " | Section: " + student.getSection();
            Text info = new Text(display);
            info.setWrappingWidth(440);

            Button addBtn = new Button("Add");
            addBtn.setStyle("-fx-background-color: #0097A7; -fx-text-fill: white;");
            addBtn.setOnAction(e -> {
                if (!selectedListView.getItems().contains(student)) {
                    selectedListView.getItems().add(student);
                }
            });

            row.getChildren().addAll(addBtn, info);
            container.getChildren().add(row);
        }
    }
}
