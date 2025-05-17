package ticktocktrack.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ticktocktrack.database.DatabaseViewClassList;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.ViewClassList;

import java.util.List;

public class TeacherViewClassStudents {
	
	private final Pane centerPanel;

    public TeacherViewClassStudents(String courseName, String section, String program, int teacherId) {
    	centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: #F0F8FF; -fx-border-color: transparent;");

        String shadowPath = TeacherViewClassStudents.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutY(-115);
        centerPanel.getChildren().add(shadowView);

        addTitle(courseName, section, program);
        addStudentTable(courseName, section, program, teacherId); ;
    }

    public Pane getView() {
        return centerPanel;
    }

    private void addTitle(String courseName, String section, String program) {
        String programShort = (program != null) ? ViewClassList.mapProgramToShortName(program) : "";

        String fullTitle = "Students for " + courseName + "\n";
        if (!programShort.isEmpty() && section != null) {
            fullTitle += programShort + " - Section " + section;
        } else if (section != null) {
            fullTitle += "Section " + section;
        }

        Text title = new Text(fullTitle);
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 28));
        title.setFill(Color.web("#02383E"));
        title.setLayoutX(30);
        title.setLayoutY(50);

        TextField searchField = new TextField();
        searchField.setPromptText("Search Student");
        searchField.setPrefWidth(215);
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 8 12; -fx-background-radius: 20px;");
        searchField.setLayoutX(620);
        searchField.setLayoutY(42);

        Button addStudentBtn = new Button("+ Add Student");
        addStudentBtn.setStyle("-fx-background-color: #5D9CA2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8;");
        addStudentBtn.setPrefSize(140, 40);
        addStudentBtn.setLayoutX(860);
        addStudentBtn.setLayoutY(40);
        addStudentBtn.setCursor(javafx.scene.Cursor.HAND);

        centerPanel.getChildren().addAll(title, searchField, addStudentBtn);
    }

    private void addStudentTable(String courseName, String section, String program, int teacherId) {
        VBox studentTable = new VBox(10);
        studentTable.setLayoutX(30);
        studentTable.setLayoutY(100);

        // Get students for this specific class and teacher
        List<Student> students = DatabaseViewClassList.getStudentsEnrolledForTeacher(courseName, section, program, teacherId);

        if (!students.isEmpty()) {
            ObservableList<Student> studentData = FXCollections.observableArrayList(students);

            TableView<Student> tableView = new TableView<>();

            TableColumn<Student, String> numberCol = new TableColumn<>("#");
            numberCol.setCellValueFactory(cellData -> {
                int index = tableView.getItems().indexOf(cellData.getValue()) + 1;
                return new SimpleStringProperty(String.valueOf(index));
            });
            numberCol.setPrefWidth(30);
            numberCol.setStyle("-fx-alignment: CENTER;");

            TableColumn<Student, String> usernameCol = new TableColumn<>("Username");
            usernameCol.setCellValueFactory(cellData -> new SimpleStringProperty("@" + cellData.getValue().getUsername()));
            usernameCol.setPrefWidth(250);

            TableColumn<Student, String> nameCol = new TableColumn<>("Student Name (LN, FN, MN)");
            nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getLastName() + ", " +
                cellData.getValue().getFirstName() + " " +
                cellData.getValue().getMiddleName()
            ));
            nameCol.setPrefWidth(380);

            TableColumn<Student, String> yearLevelCol = new TableColumn<>("Year Level");
            yearLevelCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getYearLevel()));
            yearLevelCol.setPrefWidth(120);

            TableColumn<Student, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
            emailCol.setPrefWidth(200);

            tableView.getColumns().addAll(numberCol, usernameCol, nameCol, yearLevelCol, emailCol);
            tableView.setItems(studentData);
            tableView.setPrefHeight(500);

            studentTable.getChildren().add(tableView);
        } else {
            studentTable.getChildren().add(new Text("No students found for the selected course."));
        }

        centerPanel.getChildren().add(studentTable);
    }
}
