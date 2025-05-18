package ticktocktrack.gui;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import ticktocktrack.database.DatabaseViewClassList;
import ticktocktrack.logic.Student;


public class TeacherAddStudent {
   

    public static boolean showAddStudentSelectionDialog(List<Student> allStudents, String courseName, String section, String program, int classId, int teacherId) {
         final int finalTeacherId = teacherId;
        
         
        Stage dialog = new Stage();
        dialog.setTitle("Enroll Students in: " + courseName);


        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));


        ComboBox<String> yearLevelComboBox = new ComboBox<>();
        yearLevelComboBox.getItems().addAll("All", "1st Year", "2nd Year", "3rd Year", "4th Year");
        yearLevelComboBox.setValue("All");


        ListView<Student> selectedListView = new ListView<>();
        selectedListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedListView.setPrefHeight(150);
       
     
       
       
        VBox studentsVBox = new VBox(10);
        studentsVBox.setPadding(new Insets(10));
        studentsVBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");


        Set<Integer> enrolledStudentIdsOriginal = DatabaseViewClassList.getEnrolledStudentIds(classId);
        Set<Integer> enrolledStudentIds = (enrolledStudentIdsOriginal == null) ? new HashSet<>() : new HashSet<>(enrolledStudentIdsOriginal);
       
     // Now the cell factory, capturing enrolledStudentIds and studentsVBox (make them effectively final)
        selectedListView.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            private final HBox content;
            private final Text nameText;
            private final Button removeBtn;


            {
                nameText = new Text();
                removeBtn = new Button("-");
                removeBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                removeBtn.setOnAction(e -> {
                    Student s = getItem();
                    if (s != null) {
                        selectedListView.getItems().remove(s);
                        // Refresh the student rows to reflect deselection
                        List<Student> filtered = filterStudents(allStudents, section, program, yearLevelComboBox.getValue(), enrolledStudentIds);
                        populateStudentRows(filtered, studentsVBox, selectedListView);
                    }
                });


                content = new HBox(10, nameText, removeBtn);
                content.setAlignment(Pos.CENTER_LEFT);
            }


            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setGraphic(null);
                } else {
                    nameText.setText(student.getFullName() + " [" + student.getUsername() + "]");
                    setGraphic(content);
                }
            }
        });
        List<Student> filteredStudents = filterStudents(allStudents, section, program, "All", enrolledStudentIds);
        populateStudentRows(filteredStudents, studentsVBox, selectedListView);


        Boolean[] enrollmentOccurred = new Boolean[] {false}; // ✅ FIXED: Mutable boolean wrapper
        Button filterBtn = new Button("Filter");
        filterBtn.setOnAction(e -> {
            String selectedYear = yearLevelComboBox.getValue();
            List<Student> filtered = filterStudents(allStudents, section, program, selectedYear, enrolledStudentIds);
            populateStudentRows(filtered, studentsVBox, selectedListView);
        });


        Button enrollBtn = new Button("Enroll Selected");
        enrollBtn.disableProperty().bind(Bindings.isEmpty(selectedListView.getItems()));


       
        final String finalCourseName = courseName;
        final String finalSection = section;
        final String finalProgram = program;
       


        enrollBtn.setOnAction(e -> {
            List<Student> selectedStudents = new ArrayList<>(selectedListView.getItems());
            if (selectedStudents.isEmpty()) return;


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Enrollment");
            alert.setHeaderText("You are about to enroll " + selectedStudents.size() + " student(s)");
            alert.setContentText("Do you want to proceed?");


            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    boolean success = DatabaseViewClassList.enrollStudents(classId, selectedStudents);
                    if (success) {
                        enrollmentOccurred[0] = true;
                        for (Student s : selectedStudents) {
                            enrolledStudentIds.add(s.getStudentId());
                        }
                        selectedListView.getItems().clear();


                        List<Student> filtered = filterStudents(allStudents, finalSection, finalProgram, yearLevelComboBox.getValue(), enrolledStudentIds);
                        populateStudentRows(filtered, studentsVBox, selectedListView);


                        TeacherViewClassStudents.refreshStudentTable(courseName, section, program, teacherId);

                     


                    } else {
                        System.err.println("Enrollment failed!");
                    }
                }
            });
        });


        layout.getChildren().addAll(
            new Label("Filter by Year Level:"),
            yearLevelComboBox,
            filterBtn,
            studentsVBox,
            new Label("Students to Enroll:"),
            selectedListView,
            enrollBtn
        );


        Scene scene = new Scene(layout, 500, 650);
        dialog.setScene(scene);
        dialog.showAndWait();


        return enrollmentOccurred[0]; // ✅ Will return true if enrollment succeeded
    }




    private static List<Student> filterStudents(List<Student> students, String section, String program, String yearLevel, Set<Integer> enrolledIds) {
        return students.stream()
            .filter(s -> !enrolledIds.contains(s.getStudentId()))
            .filter(s -> program.equals("All") || s.getProgram().equalsIgnoreCase(program))
            .filter(s -> section.equals("All") || s.getSection().equalsIgnoreCase(section))
            .filter(s -> yearLevel.equals("All") || s.getYearLevel().equalsIgnoreCase(yearLevel))
            .collect(Collectors.toList());
    }


    private static void populateStudentRows(List<Student> students, VBox container, ListView<Student> selectedListView) {
        container.getChildren().clear();
        for (Student student : students) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);


            String display = student.getFullName() + " [" + student.getUsername() + "] | Year: " + student.getYearLevel() + " | Section: " + student.getSection();
            Text info = new Text(display);
            info.setWrappingWidth(400);


            Button toggleBtn = new Button();
            updateToggleButtonLabel(toggleBtn, selectedListView.getItems().contains(student));
            toggleBtn.setStyle("-fx-background-color: #0097A7; -fx-text-fill: white;");


            toggleBtn.setOnAction(e -> {
                if (selectedListView.getItems().contains(student)) {
                    selectedListView.getItems().remove(student);
                } else {
                    selectedListView.getItems().add(student);
                }
                updateToggleButtonLabel(toggleBtn, selectedListView.getItems().contains(student));
            });


            row.getChildren().addAll(toggleBtn, info);
            container.getChildren().add(row);
        }
    }


    private static void updateToggleButtonLabel(Button btn, boolean isSelected) {
        btn.setText(isSelected ? "-" : "+");
    }




}



