package ticktocktrack.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import ticktocktrack.database.DatabaseDashboard;
import ticktocktrack.database.DatabaseRegisterClass;

public class TeacherDashboardCenterPanel {

    public static Pane createPanel() {
        // Create the center panel
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setLayoutX(0);
        centerPanel.setLayoutY(0);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        
        // Shadow image
        String shadowPath = TeacherDashboardCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);
        
     // Load Teacher Background image
        String teacherBgPath = TeacherDashboardCenterPanel.class.getResource("/resources/Teacher_Dashboard/Teacher_bg.png").toExternalForm();
        ImageView teacherBgView = new ImageView(new Image(teacherBgPath));
        teacherBgView.setFitWidth(1000);   // Adjust the width as needed
        teacherBgView.setFitHeight(210);   // Adjust the height as needed
        teacherBgView.setLayoutX(20);      // X position
        teacherBgView.setLayoutY(50);      // Y position

        // Load Teacher Effects image
        String teacherEffectsPath = TeacherDashboardCenterPanel.class.getResource("/resources/Teacher_Dashboard/Teacher_effects.png").toExternalForm();
        ImageView teacherEffectsView = new ImageView(new Image(teacherEffectsPath));
        teacherEffectsView.setFitWidth(490);  // Set the width
        teacherEffectsView.setFitHeight(275); // Set the height
        teacherEffectsView.setLayoutX(530);   // X position
        teacherEffectsView.setLayoutY(-5);     // Y position

      
        UsersModel currentUser = Session.getCurrentUser();
        String fullName = (currentUser != null) ? currentUser.getFullName().trim() : "Teacher";
        if (fullName.isEmpty()) fullName = "Teacher";
        
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String formattedDate = currentDate.format(formatter);
        
        Text dateText = new Text(formattedDate);
        dateText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));
        dateText.setFill(Color.web("#FFFFFF"));
        dateText.setLayoutX(70);
        dateText.setLayoutY(101);
        
        

        Text dashboardTitle = new Text("Welcome Teacher \n" + fullName + "!");
        dashboardTitle.setFont(Font.font("Bakbak One", FontWeight.BOLD, 22));
        dashboardTitle.setFill(Color.web("#FFFFFF"));
        dashboardTitle.setLayoutX(70);
        dashboardTitle.setLayoutY(200);


        int teacherId = (currentUser != null && currentUser.getTeacherId() != null) ? currentUser.getTeacherId() : -1;
        int totalClasses = DatabaseDashboard .getTotalClassesByTeacher(teacherId);
        int totalStudents = DatabaseDashboard .getTotalUniqueStudentsByTeacher(teacherId);

        Pane classPanel = createBoxPanel(50, 290, 300, 120, "Total Classes", totalClasses);
        Pane studentsPanel = createBoxPanel(290, 290, 300, 120, "Number of Students", totalStudents);


        // Add panels to the center panel
        centerPanel.getChildren().addAll(shadowView,  teacherBgView, teacherEffectsView, dashboardTitle, dateText, classPanel, studentsPanel);

        return centerPanel;
    }

    // Helper method to create a styled box panel with a label and the relevant number (e.g., days or classes)
    private static Pane createBoxPanel(double x, double y, double width, double height, String labelText, int value) {
        Pane box = new Pane();
        box.setPrefSize(200, 100);
        box.setLayoutX(x);
        box.setLayoutY(y);
        box.setStyle(
            "-fx-background-color: #f0f0f0;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #cccccc;" +
            "-fx-border-radius: 15;" +
            "-fx-border-width: 1;"
        );

        // Title label
        Text label = new Text(labelText);
        label.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        label.setFill(Color.web("#02383E"));
        label.setLayoutX(20);
        label.setLayoutY(35);

        // Display the relevant value (e.g., days or number of classes)
        Text valueText = new Text(String.valueOf(value)); // Default value 0
        valueText.setFont(Font.font("Poppins", FontWeight.BOLD, 30)); // Large number font
        // Set color based on the type (Attendance panels = Blue, Classes and Students panel = Orange)
        if (labelText.contains("Class")) {
            valueText.setFill(Color.web("#FF9800")); // Orange for total classes
        } else if (labelText.contains("Student")) {
            valueText.setFill(Color.web("#FF9800")); // Orange for number of students
        } else {
            valueText.setFill(Color.web("#009688")); // Blue for other statistics
        }
        valueText.setLayoutX(20);
        valueText.setLayoutY(70); // Positioning the number below the label

        box.getChildren().addAll(label, valueText);

        return box;
    }
}
