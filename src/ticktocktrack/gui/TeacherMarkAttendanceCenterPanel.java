package ticktocktrack.gui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import ticktocktrack.database.DatabaseAttendance;
import ticktocktrack.database.DatabaseViewClassList;

import java.util.*;

public class TeacherMarkAttendanceCenterPanel {

    public static BorderPane createPanel() {
        return createPanel(null, null);
    }

    public static BorderPane createPanel(String defaultCourse, String defaultSection) {
        BorderPane mainPane = new BorderPane();
        mainPane.setPrefSize(1300, 750);
        mainPane.setStyle("-fx-background-color: white;");

        try {
            String shadowPath = TeacherMarkAttendanceCenterPanel.class
                    .getResource("/resources/SHADOW.png").toExternalForm();
            ImageView shadowView = new ImageView(new Image(shadowPath));
            shadowView.setFitWidth(1300);
            shadowView.setFitHeight(250);
            shadowView.setLayoutY(-115);
            mainPane.getChildren().add(shadowView);
        } catch (Exception e) {
            System.out.println("Shadow image not found or error loading");
        }

        Map<String, Set<String>> courseSectionsMap = new LinkedHashMap<>();
        List<String[]> courses = DatabaseViewClassList.getCourses();

        for (String[] courseInfo : courses) {
            String courseName = courseInfo[0];
            String section = courseInfo[1];
            courseSectionsMap.putIfAbsent(courseName, new LinkedHashSet<>());
            courseSectionsMap.get(courseName).add(section);
        }

        ObservableList<Student> students = FXCollections.observableArrayList();
        FilteredList<Student> filteredStudents = new FilteredList<>(students, p -> true);

        VBox centerVBox = new VBox(10);
        centerVBox.setPadding(new Insets(20, 50, 20, 50));

        HBox searchCourseBox = new HBox(20);
        searchCourseBox.setPadding(new Insets(1, 0, 10, 4));
        searchCourseBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search Student");
        searchField.setPrefWidth(350);
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 20px;");

        ComboBox<String> courseComboBox = new ComboBox<>();
        courseComboBox.setPrefWidth(200);
        courseComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        courseComboBox.getItems().addAll(courseSectionsMap.keySet());

        ComboBox<String> sectionComboBox = new ComboBox<>();
        sectionComboBox.setPrefWidth(120);
        sectionComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 8;");

        // Save Attendance Button
        Button saveButton = new Button("Save Attendance");
        saveButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20 10 20;");
        saveButton.setOnAction(e -> {
            try {
                for (Student student : students) {
                    DatabaseAttendance.saveAttendance(
                        student.getStudentId(),
                        student.getDate(),
                        student.getStatus(),
                        student.getReason()
                    );
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Attendance saved successfully.");
                alert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to save attendance");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchCourseBox.getChildren().addAll(searchField, courseComboBox, sectionComboBox, spacer, saveButton);

        courseComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldCourse, newCourse) -> {
            sectionComboBox.getItems().clear();
            if (newCourse != null) {
                Set<String> sections = courseSectionsMap.get(newCourse);
                if (sections != null) {
                    sectionComboBox.getItems().addAll(sections);
                    sectionComboBox.getSelectionModel().selectFirst();
                }
            }
            loadStudentsBasedOnSelection(courseComboBox, sectionComboBox, students);
        });

        sectionComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSection, newSection) -> {
            loadStudentsBasedOnSelection(courseComboBox, sectionComboBox, students);
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredStudents.setPredicate(student -> {
                if (filter.isEmpty()) return true;

                return student.getStudentId().toLowerCase().contains(filter) ||
                        student.getLastName().toLowerCase().contains(filter) ||
                        student.getFirstName().toLowerCase().contains(filter) ||
                        student.getMiddleName().toLowerCase().contains(filter);
            });
        });

        TableView<Student> table = new TableView<>();
        table.setPrefWidth(950);
        table.setMaxWidth(950);
        table.setItems(filteredStudents);
        table.setEditable(true);
        table.setStyle("-fx-font-size: 14px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Student, String> idCol = new TableColumn<>("Student ID");
        idCol.setPrefWidth(50);
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Student, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setPrefWidth(150);
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Student, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setPrefWidth(150);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Student, String> middleNameCol = new TableColumn<>("Middle Name");
        middleNameCol.setPrefWidth(150);
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));

