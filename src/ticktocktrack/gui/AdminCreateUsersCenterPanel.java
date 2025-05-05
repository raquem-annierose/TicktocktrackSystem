package ticktocktrack.gui;

import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.Cursor;

public class AdminCreateUsersCenterPanel {
	
	
    public static Pane createPanel() {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setLayoutX(0);
        centerPanel.setLayoutY(0);
        centerPanel.setStyle("-fx-background-color: transparent; "
                            + "-fx-border-color: #cccccc; ");

        // Shadow Image
        String shadowPath = AdminCreateUsersCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        // Title
        Text createUsersTitle = new Text("Create Users");
        createUsersTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 36));
        createUsersTitle.setFill(Color.web("#02383E"));
        createUsersTitle.setLayoutX(50);
        createUsersTitle.setLayoutY(70);

        // Faculty Pane
        Pane facultyPane = createUserPane(
            "/resources/Admin_Dashboard/faculty_image.png",
            "Faculty",
            150,
            150
        );

        // Student Pane
        Pane studentPane = createUserPane(
            "/resources/Admin_Dashboard/student_image.png",
            "Student",
            600,
            150
        );

        // Faculty Click
        facultyPane.setOnMouseClicked(event -> {
            System.out.println("Faculty pane clicked!");
            Pane facultyRegistration = AdminUserRegistration.FacultyRegistrationPanel.createPanel();
            centerPanel.getChildren().setAll(shadowView, createUsersTitle, facultyPane, studentPane, facultyRegistration); // replace entire content
        });

        // Student Click
        studentPane.setOnMouseClicked(event -> {
            System.out.println("Student pane clicked!");
            Pane studentRegistration = AdminUserRegistration.StudentRegistrationPanel.createPanel();
            centerPanel.getChildren().setAll(shadowView, createUsersTitle, facultyPane, studentPane, studentRegistration); // replace entire content
        });

        // Add all elements
        centerPanel.getChildren().addAll(shadowView, createUsersTitle, facultyPane, studentPane);

        return centerPanel;
    }

    private static Pane createUserPane(String imagePath, String labelText, double layoutX, double layoutY) {
        Pane pane = new Pane();
        pane.setPrefSize(325, 309);
        pane.setLayoutX(layoutX);
        pane.setLayoutY(layoutY);
        pane.setStyle("-fx-background-color: transparent; "
                     + "-fx-border-color: #cccccc; "
                     + "-fx-border-width: 1px; "
                     + "-fx-border-radius: 20px; "
                     + "-fx-background-radius: 20px;");

        ImageView imageView = new ImageView(new Image(AdminCreateUsersCenterPanel.class.getResource(imagePath).toExternalForm()));
        imageView.setFitWidth(225);
        imageView.setFitHeight(225);
        imageView.setLayoutX(50);
        imageView.setLayoutY(20);

        Text label = new Text(labelText);
        label.setFont(Font.font("Poppins", FontWeight.BOLD, 34));
        label.setFill(Color.web("#02383E"));
        label.setLayoutX(100);
        label.setLayoutY(290);

        pane.getChildren().addAll(imageView, label);

        // Hover Effects
        pane.setOnMouseEntered(event -> {
            pane.setStyle("-fx-background-color: #cce7e7; "
                         + "-fx-border-color: #1e7f7f; "
                         + "-fx-border-width: 1px; "
                         + "-fx-border-radius: 20px; "
                         + "-fx-background-radius: 20px;");
            pane.setCursor(Cursor.HAND);
        });

        pane.setOnMouseExited(event -> {
            pane.setStyle("-fx-background-color: transparent; "
                         + "-fx-border-color: #cccccc; "
                         + "-fx-border-width: 1px; "
                         + "-fx-border-radius: 20px; "
                         + "-fx-background-radius: 20px;");
        });

        return pane;
    }
}
