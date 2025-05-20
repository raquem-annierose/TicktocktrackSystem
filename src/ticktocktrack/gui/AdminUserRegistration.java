package ticktocktrack.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import ticktocktrack.logic.UserRegistration;
import javafx.scene.image.Image;

public class AdminUserRegistration {

    private static Pane createBasePanel() {
        Pane basePanel = new Pane();
        basePanel.setPrefSize(970, 560);
        basePanel.setStyle("-fx-background-color: WHITE; -fx-background-radius: 20;");
        basePanel.setLayoutX(40);
        basePanel.setLayoutY(45);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(15);
        shadow.setOffsetX(0);
        shadow.setOffsetY(0);
        shadow.setSpread(0.1);
        basePanel.setEffect(shadow);

        return basePanel;
    }

    private static Button createExitButton(Pane panel) {
        Button exitButton = new Button("X");
        exitButton.setLayoutX(920);
        exitButton.setLayoutY(20);
        exitButton.setStyle("-fx-background-color: red; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-padding: 10px; " +
                            "-fx-background-radius: 12px;");
        exitButton.setCursor(Cursor.HAND);

        exitButton.setOnAction(e -> panel.setVisible(false));

        return exitButton;
    }

    private static Button createDoneButton(double x, double y) {
        Button doneButton = new Button("Done");
        doneButton.setPrefSize(150, 50);
        doneButton.setLayoutX(x);
        doneButton.setLayoutY(y);
        doneButton.setStyle("-fx-background-color: #02383E; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 8px;");
        doneButton.setCursor(Cursor.HAND);    
        	
        return doneButton;
    }

