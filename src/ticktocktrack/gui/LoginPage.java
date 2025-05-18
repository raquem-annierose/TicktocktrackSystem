package ticktocktrack.gui;


import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ticktocktrack.logic.Login;  // Import the Login class

public class LoginPage extends Application {

    private Login loginLogic;  // Declare the Login object

    @Override
    public void start(Stage primaryStage) {
        // Initialize the Login object
        loginLogic = new Login();  // Create an instance of Login

        // Path to your image
        String LoginPagePath = getClass().getResource("/resources/LOGIN.png").toExternalForm();

        // Create the image and ImageView
        Image backgroundImage = new Image(LoginPagePath);
        ImageView imageView = new ImageView(backgroundImage);

        // Set the size of the image (scale it to fit 1300x750)
        imageView.setFitWidth(1300);
        imageView.setFitHeight(750);
        imageView.setPreserveRatio(true);

        // Username Field
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(314);
        usernameField.setPrefHeight(51);
        usernameField.setStyle("-fx-background-color: transparent; -fx-border-color: #02383E; -fx-border-radius: 5px; -fx-padding: 5px;");

     
     // 1. Create plain TextField (for showing password), hidden by default
        TextField passwordTextField = new TextField();
        passwordTextField.setPrefWidth(314);
        passwordTextField.setPrefHeight(51);
        passwordTextField.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: #02383E; " +
            "-fx-border-radius: 5px;"
        );
        passwordTextField.setVisible(false);  // hidden initially
        passwordTextField.setManaged(false);

