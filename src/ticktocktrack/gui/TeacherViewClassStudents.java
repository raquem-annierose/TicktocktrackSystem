package ticktocktrack.gui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeacherViewClassStudents {

    private final Pane centerPanel;
    private static Button trashButton;
    private static TableView<Student> studentTableView;  // Make it a field for refresh

    private final static Map<Student, SimpleBooleanProperty> selectedMap = new HashMap<>();
    private ObservableList<Student> studentData;

    private String courseName;
    private String section;
    private String program;
    private int teacherId;

    public TeacherViewClassStudents(String courseName, String section, String program, int teacherId) {
        this.courseName = courseName;
        this.section = section;
        this.program = program;
        this.teacherId = teacherId;

        centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: #F0F8FF; -fx-border-color: transparent;");

        String shadowPath = TeacherViewClassStudents.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutY(-115);
        centerPanel.getChildren().add(shadowView);

        addTitle(courseName, section, program, teacherId);
        addStudentTable(courseName, section, program, teacherId);

    }

    public Pane getView() {
        return centerPanel;
    }

    private void addTitle(String courseName, String section, String program, int teacherId) {
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
        searchField.setLayoutX(580);
        searchField.setLayoutY(42);

        Button addStudentBtn = new Button("+ Add Student");
        addStudentBtn.setStyle("-fx-background-color: #5D9CA2; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8;");
        addStudentBtn.setPrefSize(140, 40);
        addStudentBtn.setLayoutX(820);
        addStudentBtn.setLayoutY(40);
        addStudentBtn.setCursor(javafx.scene.Cursor.HAND);
        
        addStudentBtn.setOnAction(e -> {
            int classId = DatabaseViewClassList.getClassId(courseName, section, program, teacherId);
            if (classId == -1) {
                System.err.println("Invalid class ID for course/section/program/teacher");
                return;
            }
            List<Student> students = DatabaseViewClassList.getUnenrolledStudentsWithEnrollmentId(classId);
            if (students == null) {
                System.err.println("Failed to fetch unenrolled students for class ID: " + classId);
                return;
            }
            // Corrected parameter order here:
            boolean success = TeacherAddStudent.showAddStudentSelectionDialog(students, courseName, section, program, classId, teacherId);


            if (success) {
                refreshStudentTable(courseName, section, program, teacherId);
            }
        });


        Image trashImage = new Image("/resources/trashbin_icon.png");
        ImageView trashIcon = new ImageView(trashImage);
        trashIcon.setFitWidth(24);
        trashIcon.setFitHeight(24);
        trashButton = new Button();
        trashButton.setGraphic(trashIcon);
        trashButton.setVisible(false);
        trashButton.setStyle("-fx-background-color: transparent;");
        trashButton.setCursor(javafx.scene.Cursor.HAND);
        trashButton.setLayoutX(970);
        trashButton.setLayoutY(45);

        // Handle student unenrollment
        trashButton.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText("Unenroll Selected Students");
            confirmAlert.setContentText("Are you sure you want to unenroll the selected students from this class?");

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    List<Student> toUnenroll = selectedMap.entrySet().stream()
                            .filter(entry -> entry.getValue().get())
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

                    // Unenroll students from Enrollments table
                    DatabaseViewClassList.unenrollStudentsFromClass(toUnenroll, courseName, section, program, teacherId);

                    studentData.removeAll(toUnenroll);
                    toUnenroll.forEach(selectedMap::remove);
                    trashButton.setVisible(false);
                    
                    refreshStudentTable(courseName, section, program, teacherId);
                }
            });
        });

        centerPanel.getChildren().addAll(title, searchField, addStudentBtn, trashButton);
    }

    private void addStudentTable(String courseName, String section, String program, int teacherId) {
        VBox studentTable = new VBox(10);
        studentTable.setLayoutX(30);
        studentTable.setLayoutY(100);

        List<Student> students = DatabaseViewClassList.getStudentsEnrolledForTeacher(courseName, section, program, teacherId);
        
        studentTableView = new TableView<>();  
        
        if (!students.isEmpty()) {
        	studentTableView = new TableView<>();

            studentData = FXCollections.observableArrayList(students);
            studentTableView = new TableView<>(); 
            studentTableView.setEditable(true);


            for (Student s : studentData) {
                selectedMap.put(s, new SimpleBooleanProperty(false));
            }

            TableColumn<Student, Boolean> selectCol = new TableColumn<>();
            selectCol.setPrefWidth(30);
            selectCol.setStyle("-fx-alignment: CENTER;");
            selectCol.setCellValueFactory(cellData -> selectedMap.get(cellData.getValue()));
            selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
            selectCol.setEditable(true);

            TableColumn<Student, String> numberCol = new TableColumn<>("#");
            numberCol.setCellValueFactory(cellData -> {
            	int index = studentTableView.getItems().indexOf(cellData.getValue()) + 1;

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
            nameCol.setPrefWidth(350);

            TableColumn<Student, String> yearLevelCol = new TableColumn<>("Year Level");
            yearLevelCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getYearLevel()));
            yearLevelCol.setPrefWidth(120);

            TableColumn<Student, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
            emailCol.setPrefWidth(200);

            for (SimpleBooleanProperty prop : selectedMap.values()) {
                prop.addListener((obs, oldVal, newVal) -> {
                    boolean anySelected = selectedMap.values().stream().anyMatch(SimpleBooleanProperty::get);
                    trashButton.setVisible(anySelected);
                });
            }

            studentTableView.getColumns().addAll(selectCol, numberCol, usernameCol, nameCol, yearLevelCol, emailCol);

            studentTableView.setItems(studentData);
            studentTableView.setPrefHeight(500);


            studentTable.getChildren().add(studentTableView);

        } else {
            studentTable.getChildren().add(new Text("No students found for the selected course."));
        }

        centerPanel.getChildren().add( studentTable);
    }
    
    public static void refreshStudentTable(String courseName, String section, String program, int teacherId) {
        List<Student> students = DatabaseViewClassList.getStudentsEnrolledForTeacher(courseName, section, program, teacherId);
        ObservableList<Student> updatedStudentData = FXCollections.observableArrayList(students);

        if (studentTableView != null) {
            studentTableView.setItems(updatedStudentData);

            
            // Clear and repopulate selectedMap
            selectedMap.clear();
            for (Student s : updatedStudentData) {
                selectedMap.put(s, new SimpleBooleanProperty(false));
            }

            // Re-add listeners to each checkbox
            for (SimpleBooleanProperty prop : selectedMap.values()) {
                prop.addListener((obs, oldVal, newVal) -> {
                    boolean anySelected = selectedMap.values().stream().anyMatch(SimpleBooleanProperty::get);
                    trashButton.setVisible(anySelected);
                });
            }
        } else {
            System.err.println("studentTableView is null - cannot refresh.");
        }
    }



}
