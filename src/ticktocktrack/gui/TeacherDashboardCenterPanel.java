package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;
import ticktocktrack.database.DatabaseDashboard;

public class TeacherDashboardCenterPanel {

    public static Pane createPanel() {
        // Root panel
    	
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Shadow
        ImageView shadowView = new ImageView(new Image(
                TeacherDashboardCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm()));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutY(-115);

        // Backgrounds and effects
        ImageView teacherBgView = new ImageView(new Image(
                TeacherDashboardCenterPanel.class.getResource("/resources/Teacher_Dashboard/Teacher_bg.png").toExternalForm()));
        teacherBgView.setFitWidth(1000);
        teacherBgView.setFitHeight(210);
        teacherBgView.setLayoutX(20);
        teacherBgView.setLayoutY(50);

        ImageView teacherEffectsView = new ImageView(new Image(
                TeacherDashboardCenterPanel.class.getResource("/resources/Teacher_Dashboard/Teacher_effects.png").toExternalForm()));
        teacherEffectsView.setFitWidth(490);
        teacherEffectsView.setFitHeight(275);
        teacherEffectsView.setLayoutX(530);
        teacherEffectsView.setLayoutY(-5);

        // User & Date Info
        UsersModel currentUser = Session.getCurrentUser();
        String fullName = (currentUser != null) ? currentUser.getFullName().trim() : "Teacher";
        if (fullName.isEmpty()) fullName = "Teacher";

        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));

        Text dateText = new Text(formattedDate);
        dateText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));
        dateText.setFill(Color.WHITE);
        dateText.setLayoutX(70);
        dateText.setLayoutY(101);

        Text dashboardTitle = new Text("Welcome Teacher \n" + fullName + "!");
        dashboardTitle.setFont(Font.font("Bakbak One", FontWeight.BOLD, 22));
        dashboardTitle.setFill(Color.WHITE);
        dashboardTitle.setLayoutX(70);
        dashboardTitle.setLayoutY(200);

        // Metrics
        int teacherId = (currentUser != null && currentUser.getTeacherId() != null) ? currentUser.getTeacherId() : -1;
        int totalClasses = DatabaseDashboard.getTotalClassesByTeacher(teacherId);
        int totalStudents = DatabaseDashboard.getTotalUniqueStudentsByTeacher(teacherId);

        Text classLabel = new Text("Total Classes");
        classLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        classLabel.setFill(Color.web("#02383E"));
        classLabel.setLayoutX(80);
        classLabel.setLayoutY(300);

        Text classValue = new Text(String.valueOf(totalClasses));
        classValue.setFont(Font.font("Poppins", FontWeight.BOLD, 40));
        classValue.setFill(Color.web("#FF9800"));
        classValue.setLayoutX(110);
        classValue.setLayoutY(365);

        Text studentLabel = new Text("Number of Students");
        studentLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        studentLabel.setFill(Color.web("#02383E"));
        studentLabel.setLayoutX(325);
        studentLabel.setLayoutY(300);

        Text studentValue = new Text(String.valueOf(totalStudents));
        studentValue.setFont(Font.font("Poppins", FontWeight.BOLD, 40));
        studentValue.setFill(Color.web("#FF9800"));
        studentValue.setLayoutX(365);
        studentValue.setLayoutY(365);

        // Vertical Divider (unused in layout due to negative X)
        Line verticalLine = new Line(-390, 25, -390, 115);
        verticalLine.setStroke(Color.LIGHTGRAY);
        verticalLine.setStrokeWidth(2);

     // Holidays Panel
        Pane rightSidePanel = new Pane();
        rightSidePanel.setPrefSize(355, 330);
        rightSidePanel.setLayoutX(650);
        rightSidePanel.setLayoutY(277);
        rightSidePanel.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(15), Insets.EMPTY)));
        rightSidePanel.setBorder(new Border(new BorderStroke(Color.TEAL, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(1))));
        rightSidePanel.setEffect(new DropShadow(10, Color.rgb(190, 242, 249)));

        Text holidaysTitle = new Text("Holidays");
        holidaysTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        holidaysTitle.setFill(Color.web("#055F82"));
        holidaysTitle.setLayoutX(150);
        holidaysTitle.setLayoutY(30);

        String[][] holidays = {
            {"2025-01-01", "New Year's Day"}, {"2025-01-29", "Chinese New Year"}, {"2025-02-25", "EDSA Anniversary"},
            {"2025-04-01", "Eid’l Fitr"}, {"2025-04-09", "Araw ng Kagitingan"}, {"2025-04-17", "Maundy Thursday"},
            {"2025-04-18", "Good Friday"}, {"2025-04-19", "Black Saturday"}, {"2025-05-01", "Labor Day"},
            {"2025-05-12", "National Elections"}, {"2025-06-06", "Eid’l Adha"}, {"2025-06-12", "Independence Day"},
            {"2025-07-27", "INC Anniversary"}, {"2025-08-21", "Ninoy Aquino Day"}, {"2025-08-25", "National Heroes Day"},
            {"2025-10-31", "Special Day"}, {"2025-11-01", "All Saints’ Day"}, {"2025-11-30", "Bonifacio Day"},
            {"2025-12-08", "Feast of the Immaculate Conception"}, {"2025-12-24", "Christmas Eve"},
            {"2025-12-25", "Christmas Day"}, {"2025-12-30", "Rizal Day"}
        };

        VBox rightPanelVBox = new VBox(10);
        rightPanelVBox.setPadding(new Insets(10));
        rightPanelVBox.setPrefWidth(330);

        for (String[] holiday : holidays) {
            String date = holiday[0];
            String name = holiday[1];

            HBox holidayBox = new HBox(5);
            holidayBox.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-border-color: #ccc;
                -fx-border-radius: 10;
                -fx-padding: 8;
                -fx-effect: dropshadow(gaussian, rgba(190, 242, 249), 4, 0, 0, 1);
            """);

            Region leftAccent = new Region();
            leftAccent.setPrefSize(3, 25);
            leftAccent.setStyle("-fx-background-color: #34BCCE; -fx-background-radius: 3 0 0 3;");

            Text title = new Text(name);
            title.setFont(Font.font("Poppins", FontWeight.MEDIUM, 13));
            title.setFill(Color.web("#054882"));

            Text holidayDate = new Text(LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            holidayDate.setFont(Font.font("Poppins", 11));
            holidayDate.setFill(Color.web("#4AABB8"));

            VBox textBox = new VBox(title, holidayDate);
            textBox.setPadding(new Insets(0, 8, 0, 8));

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
                AdminDashboardCenterPanel.class.getResource("/resources/css/teacherscrollB-style.css").toExternalForm()
        );

        rightSidePanel.getChildren().addAll(holidaysTitle, rightPanelScrollPane, verticalLine);

        // Add all nodes to main panel
        centerPanel.getChildren().addAll(
            shadowView,
            teacherBgView,
            teacherEffectsView,
            dashboardTitle,
            dateText,
            classLabel, classValue,
            studentLabel, studentValue,
            rightSidePanel
        );

        return centerPanel;
    }
}
