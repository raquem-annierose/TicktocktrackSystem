package ticktocktrack.gui;

import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.Cursor;

/**
 * Represents the center panel UI for the admin dashboard section where new users (Faculty and Students) can be created.
 * 
 * <p>This class provides a static method to create the panel, which includes clickable panes for Faculty and Student user creation.
 * Upon clicking, the appropriate registration panel is displayed.</p>
 */
public class AdminCreateUsersCenterPanel {
	
	/**
	 * Provides the center panel UI for the "Create Users" section in the admin dashboard.
	 * 
	 * <p>This panel includes clickable areas for creating Faculty and Student user accounts,
	 * displaying respective registration forms upon selection.</p>
	 * 
	 * @param adminId The ID of the admin user (currently unused in this method).
	 * @return A Pane containing the UI components for user creation.
	 */
    public static Pane createPanel(int adminId) {
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
        Text createUsersTitle = new Text("WHO ARE YOU CREATING TODAY?");
        createUsersTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        createUsersTitle.setFill(Color.web("#BA8200"));
        createUsersTitle.setLayoutX(350);
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

        // Use a single-element array to allow modification inside lambdas
        final Pane[] currentRegistrationPanel = new Pane[1];

        // Faculty Click
        facultyPane.setOnMouseClicked(event -> {
            System.out.println("Faculty pane clicked!");
            if (currentRegistrationPanel[0] != null && centerPanel.getChildren().contains(currentRegistrationPanel[0])) {
                centerPanel.getChildren().remove(currentRegistrationPanel[0]);
            }
            Pane facultyRegistration = AdminUserRegistration.FacultyRegistrationPanel.createPanel(centerPanel);
            currentRegistrationPanel[0] = facultyRegistration;
            centerPanel.getChildren().add(facultyRegistration);
        });

        // Student Click
        studentPane.setOnMouseClicked(event -> {
            System.out.println("Student pane clicked!");
            if (currentRegistrationPanel[0] != null && centerPanel.getChildren().contains(currentRegistrationPanel[0])) {
                centerPanel.getChildren().remove(currentRegistrationPanel[0]);
            }
            Pane studentRegistration = AdminUserRegistration.StudentRegistrationPanel.createPanel(centerPanel);
            currentRegistrationPanel[0] = studentRegistration;
            centerPanel.getChildren().add(studentRegistration);
        });

        // Add all elements
        centerPanel.getChildren().addAll(shadowView, createUsersTitle, facultyPane, studentPane);

        return centerPanel;
    }

    /**
     * Creates a user selection pane with an image and label, styled with hover effects.
     *
     * @param imagePath the resource path to the user image to display
     * @param labelText the text label for the user type (e.g., "Faculty" or "Student")
     * @param layoutX the X-coordinate for the pane's layout position
     * @param layoutY the Y-coordinate for the pane's layout position
     * @return a styled Pane representing a user type selection with hover effects
     */
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
