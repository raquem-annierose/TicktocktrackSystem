package ticktocktrack.gui;

import javafx.scene.paint.Color;  // Import JavaFX Color
import javafx.scene.text.Font;   // Import JavaFX Font
import javafx.scene.text.FontWeight;

import java.util.List;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ticktocktrack.database.DatabaseViewClassList;
import ticktocktrack.database.Student;

public class TeacherViewClassStudents {

    private static Pane centerPanel;

    public static void showStudentList(String courseName) {
        centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        
        addTitle(courseName);
        addStudentTable(courseName);

        // Replace the current center panel with the new one showing student details
        // Assuming you have a reference to the main window (in TeacherViewClassListCenterPanel) to replace the panel
        TeacherViewClassListCenterPanel.updateCenterPanel(centerPanel);
    }

    private static void addTitle(String courseName) {
        Text title = new Text("Students for " + courseName);
        // Use JavaFX Font and Color
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 36));  // JavaFX Font
        title.setFill(Color.web("#02383E"));  // JavaFX Color
        title.setLayoutX(50);
        title.setLayoutY(70);

        centerPanel.getChildren().add(title);
    }

    private static void addStudentTable(String courseName) {
        VBox studentTable = new VBox(10);
        studentTable.setLayoutX(50);
        studentTable.setLayoutY(150);

        // Get the list of sections for the course
        List<String[]> courses = DatabaseViewClassList.getCourses();
        String section = null;

        // Find the correct section for the selected course
        for (String[] course : courses) {
            if (course[0].equals(courseName)) {
                section = course[1]; // Get the section for the course
                break;
            }
        }

        if (section != null) {
            // Fetch students for the specific course and section
            List<Student> students = DatabaseViewClassList.getStudentsForCourse(courseName, section);
            
            for (Student student : students) {
                // Display the username instead of student ID
                Text studentText = new Text("Username: " + student.getUsername() + " | Name: " + student.getFirstName() + " " + student.getLastName() + " | Year: " + student.getYearLevel() + " | Section: " + student.getSection());
                studentTable.getChildren().add(studentText);
            }
        } else {
            Text noStudents = new Text("No students found for the selected course.");
            studentTable.getChildren().add(noStudents);
        }

        centerPanel.getChildren().add(studentTable);
    }


}
