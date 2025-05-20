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

import ticktocktrack.database.DatabaseSubmitExcuse;

public class StudentSubmitExcuseCenterPanel {

    private static String selectedReason = null;

    public static Pane createPanel() {
        return createPanel(null);
    }

    public static Pane createPanel(Integer studentId) {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: #F2F2F2;");

        List<Button> excuseButtons = new ArrayList<>();

        // Shadow background
        String shadowPath = StudentSubmitExcuseCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        Rectangle container = new Rectangle(30, 50, 950, 550);
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
        fillUpText.setLayoutX(685);
        fillUpText.setLayoutY(100);

        // ComboBox Styling
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

        ComboBox<String> monthBox = new ComboBox<>(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));
        ComboBox<String> dayBox = new ComboBox<>();
        for (int i = 1; i <= 31; i++) dayBox.getItems().add(String.valueOf(i));
        ComboBox<String> yearBox = new ComboBox<>(FXCollections.observableArrayList("2025", "2026", "2027"));

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

        monthBox.setLayoutY(159);
        dayBox.setLayoutY(159);
        yearBox.setLayoutY(159);

        monthBox.setPrefWidth(115);
        dayBox.setPrefWidth(115);
        yearBox.setPrefWidth(115);

        monthBox.setPrefHeight(20);
        dayBox.setPrefHeight(20);
        yearBox.setPrefHeight(20);

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

        List<String> allTeachers = DatabaseSubmitExcuse.getAllTeacherNames();
        ContextMenu suggestions = new ContextMenu();

        teacherField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() == 0) {
                suggestions.hide();
            } else {
                List<MenuItem> filtered = allTeachers.stream()
                        .filter(name -> name.toLowerCase().contains(newText.toLowerCase()))
                        .map(name -> {
                            MenuItem item = new MenuItem(name);
                            item.setOnAction(e -> {
                                teacherField.setText(name);
                                suggestions.hide();
                            });
                            return item;
                        })
                        .collect(Collectors.toList());

                if (!filtered.isEmpty()) {
                    suggestions.getItems().setAll(filtered);
                    suggestions.show(teacherField, Side.BOTTOM, 0, 0);
                } else {
                    suggestions.hide();
                }
            }
        });

        // REMARKS FIELD
        Text remarksText = new Text("Remarks");
        remarksText.setFont(Font.font("Poppins", FontWeight.NORMAL, 14));
        remarksText.setFill(Color.web("#434343"));
        remarksText.setLayoutX(575);
        remarksText.setLayoutY(270);

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
            teacherField.clear();
            selectedReason = null;
            excuseButtons.forEach(b -> b.setStyle(defaultButtonStyle()));
        });

        messageOverlay.getChildren().addAll(submissionCard, confirmationMsg, okBtn);

        submitButton.setOnAction(e -> {
            if (selectedReason != null && !remarks.getText().trim().isEmpty()
                && monthBox.getValue() != null && dayBox.getValue() != null && yearBox.getValue() != null
                && !teacherField.getText().trim().isEmpty()) {
                messageOverlay.setVisible(true);
                messageOverlay.toFront();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Incomplete Submission");
                alert.setHeaderText(null);
                alert.setContentText("Please select a reason, date, teacher, and enter remarks before submitting.");
                alert.showAndWait();
            }
        });

        // Excuse Buttons
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
            monthBox, dayBox, yearBox,
            teacherText, teacherField,
            remarks, submitButton, excuseGrid, messageOverlay
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
