package ticktocktrack.gui;

import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;
import javafx.collections.FXCollections;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Side;
import ticktocktrack.database.DatabaseIndividualReport;

// Removed import of DatabaseSubmitExcuse
// import ticktocktrack.database.DatabaseSubmitExcuse;

import ticktocktrack.database.TeacherNotificationDAO;
import ticktocktrack.logic.Session;

/**
 * Panel for students to submit excuses for their absences.
 */
public class StudentSubmitExcuseCenterPanel {

    /**
     * Currently selected excuse reason.
     */
    private static String selectedReason = null;

    /**
     * Creates the excuse submission panel without specifying a student ID.
     * 
     * @return A Pane containing the excuse submission UI.
     */
    public static Pane createPanel() {
        return createPanel(null);
    }

    /**
     * Creates the excuse submission panel for a specific student.
     * 
     * @param studentId The ID of the student submitting the excuse.
     * @return A Pane containing the excuse submission UI.
     */
    public static Pane createPanel(Integer studentId) {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: #F2F2F2;");
               
        List<Button> excuseButtons = new ArrayList<>();

        // Shadow background
        String shadowPath = StudentSubmitExcuseCenterPanel.class
            .getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        Rectangle container = new Rectangle(30, 40, 950, 590);
        container.setFill(Color.web("#FFFFFF"));
        container.setStroke(Color.web("#CBCBCB"));
        container.setStrokeWidth(2);
        container.setEffect(new DropShadow(4, Color.GRAY));

        Text instructions = new Text(
            "If you were unable to attend your class due to\n" +
            "valid reasons (e.g., illness, emergency, or\n" +
            "personal matters), you may submit an excuse\n" +
            "here for review."
        );
        instructions.setFont(Font.font("Poppins", FontWeight.NORMAL, 14));
        instructions.setFill(Color.web("#434343"));
        instructions.setLayoutX(85);
        instructions.setLayoutY(125);

        Text fillUpText = new Text("Fill Up Form:");
        fillUpText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));
        fillUpText.setLayoutX(575);
        fillUpText.setLayoutY(70);
        

        	
        String comboBoxStyle =
            "-fx-background-color: white;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #02383E;" +
            "-fx-border-width: 0.7;" +
            "-fx-font-family: 'Poppins';" +
            "-fx-font-weight: normal;" +
            "-fx-text-fill: black;";

        ComboBox<String> monthBox = new ComboBox<>(FXCollections.observableArrayList(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ));
        ComboBox<String> dayBox = new ComboBox<>();
        for (int i = 1; i <= 31; i++) dayBox.getItems().add(String.valueOf(i));
        ComboBox<String> yearBox = new ComboBox<>(FXCollections.observableArrayList(
            "2025", "2026", "2027"
        ));

        monthBox.setPromptText("Month");
        dayBox.setPromptText("Day");
        yearBox.setPromptText("Year");

        monthBox.setStyle(comboBoxStyle);
        dayBox.setStyle(comboBoxStyle);
        yearBox.setStyle(comboBoxStyle);

        Text dateText = new Text("Date");
        dateText.setFont(Font.font("Poppins", FontWeight.NORMAL, 16));
        dateText.setFill(Color.web("#434343"));
        dateText.setLayoutX(575);
        dateText.setLayoutY(140);

        monthBox.setLayoutX(570);
        dayBox.setLayoutX(699);
        yearBox.setLayoutX(830);
        
        monthBox.setLayoutY(154);
        dayBox.setLayoutY(154);
        yearBox.setLayoutY(154);
        
        monthBox.setPrefWidth(115);
        dayBox.setPrefWidth(115);
        yearBox.setPrefWidth(115);
        
        monthBox.setPrefHeight(18);
        dayBox.setPrefHeight(18);
        yearBox.setPrefHeight(18);
        
    
         List<String> studentCourses = DatabaseIndividualReport.getCourseNamesForStudent(studentId);

        // Create ComboBox with courses
        ComboBox<String> courseBox = new ComboBox<>(FXCollections.observableArrayList(studentCourses));
        courseBox.setPromptText("Select Course");
        courseBox.setLayoutX(570);
        courseBox.setLayoutY(80);
        courseBox.setPrefWidth(375);
        courseBox.setStyle(comboBoxStyle);

        

        // TEACHER NAME AUTOCOMPLETE FIELD
        Text teacherText = new Text("Send to");
        teacherText.setFont(Font.font("Poppins", FontWeight.NORMAL, 14));
        teacherText.setFill(Color.web("#434343"));
        teacherText.setLayoutX(575);
        teacherText.setLayoutY(215);

        TextField teacherField = new TextField();
        teacherField.setPromptText("Enter teacher name...");
        teacherField.setLayoutX(575);
        teacherField.setLayoutY(225);
        teacherField.setPrefWidth(375);
        teacherField.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 13;");

        // Use TeacherNotificationDAO to get teacher names now
        List<String> allTeachers = TeacherNotificationDAO.getAllTeacherNames();

        ContextMenu suggestions = new ContextMenu();
        teacherField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                suggestions.hide();
            } else {
                List<MenuItem> filtered = allTeachers.stream()
                    .filter(name -> name.toLowerCase().contains(newText.toLowerCase()))
                    .map(name -> {
                        MenuItem item = new MenuItem(name);
                        item.setOnAction(ev -> {
                            teacherField.setText(name);
                            suggestions.hide();
                        });
                        return item;
                    }).collect(Collectors.toList());

                if (!filtered.isEmpty()) {
                    suggestions.getItems().setAll(filtered);
                    suggestions.show(teacherField, Side.BOTTOM, 0, 0);
                } else {
                    suggestions.hide();
                }
            }
        });

        // REMARKS FIELD
        Text remarksLabel = new Text("Remarks");
        remarksLabel.setFont(Font.font("Poppins", FontWeight.NORMAL, 14));
        remarksLabel.setFill(Color.web("#434343"));
        remarksLabel.setLayoutX(575);
        remarksLabel.setLayoutY(270);

        TextArea remarks = new TextArea();
        remarks.setPromptText("Enter your excuse remarks here...");
        remarks.setLayoutX(575);
        remarks.setLayoutY(285);
        remarks.setPrefSize(375, 245);
        remarks.setWrapText(true);
        remarks.setStyle("-fx-border-color: #A39C9C; -fx-border-width: 2; -fx-font-family: 'Poppins'; -fx-font-size: 13;");

        Button submitButton = new Button("Submit");
        submitButton.setLayoutX(870);
        submitButton.setLayoutY(550);
        submitButton.setPrefWidth(80);
        submitButton.setStyle("-fx-background-color: #D6B4FC; -fx-text-fill: black;");

        // Confirmation overlay pane
        Pane messageOverlay = new Pane();
        messageOverlay.setPrefSize(1300, 750);
        messageOverlay.setStyle("-fx-background-color: rgba(111,111,111,0.65);");
        messageOverlay.setVisible(false);

        Rectangle submissionCard = new Rectangle(400, 180);
        submissionCard.setArcWidth(20);
        submissionCard.setArcHeight(20);
        submissionCard.setFill(Color.WHITE);
        submissionCard.setEffect(new DropShadow(10, Color.GRAY));
        submissionCard.setLayoutX(350);
        submissionCard.setLayoutY(200);

        Text confirmationMsg = new Text("Your excuse has been submitted.\nPlease wait 1 to 3 business days for approval.");
        confirmationMsg.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        confirmationMsg.setFill(Color.BLACK);
        confirmationMsg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        confirmationMsg.setWrappingWidth(360);
        confirmationMsg.setLayoutX(370);
        confirmationMsg.setLayoutY(260);

        Button okBtn = new Button("OK");
        okBtn.setLayoutX(595);
        okBtn.setLayoutY(313);
        okBtn.setPrefWidth(100);
        okBtn.setStyle("-fx-background-color: #D6B4FC; -fx-text-fill: black;");
        okBtn.setOnAction(ev -> {
            messageOverlay.setVisible(false);
            monthBox.getSelectionModel().clearSelection();
            dayBox.getSelectionModel().clearSelection();
            yearBox.getSelectionModel().clearSelection();
            remarks.clear();
            teacherField.clear();
            courseBox.getSelectionModel().clearSelection();  // Clear ComboBox selection
            selectedReason = null;
            excuseButtons.forEach(b -> b.setStyle(defaultButtonStyle()));
        });


     // At UI initialization (before submit button handler)
        messageOverlay.getChildren().addAll(submissionCard, confirmationMsg, okBtn, courseBox);

        submitButton.setOnAction(e -> {
            // Check if reason is selected
            if (selectedReason == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a reason for the excuse. If none of the above apply, please select 'Others'.").showAndWait();
                return;  // stop further processing
            }
            
            // If reason is "Others", remarks must be filled (specified)
            if (selectedReason.equalsIgnoreCase("Others") && remarks.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Since you selected 'Others', please specify your reason in the remarks field.").showAndWait();
                return;
            }

            // Validate all required fields are filled
            if (!remarks.getText().trim().isEmpty()
                && monthBox.getValue() != null
                && dayBox.getValue() != null
                && yearBox.getValue() != null
                && !teacherField.getText().trim().isEmpty()
                && courseBox.getValue() != null) {

                // Format date as YYYY-MM-DD
                String dateString = yearBox.getValue() + "-" +
                    String.format("%02d", monthBox.getSelectionModel().getSelectedIndex() + 1) + "-" +
                    String.format("%02d", Integer.parseInt(dayBox.getValue()));

                // Get teacher ID by teacher name
                int teacherId = TeacherNotificationDAO.getTeacherIdByName(teacherField.getText().trim());
                if (teacherId == -1) {
                    new Alert(Alert.AlertType.ERROR, "Invalid teacher name.").showAndWait();
                    return;
                }

                String selectedCourse = courseBox.getValue();

                // Compose notification message
                String eventMessage = "submitted an excuse for " + dateString
                    + " in course " + selectedCourse 
                    + " | Reason: " + remarks.getText().trim();

                // Get current logged-in user's ID
                int senderUserId = -1;
                if (Session.getCurrentUser() != null) {
                    senderUserId = Session.getCurrentUser().getUserId();
                }

                System.out.println("Sending notification from senderUserId: " + senderUserId);

                if (senderUserId == -1) {
                    new Alert(Alert.AlertType.ERROR, "You must be logged in to submit an excuse.").showAndWait();
                    return;
                }

                // Send notification via DAO method
                TeacherNotificationDAO.sendTeacherNotification(teacherId, eventMessage, "Excuse Submission", senderUserId);

                messageOverlay.setVisible(true);
                messageOverlay.toFront();

            } else {
                new Alert(Alert.AlertType.WARNING, "Please complete all fields before submitting.").showAndWait();
            }
        });

        // Excuse reason buttons grid
        Pane excuseGrid = new Pane();
        excuseGrid.setPrefSize(540, 420);
        String[] imagePaths = {
            "/resources/Student_Dashboard/Student_Family_emergency.png",
            "/resources/Student_Dashboard/Student_Medical_appointment.png",
            "/resources/Student_Dashboard/Student_sick.png",
            "/resources/Student_Dashboard/Student_Academic_related.png",
            "/resources/Student_Dashboard/Student_Transportation.png",
            "/resources/Student_Dashboard/Student_Others.png"
        };
        String[] labels = {
            "Family Emergency", "Medical Appointment", "Sick",
            "Academic Related", "Transportation Issue", "Others"
        };
        double startX = 75, startY = 230, gapX = 145, gapY = 160;
        for (int i = 0; i < imagePaths.length; i++) {
            int row = i / 3, col = i % 3;
            double x = startX + col * gapX, y = startY + row * gapY;

            Image icon = new Image(StudentSubmitExcuseCenterPanel.class
                .getResourceAsStream(imagePaths[i]));
            ImageView iconView = new ImageView(icon);
            iconView.setFitWidth(65);
            iconView.setFitHeight(65);

            Button reasonBtn = new Button("", iconView);
            reasonBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            reasonBtn.setPrefSize(104, 104);
            reasonBtn.setLayoutX(x);
            reasonBtn.setLayoutY(y);
            reasonBtn.setStyle(defaultButtonStyle());
            
         // This flag tracks if placeholder is showing (only relevant for "Others")
            final String OTHERS_PLACEHOLDER = "Please specify";

            // Initially remarks is not editable and no placeholder shown
            remarks.setEditable(false);

            // Add listener to manage placeholder for Others
            remarks.textProperty().addListener((obs, oldText, newText) -> {
                if (selectedReason != null && selectedReason.equalsIgnoreCase("Others")) {
                    if (newText.isEmpty()) {
                        remarks.setPromptText(OTHERS_PLACEHOLDER);
                    } else {
                        remarks.setPromptText(null);
                    }
                } else {
                    // For other reasons, no prompt text
                    remarks.setPromptText(null);
                }
            });

            int index = i;
            reasonBtn.setOnAction(evt -> {
                excuseButtons.forEach(b -> b.setStyle(defaultButtonStyle()));
                reasonBtn.setStyle(selectedButtonStyle());
                selectedReason = labels[index];

                if (selectedReason.equalsIgnoreCase("Others")) {
                    remarks.clear();            // clear remarks so user can type
                    remarks.setEditable(true);  // allow editing
                    remarks.setPromptText(OTHERS_PLACEHOLDER);  // show placeholder text
                    remarks.requestFocus();     // focus text area
                } else {
                    remarks.setText(selectedReason);  // autofill remarks
                    remarks.setEditable(false);       // disable editing
                    remarks.setPromptText(null);      // remove placeholder text
                }
            });



            Text lbl = new Text(labels[i]);
            lbl.setFont(Font.font("Poppins", 12));
            lbl.setFill(Color.web("#434343"));
            lbl.setWrappingWidth(104);
            lbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            lbl.setLayoutX(x);
            lbl.setLayoutY(y + 125);

            excuseButtons.add(reasonBtn);
            excuseGrid.getChildren().addAll(reasonBtn, lbl);
        }

        centerPanel.getChildren().addAll(
            shadowView, container, instructions, fillUpText, dateText, remarksLabel,
            monthBox, dayBox, yearBox, courseBox,
            teacherText, teacherField,
            remarks, submitButton, excuseGrid, messageOverlay
        );

        return centerPanel;
    }

    /**
     * Returns the default CSS style for buttons in the excuse submission panel.
     *
     * @return A string representing the default button style.
     */
    private static String defaultButtonStyle() {
        return "-fx-background-color: #FFFFFF; " +
               "-fx-background-radius: 5; " +
               "-fx-border-radius: 5; " +
               "-fx-border-color: #B4B4B4; " +
               "-fx-border-width: 1;";
    }

    /**
     * Returns the CSS style for buttons when they are selected in the excuse submission panel.
     *
     * @return A string representing the selected button style.
     */
    private static String selectedButtonStyle() {
        return "-fx-background-color: #D6B4FC; " +
               "-fx-background-radius: 5; " +
               "-fx-border-radius: 5; " +
               "-fx-border-color: #9B4FFC; " +
               "-fx-border-width: 2;";
    }
}