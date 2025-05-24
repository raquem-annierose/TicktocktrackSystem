package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ticktocktrack.database.DatabaseIndividualReport;
import ticktocktrack.logic.Student;

public class StudentIndividualReportPanel {

    public static Pane createPanel(int studentId) {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: #EEF5F9; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Background shadow
        String shadowPath = StudentIndividualReportPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        // Title
        Text title = new Text("My Attendance Summary");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 36));
        title.setFill(Color.web("#02383E"));
        title.setLayoutX(50);
        title.setLayoutY(70);

        VBox studentSummaryContainer = new VBox(15);
        studentSummaryContainer.setPadding(new Insets(40, 20, 20, 50));
        studentSummaryContainer.setLayoutY(120);
        studentSummaryContainer.setLayoutX(0);

        studentSummaryContainer.setPrefWidth(1200);
        studentSummaryContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Get student data
        Student student = DatabaseIndividualReport.getStudentById(studentId);
        if (student == null) {
            Label errorLabel = new Label("Unable to load student data.");
            errorLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
            errorLabel.setTextFill(Color.RED);
            studentSummaryContainer.getChildren().add(errorLabel);
        } else {
            // Display student information
            Label nameLabel = new Label(
                student.getLastName() + ", " +
                student.getFirstName() + " " +
                student.getMiddleName()
            );
            nameLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
            nameLabel.setTextFill(Color.web("#02383E"));

            Label yearLabel = new Label("Year Level: " + student.getYearLevel());
            yearLabel.setFont(Font.font("Poppins", 14));
            yearLabel.setTextFill(Color.web("#555555"));

            studentSummaryContainer.getChildren().addAll(nameLabel, yearLabel);

            // You can add more attendance summary UI elements here
        }

        centerPanel.getChildren().addAll(title, studentSummaryContainer, shadowView);
        return centerPanel;
    }
}
