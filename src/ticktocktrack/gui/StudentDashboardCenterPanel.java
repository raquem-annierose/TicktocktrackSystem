package ticktocktrack.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ticktocktrack.database.DatabaseDashboard;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StudentDashboardCenterPanel {

    public static Pane createPanel() {
        // Create the center panel
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setLayoutX(0);
        centerPanel.setLayoutY(0);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        
        // Shadow image
        String shadowPath = StudentDashboardCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);
        
     // Load Student Background image
        String studentBgPath = StudentDashboardCenterPanel.class.getResource("/resources/Student_Dashboard/Student_bg.png").toExternalForm();
        ImageView studentBgView = new ImageView(new Image(studentBgPath));
        studentBgView.setFitWidth(1000);   // Adjust the width as needed
        studentBgView.setFitHeight(210);   // Adjust the height as needed
        studentBgView.setLayoutX(20);      // X position
        studentBgView.setLayoutY(50);      // Y position

        // Load Student Avatar image
        String studentAvatarPath = StudentDashboardCenterPanel.class.getResource("/resources/Student_Dashboard/Student_avatar.png").toExternalForm();
        ImageView studentAvatarView = new ImageView(new Image(studentAvatarPath));
        studentAvatarView.setFitWidth(440);   // Adjust the size
        studentAvatarView.setFitHeight(285);  // Adjust the size
        studentAvatarView.setLayoutX(595);    // X position
        studentAvatarView.setLayoutY(6);  // Y position

        // Load Student Effects image
        String studentEffectsPath = StudentDashboardCenterPanel.class.getResource("/resources/Student_Dashboard/Student_effects.png").toExternalForm();
        ImageView studentEffectsView = new ImageView(new Image(studentEffectsPath));
        studentEffectsView.setFitWidth(500);  // Adjust the size
        studentEffectsView.setFitHeight(285); // Adjust the size
        studentEffectsView.setLayoutX(560);   // X position
        studentEffectsView.setLayoutY(6);     // Y position
        
        UsersModel currentUser = Session.getCurrentUser();
        String fullName = (currentUser != null) ? currentUser.getFullName().trim() : "Student";
        if (fullName.isEmpty()) fullName = "Student";
        
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String formattedDate = currentDate.format(formatter);
        
        Text dateText = new Text(formattedDate);
        dateText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));
        dateText.setFill(Color.web("#8B43BC"));
        dateText.setLayoutX(70);
        dateText.setLayoutY(101);

       
        // Create the "Student Dashboard" Text
        Text dashboardTitle = new Text("Welcome Student \n" + fullName + "!");
        dashboardTitle.setFont(Font.font("Bakbak One", FontWeight.BOLD, 22));
        dashboardTitle.setFill(Color.web("#8B43BC"));
        dashboardTitle.setLayoutX(70);
        dashboardTitle.setLayoutY(200);

        // Create 3 panels (box holders) with present, absent, and excused days
        double startX = 25;
        double gap = 30;
        double width = 225;
        double height = 120;

        int studentId = currentUser != null ? currentUser.getStudentId() : -1;

        int presentCount = DatabaseDashboard.getAttendanceCountByStatus(studentId, "Present");
        int absentCount = DatabaseDashboard.getAttendanceCountByStatus(studentId, "Absent");
        int excusedCount = DatabaseDashboard.getAttendanceCountByStatus(studentId, "Excused");
        int lateCount = DatabaseDashboard.getAttendanceCountByStatus(studentId, "Late");

        Pane panel1 = createBoxPanel(startX, 290, width, height, "Present Days", presentCount);
        Pane panel2 = createBoxPanel(startX + width + gap, 290, width, height, "Absent Days", absentCount);
        Pane panel3 = createBoxPanel(startX + 2 * (width + gap), 290, width, height, "Excused Days", excusedCount);
        Pane panel4 = createBoxPanel(startX + 3 * (width + gap), 290, width, height, "Late Days", lateCount);

        centerPanel.getChildren().addAll(shadowView, studentBgView, studentEffectsView, studentAvatarView, dashboardTitle, dateText, panel1, panel2, panel3, panel4);

        return centerPanel;
    }

    // Helper method to create a styled box panel with a label and the relevant number of days
    private static Pane createBoxPanel(double x, double y, double width, double height, String labelText, int days) {
        Pane box = new Pane();
        box.setPrefSize(width, height);
        box.setLayoutX(x);
        box.setLayoutY(y);
        box.setStyle(
            "-fx-background-color: #f0f0f0;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #cccccc;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1;"
        );

        // Title label
        Text label = new Text(labelText);
        label.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        label.setFill(Color.web("#02383E"));
        label.setLayoutX(20);
        label.setLayoutY(35);

        // Display the relevant number of days (e.g., present, absent, or excused) as a placeholder
        Text daysText = new Text(String.valueOf(days));
// Placeholder text, to be replaced with actual database data
        daysText.setFont(Font.font("Poppins", FontWeight.BOLD, 36)); // Large number font
        // Set color based on the type (Present = Green, Absent = Red, Excused = Yellow)
        if (labelText.equals("Present Days")) {
            daysText.setFill(Color.web("#009688")); // Teal
        } else if (labelText.equals("Absent Days")) {
            daysText.setFill(Color.web("#FF5722")); // Red-Orange
        } else if (labelText.equals("Excused Days")) {
            daysText.setFill(Color.web("#FFC107")); // Amber
        } else if (labelText.equals("Late Days")) {
            daysText.setFill(Color.web("#673AB7")); // Deep Purple
        }

        daysText.setLayoutX(20);
        daysText.setLayoutY(70); // Positioning the number below the label

        box.getChildren().addAll(label, daysText);

        return box;
    }
    
    
}
