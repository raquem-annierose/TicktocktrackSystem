package ticktocktrack.gui;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The main homepage of the application, showing the initial UI.
 * Provides a method to handle login button click that opens the LoginPage.
 */
public class HomePage extends Application {

    /**
     * Handles the login button click event by closing the current window
     * and opening the LoginPage in a new window.
     * 
     * @param primaryStage The primary stage (window) of the homepage to close.
     */
    private void handleLoginClick(Stage primaryStage) {
        System.out.println("Login Clicked!");
        
        // Close the current homepage
        primaryStage.close();

        // Launch the LoginPage in a new window
        LoginPage loginPage = new LoginPage(); // Create an instance of LoginPage
        loginPage.start(new Stage());  // Open the LoginPage in a new stage (window)
    }


    /**
     * Starts the JavaFX application by setting up the homepage scene with
     * a background image and login button.
     * 
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        // Path to your image
        String homePagePath = getClass().getResource("/resources/HOMEPAGE.jpg").toExternalForm();

        // Create the image and ImageView
        Image backgroundImage = new Image(homePagePath);
        ImageView imageView = new ImageView(backgroundImage);
        imageView.setPreserveRatio(false); // Allow full stretch

        // Create the rectangle for the login button
        Rectangle loginButton = new Rectangle(170, 65);
        loginButton.setFill(Color.web("#FFFFFF")); // Button background: white
        loginButton.setStroke(Color.web("#107C87")); // Button border color
        loginButton.setArcWidth(20);  // Rounded corners
        loginButton.setArcHeight(20);

        // Create the "Log In" text
        Text loginText = new Text("Log In");
        loginText.setFill(Color.web("#107C87"));
        loginText.setFont(Font.font("Poppins", 28));

        // Pane to hold everything
        Pane root = new Pane();
        root.getChildren().addAll(imageView, loginButton, loginText);

        // Scene
        Scene scene = new Scene(root, 1300, 750, Color.WHITE);

        // Bind image size to window size
        imageView.fitWidthProperty().bind(scene.widthProperty());
        imageView.fitHeightProperty().bind(scene.heightProperty());

        // Bind button position: bottom right with padding
        loginButton.layoutXProperty().bind(scene.widthProperty().subtract(240)); // 240px from right
        loginButton.layoutYProperty().bind(scene.heightProperty().subtract(250)); // 250px from bottom

        // Bind text position relative to button
        loginText.layoutXProperty().bind(scene.widthProperty().subtract(190)); // Center text roughly
        loginText.layoutYProperty().bind(scene.heightProperty().subtract(210)); // Text a bit above button bottom

        // Hover effect on the login button and text
        loginButton.setOnMouseEntered(event -> {
            loginButton.setFill(Color.web("#107C87")); // Change button background color on hover
            loginText.setFill(Color.web("#FFFFFF")); // Change text color on hover
            loginButton.setCursor(Cursor.HAND); // Change cursor to hand on hover
        });

        loginButton.setOnMouseExited(event -> {
            loginButton.setFill(Color.web("#FFFFFF")); // Revert button background color
            loginText.setFill(Color.web("#107C87")); // Revert text color
            loginButton.setCursor(Cursor.DEFAULT); // Revert cursor to default
        });

        // Add the same hover effect for the text
        loginText.setOnMouseEntered(event -> {
            loginButton.setFill(Color.web("#107C87")); // Change button background color on hover
            loginText.setFill(Color.web("#FFFFFF")); // Change text color on hover
            loginText.setCursor(Cursor.HAND); // Change cursor to hand on hover
        });

        loginText.setOnMouseExited(event -> {
            loginButton.setFill(Color.web("#FFFFFF")); // Revert button background color
            loginText.setFill(Color.web("#107C87")); // Revert text color
            loginText.setCursor(Cursor.DEFAULT); // Revert cursor to default
        });

        // Click event for both button and text
        loginButton.setOnMouseClicked(event -> handleLoginClick(primaryStage));  // Pass primaryStage to handleLoginClick
        loginText.setOnMouseClicked(event -> handleLoginClick(primaryStage));    // Pass primaryStage to handleLoginClick

        // Stage setup
        primaryStage.setTitle("HomePage");
        primaryStage.setScene(scene);

        // Optional: set starting window position
        primaryStage.setX(130);
        primaryStage.setY(25);
        

        // APPLY ICON HERE
        IconHelper.applyIcon(primaryStage);

        primaryStage.show();
    }

    /**
     * The main entry point of the application.
     * Launches the JavaFX application by calling the start method.
     *
     * @param args command-line arguments 
     */
    public static void main(String[] args) {
        launch(args);
    }
}
