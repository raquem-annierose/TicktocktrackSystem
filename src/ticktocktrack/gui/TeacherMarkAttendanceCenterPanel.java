package ticktocktrack.gui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.*;
import javafx.scene.image.*;

public class TeacherMarkAttendanceCenterPanel {
    public static BorderPane createPanel() {
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

        ObservableList<Student> students = FXCollections.observableArrayList(
  
        );

        FilteredList<Student> filteredStudents = new FilteredList<>(students, p -> true);

        VBox centerVBox = new VBox(10);
        centerVBox.setPadding(new Insets(20, 50, 20, 50));

        HBox searchCourseBox = new HBox(473);
        searchCourseBox.setPadding(new Insets(1, 0, 10, 4));
        searchCourseBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search Student");
        searchField.setPrefWidth(350);
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 20px;");

        ComboBox<String> courseComboBox = new ComboBox<>();
        courseComboBox.getItems().addAll("BSIT 2-1", "BSIT 2-2", "BSIT 3-3");
        courseComboBox.setPrefWidth(120); // made thinner
        courseComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        courseComboBox.getSelectionModel().selectFirst();

        searchCourseBox.getChildren().addAll(searchField, courseComboBox);



        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase();
            filteredStudents.setPredicate(student -> {
                if (filter.isEmpty()) return true;
                return student.getStudentId().toLowerCase().contains(filter) ||
                       student.getFullName().toLowerCase().contains(filter);
            });
        });

        TableView<Student> table = new TableView<>();
        table.setPrefWidth(950);
        table.setMaxWidth(950);
        table.setItems(filteredStudents);
        table.setEditable(true);
        table.setStyle("-fx-font-size: 14px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Student, String> numberCol = new TableColumn<>("#");
        numberCol.setPrefWidth(50);
        numberCol.setCellValueFactory(cellData ->
            new ReadOnlyStringWrapper(String.valueOf(students.indexOf(cellData.getValue()) + 1)));

        TableColumn<Student, String> idCol = new TableColumn<>("Student ID");
        idCol.setPrefWidth(150);
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Student, String> nameCol = new TableColumn<>("Last Name, First Name, Middle Name");
        nameCol.setPrefWidth(350);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<Student, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(150);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Student, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(150);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(ComboBoxTableCell.forTableColumn("Present", "Absent", "Late"));

        table.getColumns().addAll(numberCol, idCol, nameCol, dateCol, statusCol);

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefSize(950, 250);
        scrollPane.setMaxSize(950, 250);

        centerVBox.getChildren().addAll(searchCourseBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainPane.setCenter(centerVBox);
        return mainPane;
    }

    public static class Student {
        private final BooleanProperty selected;
        private final StringProperty studentId;
        private final StringProperty fullName;
        private final StringProperty date;
        private final StringProperty status;

        public Student(boolean selected, String studentId, String fullName, String date, String status) {
            this.selected = new SimpleBooleanProperty(selected);
            this.studentId = new SimpleStringProperty(studentId);
            this.fullName = new SimpleStringProperty(fullName);
            this.date = new SimpleStringProperty(date);
            this.status = new SimpleStringProperty(status);
        }

        public BooleanProperty selectedProperty() { return selected; }
        public String getStudentId() { return studentId.get(); }
        public String getFullName() { return fullName.get(); }
        public String getDate() { return date.get(); }
        public String getStatus() { return status.get(); }
    }
}
