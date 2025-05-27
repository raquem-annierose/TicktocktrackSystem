package ticktocktrack.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import ticktocktrack.database.DatabaseAttendance;
import ticktocktrack.database.DatabaseViewClassList;
import ticktocktrack.logic.CourseInfo;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.UsersModel;

import ticktocktrack.database.StudentNotificationDAO;

import java.time.LocalDate;
import java.util.*;

/**
 * The {@code TeacherMarkAttendanceCenterPanel} class is responsible for creating
 * and managing the center panel used by teachers to mark attendance. It provides
 * a user interface for selecting courses, sections, and programs, and marking attendance
 * for students within those categories.
 */
public class TeacherMarkAttendanceCenterPanel {

    /**
     * Tracks the last refresh date for the attendance panel to optimize performance
     * and avoid unnecessary reloading.
     */
    private static String lastRefreshDate = "";

    /**
     * Tracks the last selected course to persist user preferences between interactions.
     */
    private static String lastSelectedCourse = null;

    /**
     * Tracks the last selected section to persist user preferences between interactions.
     */
    private static String lastSelectedSection = null;

    /**
     * Tracks the last selected program to persist user preferences between interactions.
     */
    private static String lastSelectedProgram = null;

    /**
     * Creates and returns a {@code BorderPane} that serves as the main user interface
     * for marking attendance. The panel includes sections for course, section, and
     * program selection, as well as a list of students and controls for marking attendance.
     *
     * @param defaultCourse   The default course to select when the panel is loaded.
     * @param defaultSection  The default section to select when the panel is loaded.
     * @param teacherId       The ID of the teacher using this panel.
     * @return A {@code BorderPane} containing the attendance marking UI components.
     */
    @SuppressWarnings("unchecked")
    public static BorderPane createPanel(String defaultCourse, String defaultSection, int teacherId) {
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

        List<String> programSectionList = new ArrayList<>();
        Map<String, Set<String>> courseSectionsMap = new LinkedHashMap<>();

        CourseInfo[] courses = DatabaseAttendance.getCoursesForTeacher(teacherId);
        if (courses != null) {
            for (CourseInfo courseInfo : courses) {
                String courseName = courseInfo.courseName;
                String section = courseInfo.section;
                String program = courseInfo.program;

                courseSectionsMap.putIfAbsent(courseName, new LinkedHashSet<>());
                String combined = section + " - " + program;
                courseSectionsMap.get(courseName).add(combined);
            }
        } else {
            System.out.println("No courses found for teacher ID: " + teacherId);
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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button saveAttendanceButton = new Button("Save Attendance");
        saveAttendanceButton.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-padding: 8 20; " +
            "-fx-background-radius: 15; " +
            "-fx-background-color: #5A7D9A; " +  // Whale color
            "-fx-text-fill: white; " +
            "-fx-border-color: transparent; " +  // Remove red border
            "-fx-border-width: 0px; " +
            "-fx-focus-color: transparent; " +   // Removes orange focus
            "-fx-faint-focus-color: transparent;" // Removes faint focus glow
        );

        // Add spacing to left
        HBox.setMargin(saveAttendanceButton, new Insets(0, 0, 0, 10));

        // Hover effect
        saveAttendanceButton.setOnMouseEntered(e -> {
            saveAttendanceButton.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-padding: 8 20; " +
                "-fx-background-radius: 15; " +
                "-fx-background-color: #6F91A8; " +  // Slightly lighter whale on hover
                "-fx-text-fill: white; " +
                "-fx-border-color: transparent; " +
                "-fx-border-width: 0px; " +
                "-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;"
            );
        });