        TableColumn<Student, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(100);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Student, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(ComboBoxTableCell.forTableColumn("Present", "Absent", "Late", "Pending"));

        TableColumn<Student, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setPrefWidth(250);
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reasonCol.setCellFactory(TextFieldTableCell.forTableColumn());
        reasonCol.setOnEditCommit(event -> {
            Student student = event.getRowValue();
            student.setReason(event.getNewValue());
        });

        table.getColumns().addAll(idCol, lastNameCol, firstNameCol, middleNameCol, dateCol, statusCol, reasonCol);

        ScrollPane sp = new ScrollPane(table);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setPrefSize(950, 250);

        centerVBox.getChildren().addAll(searchCourseBox, sp);
        VBox.setVgrow(sp, Priority.ALWAYS);
        mainPane.setCenter(centerVBox);

        if (defaultCourse != null && courseSectionsMap.containsKey(defaultCourse)) {
            courseComboBox.getSelectionModel().select(defaultCourse);
            if (defaultSection != null && courseSectionsMap.get(defaultCourse).contains(defaultSection)) {
                sectionComboBox.getSelectionModel().select(defaultSection);
            }
        } else if (!courseSectionsMap.isEmpty()) {
            String firstCourse = courseSectionsMap.keySet().iterator().next();
            courseComboBox.getSelectionModel().select(firstCourse);
            Set<String> firstSections = courseSectionsMap.get(firstCourse);
            if (firstSections != null && !firstSections.isEmpty()) {
                sectionComboBox.getSelectionModel().select(firstSections.iterator().next());
            }
        }

        return mainPane;
    }

    private static void loadStudentsBasedOnSelection(ComboBox<String> courseComboBox,
                                                     ComboBox<String> sectionComboBox,
                                                     ObservableList<Student> students) {
        String selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        String selectedSection = sectionComboBox.getSelectionModel().getSelectedItem();

        if (selectedCourse != null && selectedSection != null) {
            System.out.println("Fetching students for course: " + selectedCourse + ", section: " + selectedSection);
            loadStudents(selectedCourse, selectedSection, students);
        } else {
            students.clear();
        }
    }

    private static void loadStudents(String courseName, String section, ObservableList<Student> students) {
        students.clear();
        try {
            students.addAll(DatabaseAttendance.fetchStudents(courseName, section));
            System.out.println("Loaded students: " + students.size());
        } catch (Exception e) {
            System.out.println("Error loading students: " + e.getMessage());
        }
    }

    public static class Student {
        private final BooleanProperty selected;
        private final StringProperty studentId;
        private final StringProperty lastName;
        private final StringProperty firstName;
        private final StringProperty middleName;
        private final StringProperty date;
        private final StringProperty status;
        private final StringProperty reason;

        public Student(boolean selected, String studentId, String lastName, String firstName,
                       String middleName, String date, String status, String reason) {
            this.selected = new SimpleBooleanProperty(selected);
            this.studentId = new SimpleStringProperty(studentId);
            this.lastName = new SimpleStringProperty(lastName);
            this.firstName = new SimpleStringProperty(firstName);
            this.middleName = new SimpleStringProperty(middleName);
            this.date = new SimpleStringProperty(date);
            this.status = new SimpleStringProperty(status);
            this.reason = new SimpleStringProperty(reason);
        }

        public BooleanProperty selectedProperty() { return selected; }
        public String getStudentId() { return studentId.get(); }
        public StringProperty studentIdProperty() { return studentId; }
        public String getLastName() { return lastName.get(); }
        public StringProperty lastNameProperty() { return lastName; }
        public String getFirstName() { return firstName.get(); }
        public StringProperty firstNameProperty() { return firstName; }
        public String getMiddleName() { return middleName.get(); }
        public StringProperty middleNameProperty() { return middleName; }
        public String getDate() { return date.get(); }
        public StringProperty dateProperty() { return date; }
        public String getStatus() { return status.get(); }
        public StringProperty statusProperty() { return status; }
        public String getReason() { return reason.get(); }
        public StringProperty reasonProperty() { return reason; }

        public void setStatus(String status) { this.status.set(status); }
        public void setReason(String reason) { this.reason.set(reason); }
    }
}
