package ticktocktrack.gui;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ticktocktrack.database.DatabaseRegisterClass;
import ticktocktrack.logic.RegisterClass;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.UsersModel;

/**
 * Panel for the teacher to register or add a class/course.
 */
public class TeacherRegisterClassCenterPanel {

    /**
     * Creates a dialog panel for adding a new course/class for the given teacher.
     *
     * @param root the root Pane to which this dialog belongs, used for positioning or layering
     * @param teacherId the ID of the teacher adding the course
     * @return a Pane containing the UI elements for the add course dialog
     */
    public static Pane createAddCourseDialog(Pane root, int teacherId) {
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
        sectionField.setPromptText("Section (required)");
        sectionField.setPrefWidth(260);
        sectionField.setStyle("-fx-background-color: #eeeeee; -fx-border-color: transparent transparent black transparent; -fx-border-width: 1px;");

        // Program ComboBox
        ComboBox<String> programComboBox = new ComboBox<>();
        programComboBox.getItems().addAll(
            "BSECE – BS in Electronics Engineering",
            "BSME – BS in Mechanical Engineering",
            "BSA – BS in Accountancy",
            "BSBA-HRDM – BSBA major in Human Resource Development Management",
            "BSBA-MM – BSBA major in Marketing Management",
            "BSENTREP – BS in Entrepreneurship",
            "BSIT – BS in Information Technology",
            "DIT – Diploma Information Technology",
            "BSAM – BS in Applied Mathematics",
            "BSED-ENGLISH – Bachelor in Secondary Education major in English",
            "BSED-MATH – Bachelor in Secondary Education major in Mathematics",
            "BSOA – BS in Office Administration"
        );
        programComboBox.setPromptText("Select Program");
        programComboBox.setPrefWidth(260);

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #0097A7;");
        cancelBtn.setOnAction(e -> root.getChildren().remove(overlay));

        Button createBtn = new Button("Create");
        createBtn.setDisable(true);
        createBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaa;");
        
        

        createBtn.setOnAction(e -> {
            String courseName = courseField.getText().trim();
            String section = sectionField.getText().trim();
            String program = programComboBox.getValue();

            if (program != null && !program.isEmpty() && !courseName.isEmpty() && !section.isEmpty()) {
                boolean classCreated = RegisterClass.createClass(courseName, section, program);

                if (classCreated) {
                    int classId = DatabaseRegisterClass.getClassId(courseName, teacherId, section, program);

                    if (classId != -1) {
                        List<Student> students = DatabaseRegisterClass.getUnenrolledStudents(classId);
                        TeacherEnrollmentStudent.showStudentSelectionDialog(
                            students, courseName, section, classId, program
                        );
                    } else {
                        RegisterClass.showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve class ID.");
                    }
                }
            } else {
                RegisterClass.showAlert(Alert.AlertType.ERROR, "Missing Input", "Please fill in all fields.");
            }
        });

        // Enable Create button if all fields are filled
        courseField.textProperty().addListener((obs, oldVal, newVal) -> 
            updateCreateButtonState(createBtn, courseField, sectionField, programComboBox));
        sectionField.textProperty().addListener((obs, oldVal, newVal) -> 
            updateCreateButtonState(createBtn, courseField, sectionField, programComboBox));
        programComboBox.valueProperty().addListener((obs, oldVal, newVal) -> 
            updateCreateButtonState(createBtn, courseField, sectionField, programComboBox));

        buttonBox.getChildren().addAll(cancelBtn, createBtn);
        dialog.getChildren().addAll(title, courseField, sectionField, programComboBox, buttonBox);
        overlay.getChildren().add(dialog);

        return overlay;
    }

    /**
     * Updates the enabled/disabled state and style of the "Create" button based on the validity
     * of the input fields: course name, section, and selected program.
     *
     * @param createBtn       the Button to enable or disable
     * @param courseField     the TextField input for the course name
     * @param sectionField    the TextField input for the section
     * @param programComboBox the ComboBox for program selection
     */
    private static void updateCreateButtonState(Button createBtn, TextField courseField, TextField sectionField, ComboBox<String> programComboBox) {
        boolean enabled = !courseField.getText().trim().isEmpty()
                          && !sectionField.getText().trim().isEmpty()
                          && programComboBox.getValue() != null
                          && !programComboBox.getValue().isEmpty();

        createBtn.setDisable(!enabled);
        createBtn.setStyle(enabled ? "-fx-background-color: transparent; -fx-text-fill: #0097A7;" 
                                  : "-fx-background-color: transparent; -fx-text-fill: #aaa;");
    }

    /**
     * Displays an alert dialog with the specified type, title, and message.
     *
     * @param type    the Alert.AlertType specifying the kind of alert (e.g., INFORMATION, ERROR)
     * @param title   the title text for the alert dialog window
     * @param message the message content to display inside the alert
     */
    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    

}
