package ticktocktrack.gui;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import ticktocktrack.database.DatabaseRegistrationManager;
import ticktocktrack.logic.UserRegistration;

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
            double gap = 80;

            // Create and add email field
            TextField emailField = createTextField("Email Address", startX, startY, 480, 60);
            facultyRegistrationPanel.getChildren().add(emailField);

            // Create and add username field
            TextField usernameField = createTextField("Username", startX, startY + gap * 1, 480, 60);
            facultyRegistrationPanel.getChildren().add(usernameField);

            // Create and add role ComboBox
            ComboBox<String> roleComboBox = new ComboBox<>();
            roleComboBox.getItems().addAll("Admin", "Teacher");
            roleComboBox.setLayoutX(startX);
            roleComboBox.setLayoutY(startY + gap * 2);
            roleComboBox.setPrefSize(480, 60);
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

            // Create and add password field
            PasswordField passwordField = createPasswordField("Password", startX, startY + gap * 3, 480, 60);
            facultyRegistrationPanel.getChildren().add(passwordField);

            // Create and add confirm password field
            PasswordField confirmPasswordField = createPasswordField("Confirm Password", startX, startY + gap * 4, 480, 60);
            facultyRegistrationPanel.getChildren().add(confirmPasswordField);

            // Create and add Done button
            Button doneButton = createDoneButton(800, 490);
            facultyRegistrationPanel.getChildren().add(doneButton);

            doneButton.setOnAction(e -> {
                String username = usernameField.getText();
                String email = emailField.getText();
                String role = roleComboBox.getValue();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                if (password.equals(confirmPassword)) {
                    UserRegistration.registerFaculty(username, email, role, password, confirmPassword);
                    showAlert(AlertType.INFORMATION, "Registration Successful", "Faculty successfully registered!");
                } else {
                    // Show error message if passwords do not match
                    showAlert(AlertType.ERROR, "Error", "Passwords do not match!");
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
            
            // For First Name and Middle Name on the same line
            double smallFieldWidth = 235;  // Half-width fields
            double smallGap = 10;           // Gap between first and middle name fields

            TextField emailField = createTextField("Email Address", startX, startY, startW, startH);
            TextField usernameField = createTextField("Username", startX, startY + gap, startW, startH);
         
            // Adding ComboBox for Year Level (styled) on the right of the name fields
            ComboBox<String> yearLevelComboBox = new ComboBox<>();
            yearLevelComboBox.getItems().addAll(
                "1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year"
            );
            yearLevelComboBox.setPromptText("Select Year Level");
            yearLevelComboBox.setLayoutX(startX);  // Positioned to the right of name fields
            yearLevelComboBox.setLayoutY(startY + gap * 2);
            yearLevelComboBox.setPrefWidth(235);
            yearLevelComboBox.setPrefHeight(50); // To match your smaller field height

            // Style it nicely
            String comboBoxBaseStyle = ""
                + "-fx-background-color: transparent;"
                + "-fx-text-fill: #02383E;"
                + "-fx-prompt-text-fill: #02383E;"
                + "-fx-border-color: #02383E;"
                + "-fx-border-width: 2px;"
                + "-fx-background-radius: 12px;"
                + "-fx-border-radius: 12px;";

            yearLevelComboBox.setStyle(comboBoxBaseStyle);

            // Section field next to Year Level
            TextField sectionField = createTextField("Section", startX + smallFieldWidth + smallGap, startY + gap * 2, 235, 50);
            
            TextField lastNameField = createTextField("Last Name", startX, startY + gap * 3, startW, startH);
           

            TextField firstNameField = createTextField("First Name", startX, startY + gap * 4, startW, startH);
            firstNameField.setPrefWidth(smallFieldWidth);

            TextField middleNameField = createTextField("Middle Name", startX + smallFieldWidth + smallGap, startY + gap * 4, startW, startH);
            middleNameField.setPrefWidth(smallFieldWidth);

            PasswordField passwordField = createPasswordField("Password", startX, startY + gap * 5, startW, startH);
            PasswordField confirmPasswordField = createPasswordField("Confirm Password", startX, startY + gap * 6, startW, startH);

         
            
            Button doneButton = createDoneButton(800, 490);

            doneButton.setOnAction(event -> {
                // Get all the values from the fields
                String email = emailField.getText();
                String username = usernameField.getText();
                String lastName = lastNameField.getText();
                String firstName = firstNameField.getText();
                String middleName = middleNameField.getText();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                String yearLevel = yearLevelComboBox.getValue();  // Get selected Year Level
                String section = sectionField.getText();

                // Now simply call the UserRegistration method
                UserRegistration.registerStudent(
                    username,
                    email,
                    password,
                    confirmPassword,
                    firstName,
                    middleName,
                    lastName,
                    yearLevel,
                    section
                );
            });

            studentRegistrationPanel.getChildren().addAll(
                    emailField, usernameField, lastNameField,
                    firstNameField, middleNameField,
                    passwordField, confirmPasswordField,
                    yearLevelComboBox, sectionField,  // Add ComboBox for Year Level and Section field
                    doneButton
            );

            return studentRegistrationPanel;
        }
    }


	public static void showAlert(AlertType error, String string, String string2) {
		// TODO Auto-generated method stub
		
	}

}