 // Overloaded version with width and height
    private static TextField createTextField(String prompt, double x, double y, double width, double height) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setPrefSize(width, height);
        textField.setLayoutX(x);
        textField.setLayoutY(y);
        textField.setStyle(
            "-fx-border-color: #02383E;" +
            "-fx-border-width: 2px;" +
            "-fx-background-radius: 12px;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-color: white;" +
            "-fx-prompt-text-fill: #02383E;"
        );
        return textField;
    }

    private static PasswordField createPasswordField(String prompt, double x, double y, double width, double height) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(prompt);
        passwordField.setPrefSize(width, height); // width and height passed as arguments
        passwordField.setLayoutX(x);
        passwordField.setLayoutY(y);
        passwordField.setStyle(
            "-fx-border-color: #02383E;" +
            "-fx-border-width: 2px;" +
            "-fx-background-radius: 12px;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-color: white;" +
            "-fx-text-fill: #02383E;" +
            "-fx-prompt-text-fill: #02383E;"
        );
        return passwordField;
    }
    
    public static Pane createPasswordFieldWithToggle(String prompt, double x, double y, double width, double height) {
        PasswordField passwordField = new PasswordField();
        TextField textField = new TextField();

        passwordField.setPromptText(prompt);
        textField.setPromptText(prompt);
        textField.setVisible(false);
        textField.setManaged(false);

        // Sync text
        passwordField.textProperty().bindBidirectional(textField.textProperty());

        String style = "-fx-border-color: #02383E;" +
                       "-fx-border-width: 2px;" +
                       "-fx-background-radius: 12px;" +
                       "-fx-border-radius: 12px;" +
                       "-fx-background-color: white;" +
                       "-fx-text-fill: #02383E;" +
                       "-fx-prompt-text-fill: #02383E;";
        passwordField.setStyle(style);
        textField.setStyle(style);

        passwordField.setPrefSize(width, height);
        textField.setPrefSize(width, height);

        String imageUrl = AdminUserRegistration.class.getResource("/resources/EyeIcon.png").toExternalForm();

        ImageView eyeIcon = new ImageView(new Image(imageUrl));

        eyeIcon.setFitWidth(24);
        eyeIcon.setFitHeight(24);
        eyeIcon.setCursor(Cursor.HAND);
        eyeIcon.setVisible(false);
        eyeIcon.setManaged(false);


        ChangeListener<String> showIconListener = (obs, oldText, newText) -> {
            boolean hasText = !newText.isEmpty();
            eyeIcon.setVisible(hasText);
            eyeIcon.setManaged(hasText);
        };
        passwordField.textProperty().addListener(showIconListener);
        textField.textProperty().addListener(showIconListener);

        eyeIcon.setOnMouseClicked(e -> {
            boolean show = !textField.isVisible();
            textField.setVisible(show);
            textField.setManaged(show);
            passwordField.setVisible(!show);
            passwordField.setManaged(!show);

            if (show) {
                textField.requestFocus();
                textField.positionCaret(textField.getText().length());
            } else {
                passwordField.requestFocus();
                passwordField.positionCaret(passwordField.getText().length());
            }
        });

        StackPane container = new StackPane(passwordField, textField, eyeIcon);
        container.setLayoutX(x);
        container.setLayoutY(y);
        container.setPrefSize(width, height);
        container.setMaxSize(width, height);

        StackPane.setMargin(eyeIcon, new Insets(0, 10, 0, 0));
        StackPane.setAlignment(eyeIcon, Pos.CENTER_RIGHT);

        return container;
    }

    public static String getPasswordFromPane(Pane pane) {
        for (javafx.scene.Node node : pane.getChildren()) {
            if (node instanceof PasswordField) {
                return ((PasswordField) node).getText();
            }
            if (node instanceof TextField) {
                TextField tf = (TextField) node;
                if (tf.isVisible()) {
                    return tf.getText();
                }
            }
        }
        return "";
    }
    
    private static void showAlert(AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    public static class FacultyRegistrationPanel {

        public static Pane createPanel() {
            Pane facultyRegistrationPanel = createBasePanel();

            Text title = new Text("Faculty Registration");
            title.setFont(Font.font("Poppins", FontWeight.BOLD, 36));
            title.setFill(Color.web("#02383E"));
            title.setLayoutX(313);
            title.setLayoutY(70);
            facultyRegistrationPanel.getChildren().add(title);

            Button exitButton = createExitButton(facultyRegistrationPanel);
            facultyRegistrationPanel.getChildren().add(exitButton);

            double startX = 225;
            double startY = 100;
            double gap = 70;

            // Create and add first name field
            TextField firstNameField = createTextField("First Name", startX, startY, 475/ 2, 50);
            facultyRegistrationPanel.getChildren().add(firstNameField);

            // Create and add last name field
            TextField lastNameField = createTextField("Last Name", 235 * 2, startY, 475/ 2, 50);
            facultyRegistrationPanel.getChildren().add(lastNameField);

            // Create and add email field (shifted down)
            TextField emailField = createTextField("Email Address", startX, startY + gap * 1, 480, 50);
            facultyRegistrationPanel.getChildren().add(emailField);

            // Create and add username field (shifted down)
            TextField usernameField = createTextField("Username", startX, startY + gap * 2, 480, 50);
            facultyRegistrationPanel.getChildren().add(usernameField);

            // Create and add role ComboBox (shifted down)
            ComboBox<String> roleComboBox = new ComboBox<>();
            roleComboBox.getItems().addAll("Admin", "Teacher");
            roleComboBox.setLayoutX(startX);
            roleComboBox.setLayoutY(startY + gap * 3);
            roleComboBox.setPrefSize(480, 50);
            roleComboBox.setPromptText("Role");

            String comboBoxBaseStyle = ""
                + "-fx-background-color: transparent;"
                + "-fx-text-fill: #02383E;"
                + "-fx-prompt-text-fill: #02383E;"
                + "-fx-border-color: #02383E;"
                + "-fx-border-width: 2px;"
                + "-fx-background-radius: 12px;"
                + "-fx-border-radius: 12px;";

            roleComboBox.setStyle(comboBoxBaseStyle);
            facultyRegistrationPanel.getChildren().add(roleComboBox);

         // Replace these lines:
            PasswordField passwordField = createPasswordField("Password", startX, startY + gap * 4, 480, 50);
            facultyRegistrationPanel.getChildren().add(passwordField);

            PasswordField confirmPasswordField = createPasswordField("Confirm Password", startX, startY + gap * 5, 480, 50);
            facultyRegistrationPanel.getChildren().add(confirmPasswordField);

            // With these lines:
            Pane passwordPane = createPasswordFieldWithToggle("Password", startX, startY + gap * 4, 480, 50);
            Pane confirmPasswordPane = createPasswordFieldWithToggle("Confirm Password", startX, startY + gap * 5, 480, 50);

            facultyRegistrationPanel.getChildren().addAll(passwordPane, confirmPasswordPane);


            // Create and add Done button (shifted down)
            Button doneButton = createDoneButton(800, 490);
            facultyRegistrationPanel.getChildren().add(doneButton);

            doneButton.setOnAction(e -> {
                System.out.println("Done button clicked");
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String username = usernameField.getText();
                String email = emailField.getText();
                String role = roleComboBox.getValue();

                // Use helper to get text from your toggle password panes
                String password = getPasswordFromPane(passwordPane);
                String confirmPassword = getPasswordFromPane(confirmPasswordPane);

                if (!password.equals(confirmPassword)) {
                    showAlert(AlertType.ERROR, "Error", "Passwords do not match!");
                    return;
                }

                boolean registered = UserRegistration.registerFaculty(username, email, role, password, confirmPassword, firstName, lastName);
                if (registered) {
                    showAlert(AlertType.INFORMATION, "Registration Successful", "Faculty successfully registered!");
                } else {
                    showAlert(AlertType.ERROR, "Registration Failed", "Registration failed. Please try again.");
                }
            });



            return facultyRegistrationPanel;
        }
    }


    public static class StudentRegistrationPanel {
        public static Pane createPanel() {
            Pane studentRegistrationPanel = createBasePanel();

            Text title = new Text("Student Registration");
            title.setFont(Font.font("Poppins", FontWeight.BOLD, 36));
            title.setFill(Color.web("#02383E"));
            title.setLayoutX(308);
            title.setLayoutY(70);
            studentRegistrationPanel.getChildren().add(title);

            Button exitButton = createExitButton(studentRegistrationPanel);
            studentRegistrationPanel.getChildren().add(exitButton);

            double startX = 225;
            double startY = 100;
            double startW = 480;
            double startH = 50;
            double gap = 65;

            double smallFieldWidth = 235;
            double smallGap = 10;

            TextField emailField = createTextField("Email Address", startX, startY, startW, startH);
            TextField usernameField = createTextField("Username", startX, startY + gap, startW, startH);
            
         // ComboBox for Program (NEW)
            ComboBox<String> programComboBox = new ComboBox<>();  // ⬅️ NEW
            programComboBox.getItems().addAll(                    // ⬅️ NEW
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
            programComboBox.setPromptText("Select Program");       // ⬅️ NEW
            programComboBox.setLayoutX(startX);              // ⬅️ NEW (Positioned beside Year Level)
            programComboBox.setLayoutY(startY + gap * 2);          // ⬅️ NEW
            programComboBox.setPrefWidth(480);                     // ⬅️ NEW (Wide enough for full text)
            programComboBox.setPrefHeight(50);                     // ⬅️ NEW

            // Apply the same style for rounded corners and border
            programComboBox.setStyle(
                "-fx-border-color: #02383E;" +
                "-fx-border-width: 2px;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-radius: 12px;" +
                "-fx-background-color: white;" +
                "-fx-prompt-text-fill: #02383E;"
            );

            // ComboBox for Year Level
            ComboBox<String> yearLevelComboBox = new ComboBox<>();
            yearLevelComboBox.getItems().addAll(
                "1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year"
            );
            yearLevelComboBox.setPromptText("Select Year Level");
            yearLevelComboBox.setLayoutX(startX);
            yearLevelComboBox.setLayoutY(startY + gap * 3);
            yearLevelComboBox.setPrefWidth(startW / 2);  // Adjusted width to fit 3 items on a row
            yearLevelComboBox.setPrefHeight(50);

            // Apply the same style for rounded corners and border to Year Level ComboBox
            yearLevelComboBox.setStyle(
                "-fx-border-color: #02383E;" +
                "-fx-border-width: 2px;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-radius: 12px;" +
                "-fx-background-color: white;" +
                "-fx-prompt-text-fill: #02383E;"
            );
            // Section field
            TextField sectionField = createTextField("Section", startX + smallFieldWidth + smallGap, startY + gap * 3, startW / 2, 50); // ⬅️ Adjusted position

            TextField lastNameField = createTextField("Last Name", startX, startY + gap * 4, startW, startH);

            TextField firstNameField = createTextField("First Name", startX, startY + gap * 5, startW, startH);
            firstNameField.setPrefWidth(smallFieldWidth);

            TextField middleNameField = createTextField("Middle Name", startX + smallFieldWidth + smallGap, startY + gap * 5, startW, startH);
            middleNameField.setPrefWidth(smallFieldWidth);

            Pane passwordFieldPane = createPasswordFieldWithToggle("Password", startX, startY + gap * 6, startW / 2, startH);
            Pane confirmPasswordFieldPane = createPasswordFieldWithToggle("Confirm Password", startX + smallFieldWidth + smallGap, startY + gap * 6, startW / 2, startH);

            Button doneButton = createDoneButton(800, 490);

            doneButton.setOnAction(event -> {
                String email = emailField.getText();
                String username = usernameField.getText();
                String lastName = lastNameField.getText();
                String firstName = firstNameField.getText();
                String middleName = middleNameField.getText();
                String password = getPasswordFromPane(passwordFieldPane);
                String confirmPassword = getPasswordFromPane(confirmPasswordFieldPane);
                String yearLevel = yearLevelComboBox.getValue();
                String section = sectionField.getText();
                String program = programComboBox.getValue(); // NEW

                UserRegistration.registerStudent(
                    username,
                    email,
                    password,
                    confirmPassword,
                    firstName,
                    middleName,
                    lastName,
                    yearLevel,
                    section,
                    program // NEW
                );
            });


            studentRegistrationPanel.getChildren().addAll(
                emailField, usernameField, lastNameField,
                firstNameField, middleNameField,
                passwordFieldPane, confirmPasswordFieldPane,
                yearLevelComboBox, programComboBox, sectionField, // ⬅️ NEW programComboBox added
                doneButton
            );

            return studentRegistrationPanel;
        }
    }

	

}