        // 2. Create PasswordField (for masking), visible by default
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(314);
        passwordField.setPrefHeight(51);
        passwordField.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: #02383E; " +
            "-fx-border-radius: 5px;"
        );
        passwordField.setVisible(true);
        passwordField.setManaged(true);

        // 3. Sync text both ways
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());

        // 4. Eye icon toggle
        ImageView passwordLogo = new ImageView(new Image(getClass().getResourceAsStream("/resources/EyeIcon.png")));
        passwordLogo.setFitWidth(24);
        passwordLogo.setFitHeight(24);
        passwordLogo.setPreserveRatio(true);
        passwordLogo.setCursor(Cursor.HAND);
        passwordLogo.setVisible(false);  // hide initially
        passwordLogo.setManaged(false);

        // 5. Show/hide icon when text is typed (bind to one listener)
        ChangeListener<String> showIconListener = (obs, oldText, newText) -> {
            boolean hasText = !newText.isEmpty();
            passwordLogo.setVisible(hasText);
            passwordLogo.setManaged(hasText);
        };
        passwordTextField.textProperty().addListener(showIconListener);
        passwordField.textProperty().addListener(showIconListener);

        // 6. Toggle logic on icon click
        passwordLogo.setOnMouseClicked(event -> {
            boolean showPassword = !passwordTextField.isVisible();

            passwordTextField.setVisible(showPassword);
            passwordTextField.setManaged(showPassword);
            passwordField.setVisible(!showPassword);
            passwordField.setManaged(!showPassword);

            if (showPassword) {
                passwordTextField.requestFocus();
                passwordTextField.positionCaret(passwordTextField.getText().length());
            } else {
                passwordField.requestFocus();
                passwordField.positionCaret(passwordField.getText().length());
            }
        });

        // 7. Wrap everything in an AnchorPane
        AnchorPane passwordContainer = new AnchorPane();
        passwordContainer.setPrefWidth(314);
        passwordContainer.setPrefHeight(51);
        
        
        
        passwordContainer.getChildren().addAll(passwordField, passwordTextField, passwordLogo);

        // Position the eye icon to the right and vertically centered
        AnchorPane.setRightAnchor(passwordLogo, 5.0);
        AnchorPane.setTopAnchor(passwordLogo, (51 - 24) / 2.0);

        
        // Login Button (rectangle)
        Rectangle loginButton = new Rectangle(180, 50);
        loginButton.setFill(Color.WHITE);
        loginButton.setArcWidth(10); // Rounded corners
        loginButton.setArcHeight(10);

        // Login Text (over button)
        Text loginText = new Text("Login");
        loginText.setFill(Color.web("#107C87"));
        loginText.setFont(Font.font("Arial", 20));

        // AnchorPane root
        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(imageView, usernameField, passwordContainer, loginButton, loginText);

        // Set positions
        AnchorPane.setTopAnchor(usernameField, 289.0);
        AnchorPane.setLeftAnchor(usernameField, 258.0);
        
     // Position password container (below username field)
        AnchorPane.setTopAnchor(passwordContainer, 435.0); // Adjust spacing as desired
        AnchorPane.setLeftAnchor(passwordContainer, 258.0);
        
     // Position the icon inside the container (right, vertically centered)
        AnchorPane.setRightAnchor(passwordLogo, 10.0); // 10px from right edge
        AnchorPane.setTopAnchor(passwordLogo, 13.0);  // vertically centered

     // Anchor password field elements
        AnchorPane.setLeftAnchor(passwordField, 0.0);
        AnchorPane.setRightAnchor(passwordField, 0.0);
        AnchorPane.setTopAnchor(passwordField, 0.0);
        AnchorPane.setBottomAnchor(passwordField, 0.0);

        AnchorPane.setLeftAnchor(passwordTextField, 0.0);
        AnchorPane.setRightAnchor(passwordTextField, 0.0);
        AnchorPane.setTopAnchor(passwordTextField, 0.0);
        AnchorPane.setBottomAnchor(passwordTextField, 0.0);


        // Create the Scene
        Scene scene = new Scene(root, 1300, 750, Color.BLACK);

        // Set button position
        AnchorPane.setLeftAnchor(loginButton, 330.0);
        AnchorPane.setTopAnchor(loginButton, 520.0);

        // Set login text position
        AnchorPane.setLeftAnchor(loginText, 395.0);
        AnchorPane.setTopAnchor(loginText, 535.0);

        // Set default colors
        loginButton.setFill(Color.web("#107C87"));  // Default button color (background)
        loginButton.setStroke(Color.web("#107C87")); // Border color
        loginButton.setStrokeWidth(2);               // Border thickness
        loginText.setFill(Color.web("#FFFFFF"));     // Default text color

        // Hover effect on the login button and text
        loginButton.setOnMouseEntered(event -> {
            loginButton.setFill(Color.web("#FFFFFF")); // Change background to white on hover
            loginText.setFill(Color.web("#107C87"));   // Change text color to blue on hover
            loginButton.setCursor(Cursor.HAND);        // Change cursor to hand
        });

        loginButton.setOnMouseExited(event -> {
            loginButton.setFill(Color.web("#107C87")); // Revert background to blue
            loginText.setFill(Color.web("#FFFFFF"));   // Revert text color to white
            loginButton.setCursor(Cursor.DEFAULT);     // Revert cursor
        });

        loginText.setOnMouseEntered(event -> {
            loginButton.setFill(Color.web("#FFFFFF"));
            loginText.setFill(Color.web("#107C87"));
            loginText.setCursor(Cursor.HAND);
        });

        loginText.setOnMouseExited(event -> {
            loginButton.setFill(Color.web("#107C87"));
            loginText.setFill(Color.web("#FFFFFF"));
            loginText.setCursor(Cursor.DEFAULT);
        });

        // Click event for both button and text
        loginButton.setOnMouseClicked(event -> handleLoginClick(usernameField.getText(), passwordField.getText(), primaryStage));
        loginText.setOnMouseClicked(event -> handleLoginClick(usernameField.getText(), passwordField.getText(), primaryStage));

        // Stage setup
        primaryStage.setTitle("Login Page");
        primaryStage.setScene(scene);
        primaryStage.setX(130);
        primaryStage.setY(25);
        primaryStage.show();
    }

 // Handle login click
    private void handleLoginClick(String username, String password, Stage primaryStage) {
        // Print the username and password for debugging
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        // Call authenticate method from Login class
        String role = loginLogic.authenticate(username, password);

        // Handle authentication result
        if (role != null) {
            System.out.println("Login successful! Role: " + role);

            // Check which dashboard to open based on role
            switch (role) {
                case "HeadAdmin":
                    openAdminDashboard(primaryStage);
                    break;
                case "Admin":
                    openAdminDashboard(primaryStage);
                    break;
                case "Teacher":
                    openTeacherDashboard(primaryStage);
                    break;
                case "Student":
                    openStudentDashboard(primaryStage);
                    break;
                default:
                    System.out.println("Unknown role: " + role);
                    // Optionally show an error message here
            }
        } else {
            System.out.println("Invalid username or password.");
            // Show an error message (e.g., in a dialog or label)
        }
    }

    // Open Admin Dashboard
    private void openAdminDashboard(Stage primaryStage) {
        AdminDashboardPage adminDashboard = new AdminDashboardPage();
        try {
            adminDashboard.start(new Stage());  // Open the Admin Dashboard in a new window
            primaryStage.close();  // Close the login window
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Open Teacher Dashboard
    private void openTeacherDashboard(Stage primaryStage) {
        TeacherDashboardPage teacherDashboard = new TeacherDashboardPage();
        try {
            teacherDashboard.start(new Stage());  // Open the Teacher Dashboard in a new window
            primaryStage.close();  // Close the login window
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Open Student Dashboard
    private void openStudentDashboard(Stage primaryStage) {
        StudentDashboardPage studentDashboard = new StudentDashboardPage();
        try {
            studentDashboard.start(new Stage());  // Open the Student Dashboard in a new window
            primaryStage.close();  // Close the login window
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
