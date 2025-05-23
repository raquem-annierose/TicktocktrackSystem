package ticktocktrack.gui;

import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ticktocktrack.database.DatabaseDashboard;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;
import javafx.scene.paint.Color;

public class AdminDashboardCenterPanel {

    public static Pane createPanel() {
        // Create the center panel
        Pane centerPanel = new Pane();
        
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setLayoutX(0);
        centerPanel.setLayoutY(0);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Shadow image
        String shadowPath = AdminDashboardCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);
        
     // Admin background image
        String adminBgPath = AdminDashboardCenterPanel.class.getResource("/resources/Admin_Dashboard/Admin_bg.png").toExternalForm();
        ImageView adminBgView = new ImageView(new Image(adminBgPath));
        adminBgView.setFitWidth(1000);
        adminBgView.setFitHeight(210);
        adminBgView.setLayoutX(20);
        adminBgView.setLayoutY(50);
        
        String adminAvatarPath = AdminDashboardCenterPanel.class.getResource("/resources/Admin_Dashboard/Admin_avatar.png").toExternalForm();
        ImageView adminAvatarView = new ImageView(new Image(adminAvatarPath));
        adminAvatarView.setFitWidth(440); // or any size you want
        adminAvatarView.setFitHeight(275);
        adminAvatarView.setLayoutX(595); // adjust X position
        adminAvatarView.setLayoutY(-16.5);  // adjust Y position
        
     // Load Admin Effects image
        String adminEffectsPath = AdminDashboardCenterPanel.class.getResource("/resources/Admin_Dashboard/Admin_effects.png").toExternalForm();
        ImageView adminEffectsView = new ImageView(new Image(adminEffectsPath));
        adminEffectsView.setFitWidth(500); // size you want
        adminEffectsView.setFitHeight(275);
        adminEffectsView.setLayoutX(560); // position X
        adminEffectsView.setLayoutY(4); // position Y
        
     // Load Admin Effects Icon image
        String adminEffectsIconPath = AdminDashboardCenterPanel.class.getResource("/resources/Admin_Dashboard/Admin_effectsicon.png").toExternalForm();
        ImageView adminEffectsIconView = new ImageView(new Image(adminEffectsIconPath));
        adminEffectsIconView.setFitWidth(140); // or any size you want
        adminEffectsIconView.setFitHeight(140);
        adminEffectsIconView.setLayoutX(510); // Adjust X position
        adminEffectsIconView.setLayoutY(110);  // Adjust Y position
        
        UsersModel currentUser = Session.getCurrentUser();
        String fullName = (currentUser != null) ? currentUser.getFullName().trim() : "Admin";
        if (fullName.isEmpty()) fullName = "Admin";
        
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String formattedDate = currentDate.format(formatter);
        
        Text dateText = new Text(formattedDate);
        dateText.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        dateText.setFill(Color.web("#02383E"));
        dateText.setLayoutX(70);
        dateText.setLayoutY(101);
        

        // Create the "Dashboard" Text
        Text dashboardTitle = new Text("Welcome Admin \n" + fullName + "!");
        dashboardTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 30));
        dashboardTitle.setFill(Color.web("#02383E"));
        dashboardTitle.setLayoutX(70);
        dashboardTitle.setLayoutY(200);

        // Create 3 panels (box holders)
        double startX = 20;
        double gap = 95;
        double width = 267;
        double height = 120;
        double panelsY = 290; // <--- move lower (increase Y)
        int totalAdmins = DatabaseDashboard.countUsersByRole("admin");
        int totalTeachers = DatabaseDashboard.countUsersByRole("teacher");
        int totalStudents = DatabaseDashboard.countUsersByRole("student");

        String createdBy = (currentUser != null) ? currentUser.getFullName().trim() : "Admin";
        int totalCreatedByAdmin = DatabaseDashboard.countAccountsCreatedBy(createdBy);

        Pane panel1 = createBoxPanel(startX, panelsY, width, height, "Total Admins User", totalAdmins, "/resources/admin_icon.png");
        Pane panel2 = createBoxPanel(startX + width + gap, panelsY, width, height, "Total Teachers User", totalTeachers, "/resources/teacher_icon.png");
        Pane panel3 = createBoxPanel(startX + 2 * (width + gap), panelsY, width, height, "Total Students User", totalStudents, "/resources/student_icon.png");

        
     // Additional panel: Number of accounts created by admin
        Pane createdAccountsPanel = new Pane();
        createdAccountsPanel.setPrefSize(535, 150);
        createdAccountsPanel.setLayoutX(255); // Centered under the three boxes
        createdAccountsPanel.setLayoutY(430);
        createdAccountsPanel.setStyle(
            "-fx-background-color: #e8f5e9;" +  // light greenish background
            "-fx-background-radius: 20;" +
            "-fx-border-color: #a5d6a7;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 1;"
        );

        // Title label
        Text createdLabel = new Text("Number of Accounts Created by Admin");
        createdLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        createdLabel.setFill(Color.web("#02383E"));
        createdLabel.setLayoutX(30);
        createdLabel.setLayoutY(40);

        // Number value (placeholder)
        Text createdNumber = new Text(String.valueOf(totalCreatedByAdmin)); // You can update this dynamically from the database
        createdNumber.setFont(Font.font("Poppins", FontWeight.BOLD, 40));
        createdNumber.setFill(Color.web("#00796B"));
        createdNumber.setLayoutX(30);
        createdNumber.setLayoutY(85);

        // Optional: Add a decorative icon
        String createdIconPath = AdminDashboardCenterPanel.class.getResource("/resources/Admin_Dashboard/Admin_hammer_wrench_icon.png").toExternalForm();
        ImageView createdIcon = new ImageView(new Image(createdIconPath));
        createdIcon.setFitWidth(60);
        createdIcon.setFitHeight(60);
        createdIcon.setLayoutX(470); // Right end
        createdIcon.setLayoutY(20);

        createdAccountsPanel.getChildren().addAll(createdLabel, createdNumber, createdIcon);

        centerPanel.getChildren().addAll(shadowView, adminBgView, adminEffectsView, adminEffectsIconView, adminAvatarView, dashboardTitle, dateText,  panel1, panel2, panel3, createdAccountsPanel);

        return centerPanel;
    }

    // Helper method to create a styled box panel with a label, number holder, and icon
    private static Pane createBoxPanel(double x, double y, double width, double height, String labelText, int numberValue, String iconResourcePath) {
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

        Text label = new Text(labelText);
        label.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        label.setFill(Color.web("#02383E"));
        label.setLayoutX(20);
        label.setLayoutY(35);

        Text number = new Text(String.valueOf(numberValue));
        number.setFont(Font.font("Poppins", FontWeight.BOLD, 32));
        number.setFill(Color.web("#009688"));
        number.setLayoutX(20);
        number.setLayoutY(90);

        String fullIconPath = AdminDashboardCenterPanel.class.getResource(iconResourcePath).toExternalForm();
        ImageView icon = new ImageView(new Image(fullIconPath));
        icon.setFitWidth(40);
        icon.setFitHeight(40);
        icon.setLayoutX(width - 55);
        icon.setLayoutY(30);

        box.getChildren().addAll(label, number, icon);

        return box;
    }

}
