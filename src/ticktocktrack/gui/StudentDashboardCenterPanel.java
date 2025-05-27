package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ticktocktrack.database.DatabaseDashboard;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Provides the main content panel for the Student Dashboard UI.
 * This panel displays the core student-related information and controls.
 */
public class StudentDashboardCenterPanel {

	 /**
     * Creates and returns the main content pane for the student dashboard.
     *
     * @return a Pane containing the student dashboard UI elements
     */
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
        studentBgView.setFitWidth(1000);
        studentBgView.setFitHeight(210);
        studentBgView.setLayoutX(20);
        studentBgView.setLayoutY(50);

        // Load Student Avatar image
        String studentAvatarPath = StudentDashboardCenterPanel.class.getResource("/resources/Student_Dashboard/Student_avatar.png").toExternalForm();
        ImageView studentAvatarView = new ImageView(new Image(studentAvatarPath));
        studentAvatarView.setFitWidth(440);
        studentAvatarView.setFitHeight(285);
        studentAvatarView.setLayoutX(595);
        studentAvatarView.setLayoutY(6);

        // Load Student Effects image
        String studentEffectsPath = StudentDashboardCenterPanel.class.getResource("/resources/Student_Dashboard/Student_effects.png").toExternalForm();
        ImageView studentEffectsView = new ImageView(new Image(studentEffectsPath));
        studentEffectsView.setFitWidth(500);
        studentEffectsView.setFitHeight(285);
        studentEffectsView.setLayoutX(560);
        studentEffectsView.setLayoutY(6);

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

        // Welcome Text
        Text dashboardTitle = new Text("Welcome Student \n" + fullName + "!");
        dashboardTitle.setFont(Font.font("Bakbak One", FontWeight.BOLD, 22));
        dashboardTitle.setFill(Color.web("#8B43BC"));
        dashboardTitle.setLayoutX(70);
        dashboardTitle.setLayoutY(200);

        int studentId = currentUser != null ? currentUser.getStudentId() : -1;

        int presentCount = DatabaseDashboard.getAttendanceCountByStatus(studentId, "Present");
        int absentCount = DatabaseDashboard.getAttendanceCountByStatus(studentId, "Absent");
        int excusedCount = DatabaseDashboard.getAttendanceCountByStatus(studentId, "Excused");
        int lateCount = DatabaseDashboard.getAttendanceCountByStatus(studentId, "Late");

        // Attendance Status Title
        Text attendanceTitle = new Text("HOW I'M DOING");
        attendanceTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 17));
        attendanceTitle.setFill(Color.web("#8B43BC"));
        attendanceTitle.setLayoutX(39);
        attendanceTitle.setLayoutY(295);

        // Present Days
        Text presentLabel = new Text("Present Days");
        presentLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        presentLabel.setFill(Color.web("#65168D"));
        presentLabel.setLayoutX(150);
        presentLabel.setLayoutY(355);

        Text presentCountText = new Text(String.valueOf(presentCount));
        presentCountText.setFont(Font.font("Poppins", FontWeight.BOLD, 35));
        presentCountText.setFill(Color.web("#009688"));
        presentCountText.setLayoutX(150);
        presentCountText.setLayoutY(409);
        
        Line line1 = new Line(315, 345, 315, 420);
        line1.setStroke(Color.web("#C164FF"));
        line1.setStrokeWidth(2);


        // Absent Days
        Text absentLabel = new Text("Absent Days");
        absentLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        absentLabel.setFill(Color.web("#65168D"));
        absentLabel.setLayoutX(415);
        absentLabel.setLayoutY(355);

        Text absentCountText = new Text(String.valueOf(absentCount));
        absentCountText.setFont(Font.font("Poppins", FontWeight.BOLD, 35));
        absentCountText.setFill(Color.web("#FF5722"));
        absentCountText.setLayoutX(415);
        absentCountText.setLayoutY(409);

        // Late Days
        Text lateLabel = new Text("Late Days");
        lateLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        lateLabel.setFill(Color.web("#65168D"));
        lateLabel.setLayoutX(150);
        lateLabel.setLayoutY(500);

        Text lateCountText = new Text(String.valueOf(lateCount));
        lateCountText.setFont(Font.font("Poppins", FontWeight.BOLD, 35));
        lateCountText.setFill(Color.web("#673AB7"));
        lateCountText.setLayoutX(150);
        lateCountText.setLayoutY(550);
        
     // Vertical divider line placed at X=431, starts near top (Y=4) and extends down to Y=500
        Line line2 = new Line(315, 490, 315, 565);
        line2.setStroke(Color.web("#C164FF"));
        line2.setStrokeWidth(2);

        // Excused Days
        Text excusedLabel = new Text("Excused Days");
        excusedLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        excusedLabel.setFill(Color.web("#65168D"));
        excusedLabel.setLayoutX(416);
        excusedLabel.setLayoutY(500);

        Text excusedCountText = new Text(String.valueOf(excusedCount));
        excusedCountText.setFont(Font.font("Poppins", FontWeight.BOLD, 35));
        excusedCountText.setFill(Color.web("#FFC107"));
        excusedCountText.setLayoutX(415);
        excusedCountText.setLayoutY(550);
        
        Pane rightSidePanel = new Pane();
        rightSidePanel.setPrefSize(355, 330);
        rightSidePanel.setLayoutX(650);
        rightSidePanel.setLayoutY(277);
        rightSidePanel.setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF"), new CornerRadii(15), Insets.EMPTY)));
        rightSidePanel.setBorder(new Border(new BorderStroke(Color.PURPLE, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(1))));
        rightSidePanel.setEffect(new DropShadow(10, Color.rgb(155, 89, 182)));

        Text holidaysTitle = new Text("Holidays");
        holidaysTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        holidaysTitle.setFill(Color.web("#65168D"));
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
            leftAccent.setStyle("-fx-background-color: #9B59B6; -fx-background-radius: 3 0 0 3;");
            
            VBox textBox = new VBox();
            textBox.setPadding(new Insets(0, 8, 0, 8));

            Text title = new Text(name);
            title.setFont(Font.font("Poppins", FontWeight.MEDIUM, 13));
            title.setFill(Color.web("#440364"));

            Text dateText1 = new Text(LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            dateText1.setFont(Font.font("Poppins", 11));
            dateText1.setFill(Color.web("#8B43BC"));

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
                AdminDashboardCenterPanel.class.getResource("/resources/css/studentscrollB-style.css").toExternalForm()
        );

        rightSidePanel.getChildren().addAll(holidaysTitle, rightPanelScrollPane);

        // Add everything to the center panel
        centerPanel.getChildren().addAll(
            shadowView,
            studentBgView,
            studentEffectsView,
            studentAvatarView,
            rightSidePanel,
            dashboardTitle,
            line1,
            line2,
            dateText,
            attendanceTitle,
            presentLabel,
            presentCountText,
            absentLabel,
            absentCountText,
            lateLabel,
            lateCountText,
            excusedLabel,
            excusedCountText
        );

        return centerPanel;
    }

}
