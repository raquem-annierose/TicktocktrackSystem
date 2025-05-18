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

import java.util.ArrayList;
import java.util.List;

public class StudentSubmitExcuseCenterPanel {

    private static String selectedReason = null;

    public static Pane createPanel() {
        return createPanel(null);
    }

    // Overloaded version that accepts an (optional) student-ID or any numeric parameter
    public static Pane createPanel(Integer studentId) {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: #F2F2F2;");

        // ************ PREDECLARE EXCUSE BUTTON LIST SO IT'S IN SCOPE EVERYWHERE ************
        List<Button> excuseButtons = new ArrayList<>();

        // Background shadow image
        String shadowPath = StudentSubmitExcuseCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        // Main white container
        Rectangle container = new Rectangle(30, 50, 950, 550);
        container.setFill(Color.web("#FFFFFF"));
        container.setStroke(Color.web("#CBCBCB"));
        container.setStrokeWidth(2);
        container.setEffect(new DropShadow(4, Color.GRAY));

        // Instructions text
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

        // Fill Up Form Title
        Text fillUpText = new Text("Fill Up Form:");
        fillUpText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));
        fillUpText.setLayoutX(685);
        fillUpText.setLayoutY(100);

        // ComboBox styling
        String comboBoxStyle =
            "-fx-background-color: white;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 0.7;" +
            "-fx-font-family: 'Poppins';" +
            "-fx-font-weight: normal;" +
            "-fx-text-fill: black;";

        // Date Dropdowns
        ComboBox<String> monthBox = new ComboBox<>(FXCollections.observableArrayList(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ));
        monthBox.setPromptText("Month");
        ComboBox<String> dayBox = new ComboBox<>();
        for (int i = 1; i <= 31; i++) dayBox.getItems().add(String.valueOf(i));
        dayBox.setPromptText("Day");
        ComboBox<String> yearBox = new ComboBox<>(FXCollections.observableArrayList("2025", "2026", "2027"));
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

        monthBox.setLayoutY(159);
        dayBox.setLayoutY(159);
        yearBox.setLayoutY(159);

        monthBox.setPrefWidth(115);
        dayBox.setPrefWidth(115);
        yearBox.setPrefWidth(115);

        monthBox.setPrefHeight(20);
        dayBox.setPrefHeight(20);
        yearBox.setPrefHeight(20);

        // Remarks TextArea
        TextArea remarks = new TextArea();
        remarks.setPromptText("Enter your excuse remarks here...");
        remarks.setLayoutX(575);
        remarks.setLayoutY(245);
        remarks.setPrefSize(375, 285);
        remarks.setWrapText(true);
        remarks.setStyle("-fx-border-color: #A39C9C; -fx-border-width: 2; -fx-font-family: 'Poppins'; -fx-font-size: 13;");

        Text remarksText = new Text("Remarks");
        remarksText.setFont(Font.font("Poppins", FontWeight.NORMAL, 14));
        remarksText.setFill(Color.web("#434343"));
        remarksText.setLayoutX(575);
        remarksText.setLayoutY(230);

        // Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setLayoutX(870);
        submitButton.setLayoutY(550);
        submitButton.setPrefWidth(80);
        submitButton.setStyle("-fx-background-color: #D6B4FC; -fx-text-fill: black;");

        // Confirmation Overlay
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
        okBtn.setOnAction(e -> {
            messageOverlay.setVisible(false);
            monthBox.getSelectionModel().clearSelection();
            dayBox.getSelectionModel().clearSelection();
            yearBox.getSelectionModel().clearSelection();
            remarks.clear();
            selectedReason = null;
            // reset all excuse button styles safely even if list is empty
            excuseButtons.forEach(b -> b.setStyle(defaultButtonStyle()));
        });

        messageOverlay.getChildren().addAll(submissionCard, confirmationMsg, okBtn);

        submitButton.setOnAction(e -> {
            if (selectedReason != null && !remarks.getText().trim().isEmpty()
                && monthBox.getValue() != null && dayBox.getValue() != null && yearBox.getValue() != null) {
                messageOverlay.setVisible(true);
                messageOverlay.toFront();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Incomplete Submission");
                alert.setHeaderText(null);
                alert.setContentText("Please select a reason, date, and enter remarks before submitting.");
                alert.showAndWait();
            }
        });

        // Excuse buttons grid
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

        double startX = 75;
        double startY = 230;
        double gapX = 145;
        double gapY = 160;

        for (int i = 0; i < imagePaths.length; i++) {
            int row = i / 3;
            int col = i % 3;
            double x = startX + col * gapX;
            double y = startY + row * gapY;

            Image icon = new Image(StudentSubmitExcuseCenterPanel.class.getResourceAsStream(imagePaths[i]));
            ImageView iconView = new ImageView(icon);
            iconView.setFitWidth(65);
            iconView.setFitHeight(65);

            Button reasonBtn = new Button("", iconView);
            reasonBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            reasonBtn.setPrefSize(104, 104);
            reasonBtn.setLayoutX(x);
            reasonBtn.setLayoutY(y);
            reasonBtn.setStyle(defaultButtonStyle());

            int index = i;
            reasonBtn.setOnAction(e -> {
                excuseButtons.forEach(b -> b.setStyle(defaultButtonStyle()));
                reasonBtn.setStyle(selectedButtonStyle());
                selectedReason = labels[index];
            });

            Text label = new Text(labels[i]);
            label.setFont(Font.font("Poppins", 12));
            label.setFill(Color.web("#434343"));
            label.setWrappingWidth(104);
            label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            label.setLayoutX(x);
            label.setLayoutY(y + 125);

            excuseButtons.add(reasonBtn);
            excuseGrid.getChildren().addAll(reasonBtn, label);
        }

        centerPanel.getChildren().addAll(
            shadowView, container, instructions, fillUpText, dateText, remarksText,
            monthBox, dayBox, yearBox, remarks, submitButton,
            excuseGrid, messageOverlay
        );

        return centerPanel;
    }

    private static String defaultButtonStyle() {
        return "-fx-background-color: #FFFFFF; " +
               "-fx-background-radius: 5; " +
               "-fx-border-radius: 5; " +
               "-fx-border-color: #A39C9C; " +
               "-fx-border-width: 2px; " +
               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 1, 0.15, 0, 1);";
    }

    private static String selectedButtonStyle() {
        return "-fx-background-color: #FFFFFF; " +
               "-fx-background-radius: 5; " +
               "-fx-border-radius: 5; " +
               "-fx-border-color: #B57EDC; " +
               "-fx-border-width: 2.5px; " +
               "-fx-effect: dropshadow(gaussian, rgba(181,126,220,0.25), 2, 0.2, 0, 2);";
    }
}
