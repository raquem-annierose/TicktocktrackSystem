package ticktocktrack.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ticktocktrack.database.DatabaseViewClassList;
import ticktocktrack.database.Student;

import java.util.List;

public class TeacherViewClassStudents {

    private static Pane centerPanel;

    public static void showStudentList(String courseName) {
        centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: #F0F8FF; -fx-border-color: transparent; -fx-border-width: 0;");

        String shadowPath = TeacherViewClassStudents.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutY(-115);

        // ✅ Add shadowView FIRST so it's in the background
        centerPanel.getChildren().add(shadowView);

        addTitle(courseName);
        addStudentTable(courseName);

        TeacherViewClassListCenterPanel.updateCenterPanel(centerPanel);
    }
    private static void addTitle(String courseName) {
        List<String[]> courses = DatabaseViewClassList.getCourses();
        String section = null;

        for (String[] course : courses) {
            if (course[0].equals(courseName)) {
                section = course[1];
                break;
            }
        }

        String fullTitle = "Students for " + courseName;
        if (section != null) {
            fullTitle += "\n (Section " + section + ")";
        }

        Pane manualLayout = new Pane(); // Use Pane to set absolute positions

        // Title Text
        Text title = new Text(fullTitle);
        title.setFont(javafx.scene.text.Font.font("Poppins", javafx.scene.text.FontWeight.BOLD, 28));
        title.setFill(javafx.scene.paint.Color.web("#02383E"));
        title.setLayoutX(30);
        title.setLayoutY(50);

        // Search Field
        TextField searchField = new TextField();
        searchField.setPromptText("Search Student");
        searchField.setPrefWidth(215); // Reduced width
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 8 12; -fx-background-radius: 20px;");
        searchField.setLayoutX(620); // Manually position X
        searchField.setLayoutY(42);  // Manually position Y

        // Add Student Button
        Button addStudentBtn = new Button("+ Add Student");
        addStudentBtn.setStyle("-fx-background-color: #5D9CA2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8;");
        addStudentBtn.setPrefHeight(40);
        addStudentBtn.setPrefWidth(140);
        addStudentBtn.setLayoutX(860); // Next to search field
        addStudentBtn.setLayoutY(40);
        addStudentBtn.setCursor(javafx.scene.Cursor.HAND);

        manualLayout.getChildren().addAll(title, searchField, addStudentBtn);

        centerPanel.getChildren().add(manualLayout);
    }

    private static void addStudentTable(String courseName) {
        VBox studentTable = new VBox(10);
        studentTable.setLayoutX(30);
        studentTable.setLayoutY(100);

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

            // Create a TableView to display students
            TableView<Student> tableView = new TableView<>();
            ObservableList<Student> studentData = FXCollections.observableArrayList(students);

            // Column for row numbers
            TableColumn<Student, String> numberCol = new TableColumn<>("#");
            numberCol.setCellValueFactory(cellData -> {
                int index = tableView.getItems().indexOf(cellData.getValue()) + 1;
                return new SimpleStringProperty(String.valueOf(index));
            });
            numberCol.setPrefWidth(30);
            numberCol.setStyle("-fx-alignment: CENTER;");

         // Username column
            TableColumn<Student, String> usernameCol = new TableColumn<>("Username");
            usernameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty("@" + cellData.getValue().getUsername()));
            usernameCol.setPrefWidth(250);
            usernameCol.setStyle("-fx-border-color: transparent;");

            // Name column (Last, First Middle)
            TableColumn<Student, String> nameCol = new TableColumn<>("Student Name (LN, FN, MN)");
            nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getLastName() + ", " +
                    cellData.getValue().getFirstName() + " " +
                    cellData.getValue().getMiddleName()
            ));
            nameCol.setPrefWidth(380);
            nameCol.setStyle("-fx-border-color: transparent;");

            // Year level column
            TableColumn<Student, String> yearLevelCol = new TableColumn<>("Year Level");
            yearLevelCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getYearLevel()));
            yearLevelCol.setPrefWidth(120);
            yearLevelCol.setStyle("-fx-border-color: transparent;");

            // ✅ Email column (NEW)
            TableColumn<Student, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
            emailCol.setPrefWidth(200);
            emailCol.setStyle("-fx-border-color: transparent;");

            // Table styling
            tableView.setPrefHeight(500);
            tableView.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");

            // Add all columns to TableView
            tableView.getColumns().addAll(numberCol, usernameCol, nameCol, yearLevelCol, emailCol);

            // Set data
            tableView.setItems(studentData);

            studentTable.getChildren().add(tableView);
        } else {
            Text noStudents = new Text("No students found for the selected course.");
            studentTable.getChildren().add(noStudents);
        }

        centerPanel.getChildren().add(studentTable);
    }


}