        saveAttendanceButton.setOnMouseExited(e -> {
            saveAttendanceButton.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-padding: 8 20; " +
                "-fx-background-radius: 15; " +
                "-fx-background-color: #5A7D9A; " +  // Original whale color
                "-fx-text-fill: white; " +
                "-fx-border-color: transparent; " +
                "-fx-border-width: 0px; " +
                "-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;"
            );
        });

        // Button action
        saveAttendanceButton.setOnAction(e -> {
            saveAttendance(students, courseComboBox.getValue(), sectionComboBox.getValue());
        });


        searchCourseBox.getChildren().addAll(searchField, courseComboBox, sectionComboBox, saveAttendanceButton, spacer);


        courseComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
           
            if (newVal != null) {
                Set<String> combinedSections = courseSectionsMap.get(newVal);
                sectionComboBox.getItems().setAll(combinedSections);
                if (!combinedSections.isEmpty()) {
                    String firstSection = combinedSections.iterator().next();
                    sectionComboBox.getSelectionModel().select(firstSection);
                    sectionComboBox.setValue(firstSection);  // <-- explicitly set value here
                } else {
                    sectionComboBox.setValue(null);
                }

            }
            loadStudentsBasedOnSelection(courseComboBox, sectionComboBox, students);
        });

        sectionComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadStudentsBasedOnSelection(courseComboBox, sectionComboBox, students);
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredStudents.setPredicate(student -> {
                if (filter.isEmpty()) return true;

                String studentIdStr = String.valueOf(student.getStudentId());
                return studentIdStr.toLowerCase().contains(filter)
                        || (student.getLastName() != null && student.getLastName().toLowerCase().contains(filter))
                        || (student.getFirstName() != null && student.getFirstName().toLowerCase().contains(filter))
                        || (student.getMiddleName() != null && student.getMiddleName().toLowerCase().contains(filter));
            });

        });

        TableView<Student> table = new TableView<>();
        table.setMaxWidth(1000);
        table.setItems(filteredStudents);
        table.setEditable(true);

        table.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-color: transparent;" +     // Makes table background transparent
            "-fx-text-fill: white;"                     // This affects selected text, but not all cells
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getColumns().addAll(
            createColumn("Student ID", "studentId"),
            createColumn("Last Name", "lastName"),
            createColumn("First Name", "firstName"),
            createColumn("Middle Name", "middleName"),
            createColumn("Date", "date"),
            createEditableStatusColumn()
        );

        // Style the header and header text
        Platform.runLater(() -> {
            Node header = table.lookup("TableHeaderRow");
            if (header != null) {
                header.setStyle("-fx-background-color: #2a72d9;");  // header background
            }

            Set<Node> columnHeaders = table.lookupAll(".column-header");
            for (Node colHeader : columnHeaders) {
                colHeader.setStyle("-fx-background-color: #336699; -fx-border-color: transparent;");

                Node label = colHeader.lookup(".label");
                if (label != null) {
                    label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");  // white header text
                }
            }
        });
        
        ScrollPane sp = new ScrollPane(table);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setMaxWidth(950);
     

        centerVBox.getChildren().addAll(searchCourseBox, sp);
        VBox.setVgrow(sp, Priority.ALWAYS);
        mainPane.setCenter(centerVBox);

        lastRefreshDate = java.time.LocalDate.now().toString();

        Timeline dailyClearTimer = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> {
                    String today = java.time.LocalDate.now().toString();
                    if (!today.equals(lastRefreshDate)) {
                        Platform.runLater(() -> {
                            students.clear();
                            if (lastSelectedCourse != null && lastSelectedSection != null) {
                            	loadStudents(lastSelectedCourse, lastSelectedProgram, lastSelectedSection, students); // ✅

                            }
                            lastRefreshDate = today;
                        });
                    }
                })
        );
        dailyClearTimer.setCycleCount(Timeline.INDEFINITE);
        dailyClearTimer.play();

        if (defaultCourse != null && courseSectionsMap.containsKey(defaultCourse)) {
            courseComboBox.getSelectionModel().select(defaultCourse);
            if (defaultSection != null && courseSectionsMap.get(defaultCourse).contains(defaultSection)) {
                sectionComboBox.getSelectionModel().select(defaultSection);
            }
        } else if (!courseSectionsMap.isEmpty()) {
            String firstCourse = courseSectionsMap.keySet().iterator().next();
            courseComboBox.getSelectionModel().select(firstCourse);
            Set<String> sections = courseSectionsMap.get(firstCourse);
            if (sections != null && !sections.isEmpty()) {
                String firstSectionProgram = sections.iterator().next();
                sectionComboBox.getItems().setAll(sections);
                sectionComboBox.getSelectionModel().select(firstSectionProgram);
            }
        }

        return mainPane;
    }
    
    /**
     * Creates a table column with the specified title and property mapping.
     * This method is a utility for generating generic, read-only table columns.
     *
     * @param title    The title of the column, displayed in the table header.
     * @param property The property of the {@code Student} object that this column maps to.
     * @return A {@code TableColumn} configured to display the specified property.
     */
    private static TableColumn<Student, String> createColumn(String title, String property) {
        TableColumn<Student, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        return col;
    }

    /**
     * Creates a table column for editing attendance status. The column allows
     * selecting predefined attendance statuses from a dropdown menu.
     *
     * @return A {@code TableColumn} configured for editable attendance status values.
     */
    private static TableColumn<Student, String> createEditableStatusColumn() {
        TableColumn<Student, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        ObservableList<String> statusOptions = FXCollections.observableArrayList(
            "Present", "Excused", "Late", "Absent", "Pending"
        	);
        // Use ComboBoxTableCell to allow selection from predefined statuses
        statusCol.setCellFactory(ComboBoxTableCell.forTableColumn(statusOptions));
        statusCol.setEditable(true);

        // When edit is committed, update the Student object's status property
        statusCol.setOnEditCommit(event -> {
            Student student = event.getRowValue();
            String newStatus = event.getNewValue();
            student.setStatus(newStatus);
            // Optionally, refresh the table or persist the change immediately if you want
        });

        return statusCol;
    }
    
    /**
     * Loads the list of students based on the selected course and section.
     * Updates the provided list of students to reflect the changes.
     *
     * @param courseComboBox   The {@code ComboBox} containing the list of available courses.
     * @param sectionComboBox  The {@code ComboBox} containing the list of available sections.
     * @param students         The {@code ObservableList} of students to be updated with filtered data.
     */
    private static void loadStudentsBasedOnSelection(ComboBox<String> courseComboBox,
            ComboBox<String> sectionComboBox,
            ObservableList<Student> students) {

    		String selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
    		String selectedCombined = sectionComboBox.getSelectionModel().getSelectedItem();

    		System.out.println("loadStudentsBasedOnSelection called with: ");
    		System.out.println("Selected course: " + selectedCourse);
    		System.out.println("Selected combined: " + selectedCombined);

    		if (selectedCombined != null && selectedCourse != null) {
    			// Split only on the first " - "
    			String[] parts = selectedCombined.split(" - ", 2);
    			if (parts.length == 2) {
    				String section = parts[0].trim();
    				String program = parts[1].trim();

    				if (!program.equals(selectedCourse)) {
    					System.out.println("Warning: program '" + program + "' does not match selected course '" + selectedCourse + "'");
    					// Optional: handle mismatch if needed
    				}

    				lastSelectedCourse = selectedCourse;
    				lastSelectedProgram = program;
    				lastSelectedSection = section;

    				loadStudents(selectedCourse, program, section, students);
    				return;
    			} else {
    				System.err.println("Invalid combined format after split: " + selectedCombined);
    			}
    		}
    		students.clear();
    }

    /**
     * Loads students enrolled in a specific course, program, and section into the provided list.
     * Updates each student's attendance status for the current day.
     *
     * @param course    The name of the course for which to load students.
     * @param program   The program name used for filtering students.
     * @param section   The section name used for filtering students.
     * @param students  The {@code ObservableList} to populate with the loaded student data.
     */
    private static void loadStudents(String course, String program, String section, ObservableList<Student> students) {
        students.clear();
        List<Student> loadedStudents = DatabaseAttendance.getStudentsEnrolled(course, program, section);
        String today = java.time.LocalDate.now().toString();

        for (Student s : loadedStudents) {
            s.setDate(today);

            // Get attendance status for today
            String attendanceStatus = DatabaseAttendance.getAttendanceStatus(
                s.getStudentId(), course, program, section, today
            );

            s.setStatus(attendanceStatus); // Set actual or "Pending"
            students.add(s);
        }
    }

    /**
     * Saves the attendance records for the provided list of students.
     * The attendance is associated with a specific course and section.
     *
     * @param students        The {@code ObservableList} of students whose attendance will be saved.
     * @param course          The name of the course for which attendance is being saved.
     * @param combinedSection The combined section information used to identify the group of students.
     */
    private static void saveAttendance(ObservableList<Student> students, String course, String combinedSection) {
        if (combinedSection == null || course == null) return;

        // Split combinedSection into section and program
        String[] parts;
        if (combinedSection.contains(" - ")) {
            parts = combinedSection.split(" - ", 2);
        } else if (combinedSection.contains(" – ")) {
            parts = combinedSection.split(" – ", 2);
        } else if (combinedSection.contains(" — ")) {
            parts = combinedSection.split(" — ", 2);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid section format: " + combinedSection);
            alert.showAndWait();
            return;
        }


        if (parts.length != 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid section format: " + combinedSection);
            alert.showAndWait();
            return;
        }

        String sectionFromSection = parts[0].trim();  // e.g. "1"
        String programFromSection = parts[1].trim();  // e.g. "BSIT – BS in Information Technology"

        try {
            for (Student student : students) {
                String studentProgram = student.getProgram() != null && !student.getProgram().isEmpty()
                                        ? student.getProgram()
                                        : programFromSection;

                String studentSection = student.getSection() != null && !student.getSection().isEmpty()
                                        ? student.getSection()
                                        : sectionFromSection;

                if (studentProgram.isEmpty() || studentSection.isEmpty()) {
                    System.err.println("Skipping student " + student.getStudentId() + " due to missing program or section");
                    continue;
                }

                int studentId = student.getStudentId();
                
                String date = student.getDate();
                String status = student.getStatus();

                int attendanceId = DatabaseAttendance.saveAttendance(
                    studentId,
                    date,
                    status,
                    student.getReason(),
                    studentProgram,
                    course,
                    studentSection
                );

                DatabaseAttendance.saveAttendance(
                    studentId,
                    student.getDate(),
                    student.getStatus(),
                    student.getReason(),
                    studentProgram,
                    course,
                    studentSection
                    
                );
                DatabaseAttendance.updateStudentAttendance(
                	    student.getStudentId(),
                	    student.getDate(),
                	    student.getStatus(),
                	    studentProgram,
                	    course,
                	    studentSection
                	);

                StudentNotificationDAO.sendAttendanceNotification(studentId, status, attendanceId, LocalDate.parse(date), course);


            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Attendance saved successfully.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save attendance: " + e.getMessage());
            alert.showAndWait();
        }
    }


}
