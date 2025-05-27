package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ticktocktrack.database.DatabaseDashboard;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.net.URL;

public class AdminDashboardCenterPanel {

    public static Pane createPanel() {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Background and decorative images
        ImageView shadowView = createImageView("/resources/SHADOW.png", 1300, 250, 0, -115);
        ImageView adminBgView = createImageView("/resources/Admin_Dashboard/Admin_bg.png", 1000, 210, 20, 50);
        ImageView adminEffectsView = createImageView("/resources/Admin_Dashboard/Admin_effects.png", 500, 275, 560, 4);
        ImageView adminEffectsIconView = createImageView("/resources/Admin_Dashboard/Admin_effectsicon.png", 140, 140, 510, 110);
        ImageView adminAvatarView = createImageView("/resources/Admin_Dashboard/Admin_avatar.png", 440, 275, 595, -16.5);

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

        // User statistics
        int totalAdmins = DatabaseDashboard.countUsersByRole("admin");
        int totalTeachers = DatabaseDashboard.countUsersByRole("teacher");
        int totalStudents = DatabaseDashboard.countUsersByRole("student");
        int totalCreatedByAdmin = DatabaseDashboard.countAccountsCreatedBy(fullName);

        Text totalUsersText = new Text("Total of Users");
        totalUsersText.setFont(Font.font("Poppins", FontWeight.BOLD, 17));
        totalUsersText.setFill(Color.web("#916500"));
        totalUsersText.setLayoutX(39);
        totalUsersText.setLayoutY(295);

        // Admin
        //createImageView(String resourcePath, double width, double height, double x, double y)
        ImageView adminIcon = createImageView("/resources/admin_icon.png", 60, 60, 39, 375);
        Text adminCount = new Text(String.valueOf(totalAdmins));
        adminCount.setFont(Font.font("Poppins", FontWeight.BOLD, 35));
        adminCount.setFill(Color.web("#BA8200"));
        adminCount.setLayoutX(130);
        adminCount.setLayoutY(375);

        Text adminLabel = new Text("Admin");
        adminLabel.setFont(Font.font("Poppins", 18));
        adminLabel.setFill(Color.web("#916500"));
        adminLabel.setLayoutX(115);
        adminLabel.setLayoutY(425);
        
        Line line1 = new Line(215, 355, 215, 445);
        line1.setStroke(Color.web("#F9CF6C"));
        line1.setStrokeWidth(2);

        // Teacher
        ImageView teacherIcon = createImageView("/resources/teacher_icon.png", 60, 60, 255, 375);
        Text teacherCount = new Text(String.valueOf(totalTeachers));
        teacherCount.setFont(Font.font("Poppins", FontWeight.BOLD, 35));
        teacherCount.setFill(Color.web("#BA8200"));
        teacherCount.setLayoutX(355);
        teacherCount.setLayoutY(375);

        Text teacherLabel = new Text("Teacher");
        teacherLabel.setFont(Font.font("Poppins", 18));
        teacherLabel.setFill(Color.web("#916500"));
        teacherLabel.setLayoutX(335);
        teacherLabel.setLayoutY(425);
        
        Line line2 = new Line(431, 355, 431, 445);
        line2.setStroke(Color.web("#F9CF6C"));
        line2.setStrokeWidth(2);

        // Student
        ImageView studentIcon = createImageView("/resources/student_icon.png", 60, 60, 472, 375);
        Text studentCount = new Text(String.valueOf(totalStudents));
        studentCount.setFont(Font.font("Poppins", FontWeight.BOLD, 35));
        studentCount.setFill(Color.web("#BA8200"));
        studentCount.setLayoutX(555);
        studentCount.setLayoutY(375);

        Text studentLabel = new Text("Student");
        studentLabel.setFont(Font.font("Poppins", 18));
        studentLabel.setFill(Color.web("#916500"));
        studentLabel.setLayoutX(540);
        studentLabel.setLayoutY(425);

        // Created by
        ImageView AdminhammerwrenchIcon = createImageView("/resources/Admin_Dashboard/Admin_hammer_wrench_icon.png", 50, 50, 125, 516);
        Text createdByNumber = new Text(String.valueOf(totalCreatedByAdmin));
        createdByNumber.setFont(Font.font("Poppins", FontWeight.BOLD, 40));
        createdByNumber.setFill(Color.web("#A17102"));
        createdByNumber.setLayoutX(333);
        createdByNumber.setLayoutY(530);

        Text createdByLabel = new Text("Accounts Created by admin");
        createdByLabel.setFont(Font.font("Poppins", 18));
        createdByLabel.setFill(Color.web("#A17102"));
        createdByLabel.setLayoutX(220);
        createdByLabel.setLayoutY(575);

        // Right panel (Holidays)
        Pane rightSidePanel = new Pane();
        rightSidePanel.setPrefSize(355, 330);
        rightSidePanel.setLayoutX(650);
        rightSidePanel.setLayoutY(277);
        rightSidePanel.setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF"), new CornerRadii(15), Insets.EMPTY)));
        rightSidePanel.setBorder(new Border(new BorderStroke(Color.ORANGE, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(1))));
        rightSidePanel.setEffect(new DropShadow(10, Color.rgb(255, 223, 149)));

        Text holidaysTitle = new Text("Holidays");
        holidaysTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        holidaysTitle.setFill(Color.web("#916500"));
        holidaysTitle.setLayoutX(150);
        holidaysTitle.setLayoutY(30);

        String[][] holidays = {
            {"2025-01-01", "New Year's Day"},
            {"2025-01-29", "Chinese New Year"},
            {"2025-02-25", "EDSA Anniversary"},
            {"2025-04-01", "Eid’l Fitr"},
            {"2025-04-09", "Araw ng Kagitingan"},
            {"2025-04-17", "Maundy Thursday"},
            {"2025-04-18", "Good Friday"},
            {"2025-04-19", "Black Saturday"},
            {"2025-05-01", "Labor Day"},
            {"2025-05-12", "National Elections"},
            {"2025-06-06", "Eid’l Adha"},
            {"2025-06-12", "Independence Day"},
            {"2025-07-27", "INC Anniversary"},
            {"2025-08-21", "Ninoy Aquino Day"},
            {"2025-08-25", "National Heroes Day"},
            {"2025-10-31", "Special Day"},
            {"2025-11-01", "All Saints’ Day"},
            {"2025-11-30", "Bonifacio Day"},
            {"2025-12-08", "Feast of the Immaculate Conception"},
            {"2025-12-24", "Christmas Eve"},
            {"2025-12-25", "Christmas Day"},
            {"2025-12-30", "Rizal Day"}
        };

        VBox rightPanelVBox = new VBox(10);
        rightPanelVBox.setPadding(new Insets(10));
        rightPanelVBox.setPrefWidth(330);

        for (String[] holiday : holidays) {
            String date = holiday[0];
            String name = holiday[1];

            HBox holidayBox = new HBox(5);
            holidayBox.setStyle("-fx-background-color: white;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #ccc;" +
                    "-fx-border-radius: 10;" +
                    "-fx-padding: 8;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");

            Region leftAccent = new Region();
            leftAccent.setPrefWidth(3);
            leftAccent.setPrefHeight(25);
            leftAccent.setStyle("-fx-background-color: orange; -fx-background-radius: 3 0 0 3;");

            VBox textBox = new VBox();
            textBox.setPadding(new Insets(0, 8, 0, 8));

            Text title = new Text(name);
            title.setFont(Font.font("Poppins", FontWeight.MEDIUM, 13));
            title.setFill(Color.web("#916500"));

            Text dateText1 = new Text(LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            dateText1.setFont(Font.font("Poppins", 11));
            dateText1.setFill(Color.web("#C49321"));

            textBox.getChildren().addAll(title, dateText1);
            holidayBox.getChildren().addAll(leftAccent, textBox);
            rightPanelVBox.getChildren().add(holidayBox);
        }

        ScrollPane rightPanelScrollPane = new ScrollPane(rightPanelVBox);
        rightPanelScrollPane.setPrefSize(355, 280);
        rightPanelScrollPane.setLayoutY(50);
        rightPanelScrollPane.setFitToWidth(true);
        rightPanelScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightPanelScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        rightPanelScrollPane.getStylesheets().add(
                AdminDashboardCenterPanel.class.getResource("/resources/css/adminscrollB-style.css").toExternalForm()
        );

        rightSidePanel.getChildren().addAll(holidaysTitle, rightPanelScrollPane);

        // Add everything to centerPanel
        centerPanel.getChildren().addAll(
                shadowView, adminBgView, adminEffectsView, adminEffectsIconView, adminAvatarView,
                dashboardTitle, dateText,
                totalUsersText,
                adminIcon, adminCount, adminLabel,
                teacherIcon, teacherCount, teacherLabel,
                studentIcon, studentCount, studentLabel,
                createdByNumber, createdByLabel,
                rightSidePanel, AdminhammerwrenchIcon, line1, line2
        );

        return centerPanel;
    }

    private static ImageView createImageView(String resourcePath, double width, double height, double x, double y) {
        URL resourceUrl = AdminDashboardCenterPanel.class.getResource(resourcePath);
        if (resourceUrl == null) {
            System.err.println("ERROR: Image resource not found: " + resourcePath);
            return new ImageView(); // return empty to prevent crash
        }
        ImageView imageView = new ImageView(new Image(resourceUrl.toExternalForm()));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        return imageView;
    }
}
