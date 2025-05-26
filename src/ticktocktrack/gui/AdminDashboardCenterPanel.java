package ticktocktrack.gui;

import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane;
import ticktocktrack.database.DatabaseDashboard;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminDashboardCenterPanel {

    public static Pane createPanel() {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setLayoutX(0);
        centerPanel.setLayoutY(0);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Background and decorative images
        ImageView shadowView = createImageView("/resources/SHADOW.png", 1300, 250, 0, -115);
        ImageView adminBgView = createImageView("/resources/Admin_Dashboard/Admin_bg.png", 1000, 210, 20, 50);
        ImageView adminEffectsView = createImageView("/resources/Admin_Dashboard/Admin_effects.png", 500, 275, 560, 4);
        ImageView adminEffectsIconView = createImageView("/resources/Admin_Dashboard/Admin_effectsicon.png", 140, 140, 510, 110);
        ImageView adminAvatarView = createImageView("/resources/Admin_Dashboard/Admin_avatar.png", 440, 275, 595, -16.5);

        // User info
        UsersModel currentUser = Session.getCurrentUser();
        String fullName = (currentUser != null) ? currentUser.getFullName().trim() : "Admin";
        if (fullName.isEmpty()) fullName = "Admin";

        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));

        Text dateText = new Text(formattedDate);
        dateText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));
        dateText.setFill(Color.web("#A17102"));
        dateText.setLayoutX(70);
        dateText.setLayoutY(101);

        Text dashboardTitle = new Text("Welcome Admin \n" + fullName + "!");
        dashboardTitle.setFont(Font.font("Bakbak One", FontWeight.BOLD, 22));
        dashboardTitle.setFill(Color.web("#A17102"));
        dashboardTitle.setLayoutX(70);
        dashboardTitle.setLayoutY(200);

        // Statistics
        double panelWidth = 267;
        double panelHeight = 120;
        double gap = 95;
        double startX = 50;
        double row1Y = 290;
        double row2Y = 450;

        int totalAdmins = DatabaseDashboard.countUsersByRole("admin");
        int totalTeachers = DatabaseDashboard.countUsersByRole("teacher");
        int totalStudents = DatabaseDashboard.countUsersByRole("student");
        String createdBy = (currentUser != null) ? currentUser.getFullName().trim() : "Admin";
        int totalCreatedByAdmin = DatabaseDashboard.countAccountsCreatedBy(createdBy);

        Pane panel1 = createBoxPanel("Total Admin Users", totalAdmins, "/resources/admin_icon.png", panelWidth, panelHeight);
        panel1.setLayoutX(startX);
        panel1.setLayoutY(row1Y);

        Pane panel2 = createBoxPanel("Total Teacher Users", totalTeachers, "/resources/teacher_icon.png", panelWidth, panelHeight);
        panel2.setLayoutX(325);
        panel2.setLayoutY(row1Y);

        Pane panel3 = createBoxPanel("Total Student Users", totalStudents, "/resources/student_icon.png", panelWidth, panelHeight);
        panel3.setLayoutX(startX);
        panel3.setLayoutY(row2Y);

        Pane createdAccountsPanel = new Pane();
        createdAccountsPanel.setPrefSize(300, 120);
        createdAccountsPanel.setLayoutX(startX + panelWidth + gap);
        createdAccountsPanel.setLayoutY(row2Y);
        createdAccountsPanel.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        Text createdLabel = new Text("Accounts Created by Admin");
        createdLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        createdLabel.setFill(Color.web("#02383E"));
        createdAccountsPanel.setLayoutX(337);
        createdAccountsPanel.setLayoutY(487);
        
        Text createdNumber = new Text(String.valueOf(totalCreatedByAdmin));
        createdNumber.setFont(Font.font("Poppins", FontWeight.BOLD, 32));
        createdNumber.setFill(Color.web("#00796B"));
        createdNumber.setLayoutX(30);
        createdNumber.setLayoutY(50);

        ImageView createdIcon = createImageView("/resources/Admin_Dashboard/Admin_hammer_wrench_icon.png", 50, 50, 190, 20);
        createdAccountsPanel.getChildren().addAll(createdLabel, createdNumber, createdIcon);

        // Holidays right side panel
        // Right panel with holidays
        Pane rightSidePanel = new Pane();
        rightSidePanel.setPrefSize(355, 335); // Increased height to fit title
        rightSidePanel.setLayoutX(650);
        rightSidePanel.setLayoutY(271);
        rightSidePanel.setStyle(
        	    "-fx-background-color: #FFFFFF;" +
        	    "-fx-background-radius: 6;"
        );


        // Title above holidays
        Text holidaysTitle = new Text("Holidays");
        holidaysTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        holidaysTitle.setFill(Color.web("#02383E"));
        holidaysTitle.setLayoutX(143);
        holidaysTitle.setLayoutY(30);
        rightSidePanel.getChildren().add(holidaysTitle);

        String[][] holidays = {
            {"2025-01-01", "New Year's Day", "Regular Holiday"},
            {"2025-01-29", "Chinese New Year", "Special Non-Working Day"},
            {"2025-02-25", "EDSA Anniversary", "Special Working Day"},
            {"2025-04-01", "Eid’l Fitr", "Regular Holiday (Tentative)"},
            {"2025-04-09", "Araw ng Kagitingan", "Regular Holiday"},
            {"2025-04-17", "Maundy Thursday", "Regular Holiday"},
            {"2025-04-18", "Good Friday", "Regular Holiday"},
            {"2025-04-19", "Black Saturday", "Special Non-Working Day"},
            {"2025-05-01", "Labor Day", "Regular Holiday"},
            {"2025-05-12", "National Elections", "Special Non-Working Day"},
            {"2025-06-06", "Eid’l Adha", "Regular Holiday (Tentative)"},
            {"2025-06-12", "Independence Day", "Regular Holiday"},
            {"2025-07-27", "INC Anniversary", "Special Non-Working Day"},
            {"2025-08-21", "Ninoy Aquino Day", "Special Non-Working Day"},
            {"2025-08-25", "National Heroes Day", "Regular Holiday"},
            {"2025-10-31", "Special Day", "Special Non-Working Day"},
            {"2025-11-01", "All Saints’ Day", "Special Non-Working Day"},
            {"2025-11-30", "Bonifacio Day", "Regular Holiday"},
            {"2025-12-08", "Feast of the Immaculate Conception", "Special Non-Working Day"},
            {"2025-12-24", "Christmas Eve", "Special Non-Working Day"},
            {"2025-12-25", "Christmas Day", "Regular Holiday"},
            {"2025-12-30", "Rizal Day", "Regular Holiday"}
        };

        VBox rightPanelVBox = new VBox(10);
        rightPanelVBox.setLayoutX(10);
        rightPanelVBox.setLayoutY(50); // Pushed down to make space for "Holidays" title
        rightPanelVBox.setPrefWidth(330);
        rightPanelVBox.setPrefHeight(50);
        rightPanelVBox.setStyle("-fx-padding: 0;");
        rightPanelVBox.setMaxHeight(Region.USE_COMPUTED_SIZE);

        for (String[] holiday : holidays) {
            String date = holiday[0];
            String name = holiday[1];

            HBox holidayBox = new HBox(3);
            holidayBox.setPrefWidth(300);
            holidayBox.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #ccc;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(178,102,255,0.15), 6, 0, 0, 2);"
            );

            Region leftAccent = new Region();
            leftAccent.setPrefWidth(3);
            leftAccent.setPrefHeight(40);
            leftAccent.setStyle("-fx-background-color: orange; -fx-background-radius: 5 0 0 5;");

            VBox textBox = new VBox();
            textBox.setStyle("-fx-padding: 5 10 5 10;");

            Text title = new Text(name);
            title.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
            title.setFill(Color.web("#4b3f72"));

            Text dateText1 = new Text(LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            dateText1.setFont(Font.font("Poppins", 12));
            dateText1.setFill(Color.web("#7e57c2"));

            textBox.getChildren().addAll(title, dateText1);
            holidayBox.getChildren().addAll(leftAccent, textBox);
            rightPanelVBox.getChildren().add(holidayBox);
        }

        ScrollPane rightPanelScrollPane = new ScrollPane(rightPanelVBox);
        rightPanelScrollPane.setPrefSize(355, 298); // Reduced height to make room for title
        rightPanelScrollPane.setLayoutY(50);
        rightPanelScrollPane.setFitToWidth(true);
        rightPanelScrollPane.setFitToHeight(true);
        rightPanelScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightPanelScrollPane.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: -1;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;"
        );

        rightSidePanel.getChildren().clear();
        rightSidePanel.getChildren().addAll(holidaysTitle, rightPanelScrollPane);

        centerPanel.getChildren().addAll(
            shadowView, adminBgView, adminEffectsView, adminEffectsIconView, adminAvatarView,
            dashboardTitle, dateText,
            panel1, panel2, panel3, createdAccountsPanel,
            rightSidePanel
        );

        return centerPanel;
    }

    private static ImageView createImageView(String resourcePath, double width, double height, double x, double y) {
        String path = AdminDashboardCenterPanel.class.getResource(resourcePath).toExternalForm();
        ImageView imageView = new ImageView(new Image(path));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        return imageView;
    }

    private static Pane createBoxPanel(String labelText, int numberValue, String iconPath, double width, double height) {
        Pane box = new Pane();
        box.setPrefSize(width, height);
        box.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

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

        ImageView icon = createImageView(iconPath, 70, 70, width - 97, height - 116);

        box.getChildren().addAll(label, number, icon);
        return box;
    }
}
