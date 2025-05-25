package ticktocktrack.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;
import ticktocktrack.database.DatabaseUserProfile;

import java.io.File;

public class UserProfile extends Application {

    private ImageView imageView;
    private File selectedFile;

    private Runnable onProfileUpdated;

    public UserProfile() {}

    public UserProfile(Runnable onProfileUpdated) {
        this.onProfileUpdated = onProfileUpdated;
    }

    public void setOnProfileUpdated(Runnable callback) {
        this.onProfileUpdated = callback;
    }

    @Override
    public void start(Stage primaryStage) {
        UsersModel user = Session.getCurrentUser();

        Label titleLabel = new Label("User Profile");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        if (user != null && user.getProfilePath() != null) {
            try {
                Image image = new Image(user.getProfilePath());
                imageView.setImage(image);
            } catch (Exception e) {
                System.out.println("Failed to load profile image: " + e.getMessage());
            }
        }

        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10));
        infoBox.setAlignment(Pos.CENTER_LEFT);

        if (user != null) {
            String role = user.getRole().toLowerCase();

            Label nameLabel = new Label("Name: " + user.getFullName());
            Label emailLabel = new Label("Email: " + user.getEmail());

            infoBox.getChildren().addAll(nameLabel, emailLabel);

            if (role.equals("student")) {
                Label programLabel = new Label("Program: " + user.getProgram());
                Label sectionLabel = new Label("Section: " + user.getSection());
                Label yearLabel = new Label("Year Level: " + user.getYearLevel());

                infoBox.getChildren().addAll(programLabel, sectionLabel, yearLabel);
            } else if (role.equals("admin") || role.equals("teacher")) {
                Label usernameLabel = new Label("Username: " + user.getUsername());
                infoBox.getChildren().add(usernameLabel);
            }
        } else {
            infoBox.getChildren().add(new Label("No user information available."));
        }

        Button changeProfileBtn = new Button("Change Profile Picture");
        Button saveBtn = new Button("Save Profile Picture");
        Button removeBtn = new Button("Remove Profile Picture");

        // Style definition
        String modernButtonStyle = ""
            + "-fx-background-color: transparent;"
            + "-fx-border-color: #BA8200;"
            + "-fx-border-width: 2px;"
            + "-fx-text-fill: #BA8200;"
            + "-fx-font-size: 12px;"
            + "-fx-font-weight: normal;"
            + "-fx-background-radius: 6;"
            + "-fx-border-radius: 6;"
            + "-fx-padding: 8 12;"
            + "-fx-cursor: hand;";

        // Apply default style
        changeProfileBtn.setStyle(modernButtonStyle);
        saveBtn.setStyle(modernButtonStyle);
        removeBtn.setStyle(modernButtonStyle);

        // Hover effects
        applyHoverEffect(changeProfileBtn, modernButtonStyle);
        applyHoverEffect(saveBtn, modernButtonStyle);
        applyHoverEffect(removeBtn, modernButtonStyle);

        changeProfileBtn.setOnAction(e -> chooseImage());

        saveBtn.setOnAction(e -> {
            if (selectedFile != null && user != null) {
                String absolutePath = selectedFile.toURI().toString();
                user.setProfilePath(absolutePath);

                DatabaseUserProfile.updateProfilePath(user.getUserId(), absolutePath);

                if (onProfileUpdated != null) {
                    onProfileUpdated.run();
                }
            } else {
                showAlert("Please select an image first.");
            }
        });

        removeBtn.setOnAction(e -> {
            if (user != null) {
                imageView.setImage(null);
                user.setProfilePath(null);

                DatabaseUserProfile.removeProfilePath(user.getUserId());

                if (onProfileUpdated != null) {
                    onProfileUpdated.run();
                }

                showAlert("Profile picture removed.");
            } else {
                showAlert("No user information available.");
            }
        });

        HBox buttonsBox = new HBox(10, changeProfileBtn, saveBtn, removeBtn);
        buttonsBox.setAlignment(Pos.CENTER);

        GridPane mainPane = new GridPane();
        mainPane.setHgap(20);
        mainPane.setVgap(10);
        mainPane.setPadding(new Insets(20));
        mainPane.setAlignment(Pos.CENTER);

        mainPane.add(titleLabel, 0, 0, 2, 1);
        mainPane.add(imageView, 0, 1);
        mainPane.add(infoBox, 1, 1);
        mainPane.add(buttonsBox, 0, 2, 2, 1);

        Scene scene = new Scene(mainPane, 500, 300);
        primaryStage.setTitle("User Profile");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedFile = file;
            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void applyHoverEffect(Button button, String baseStyle) {
        button.setOnMouseEntered(e ->
            button.setStyle(baseStyle + "-fx-background-color: #BA8200; -fx-text-fill: white;")
        );
        button.setOnMouseExited(e ->
            button.setStyle(baseStyle)
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
