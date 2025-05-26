package ticktocktrack.gui;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import ticktocktrack.database.DatabaseDashboard;
import ticktocktrack.database.DatabaseRegisterClass;

public class TeacherDashboardCenterPanel {

    public static Pane createPanel() {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setLayoutX(0);
        centerPanel.setLayoutY(0);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        String shadowPath = TeacherDashboardCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        String teacherBgPath = TeacherDashboardCenterPanel.class.getResource("/resources/Teacher_Dashboard/Teacher_bg.png").toExternalForm();
        ImageView teacherBgView = new ImageView(new Image(teacherBgPath));
        teacherBgView.setFitWidth(1000);
        teacherBgView.setFitHeight(210);
        teacherBgView.setLayoutX(20);
        teacherBgView.setLayoutY(50);

        String teacherEffectsPath = TeacherDashboardCenterPanel.class.getResource("/resources/Teacher_Dashboard/Teacher_effects.png").toExternalForm();
        ImageView teacherEffectsView = new ImageView(new Image(teacherEffectsPath));
        teacherEffectsView.setFitWidth(490);
        teacherEffectsView.setFitHeight(275);
        teacherEffectsView.setLayoutX(530);
        teacherEffectsView.setLayoutY(-5);

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
        int totalClasses = DatabaseDashboard.getTotalClassesByTeacher(teacherId);
        int totalStudents = DatabaseDashboard.getTotalUniqueStudentsByTeacher(teacherId);

        Text classLabel = new Text("Total Classes");
        classLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        classLabel.setFill(Color.web("#02383E"));
        classLabel.setLayoutX(80);
        classLabel.setLayoutY(300);

        Text classValue = new Text(String.valueOf(totalClasses));
        classValue.setFont(Font.font("Poppins", FontWeight.BOLD, 60));
        classValue.setFill(Color.web("#FF9800"));
        classValue.setLayoutX(110);
        classValue.setLayoutY(365);

        Line verticalLine = new Line();
        verticalLine.setStartX(-390);
        verticalLine.setStartY(25);
        verticalLine.setEndX(-390);
        verticalLine.setEndY(115);
        verticalLine.setStroke(Color.LIGHTGRAY);
        verticalLine.setStrokeWidth(2);

        Text studentLabel = new Text("Number of Students");
        studentLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        studentLabel.setFill(Color.web("#02383E"));
        studentLabel.setLayoutX(325);
        studentLabel.setLayoutY(300);

        Text studentValue = new Text(String.valueOf(totalStudents));
        studentValue.setFont(Font.font("Poppins", FontWeight.BOLD, 60));
        studentValue.setFill(Color.web("#FF9800"));
        studentValue.setLayoutX(365);
        studentValue.setLayoutY(365);

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
        rightSidePanel.getChildren().addAll(holidaysTitle, rightPanelScrollPane, verticalLine);

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
