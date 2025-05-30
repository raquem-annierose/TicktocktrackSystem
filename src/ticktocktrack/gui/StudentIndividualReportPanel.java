package ticktocktrack.gui;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;
import ticktocktrack.database.DatabaseAttendance;
import ticktocktrack.database.DatabaseIndividualReport;
import ticktocktrack.database.DatabaseStudentViewMyAttendance;
import ticktocktrack.logic.ClassAttendanceSummary;
import ticktocktrack.logic.CourseInfo;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.UsersModel;

/**
 * This class provides the UI panel for displaying
 * an individual student's report in the student dashboard.
 */
public class StudentIndividualReportPanel {

    /**
     * Creates and returns a Pane containing the individual report
     * for the student with the specified studentId.
     *
     * @param studentId the ID of the student whose report is to be displayed
     * @return a Pane containing the individual student report UI components
     */
    public static Pane createPanel(int studentId) {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: #EEF5F9; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        String shadowPath = StudentIndividualReportPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        Text title = new Text("My Attendance Report");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web("#6D00B5"));
        title.setLayoutX(50);
        title.setLayoutY(70);

        VBox cardContainer = new VBox(20);
        cardContainer.setPadding(new Insets(20));
        cardContainer.setPrefWidth(900);
        cardContainer.setStyle("-fx-background-color: #EEF5F9;");

        ScrollPane scrollPane = new ScrollPane(cardContainer);
        scrollPane.setLayoutX(50);
        scrollPane.setLayoutY(120);
        scrollPane.setPrefWidth(945);
        scrollPane.setPrefHeight(480);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);

        // --- Load student details ---
        UsersModel currentUser = Session.getCurrentUser();
        Student s = null;
        if (currentUser != null && currentUser.getStudentId() != null && currentUser.getStudentId() == studentId) {
            s = new Student();
            s.setStudentId(currentUser.getStudentId());
            s.setFirstName(currentUser.getFirstName());
            s.setMiddleName(currentUser.getMiddleName());
            s.setLastName(currentUser.getLastName());
            s.setYearLevel(currentUser.getYearLevel());
            s.setProfilePath(currentUser.getProfilePath());
            // Add more fields if needed
        }

        if (s != null) {
            VBox studentCard = new VBox(5);
            studentCard.setPadding(new Insets(10));
            studentCard.setStyle(
                "-fx-background-color: #F9F9F9;" +
                "-fx-border-color: #AAAAAA;" +
                "-fx-border-width: 1;" +
                "-fx-background-radius: 4;" +
                "-fx-border-radius: 4;"
            );

            String profilePath = s.getProfilePath();
            ImageView userIcon;
            if (profilePath != null && !profilePath.trim().isEmpty()) {
                try {
                    Image profileImage = new Image(profilePath, true);
                    userIcon = new ImageView(profileImage);
                } catch (Exception e1) {
                    userIcon = new ImageView(new Image(CardIndividualReport.class.getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm()));
                }
            } else {
                userIcon = new ImageView(new Image(CardIndividualReport.class.getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm()));
            }

            userIcon.setFitWidth(60);
            userIcon.setFitHeight(60);
            userIcon.setPreserveRatio(true);
            Circle clip = new Circle(30, 30, 30);
            userIcon.setClip(clip);

            Label nameLabel = new Label(s.getLastName() + ", " + s.getFirstName() + " " + s.getMiddleName());
            nameLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
            nameLabel.setTextFill(Color.web("#02383E"));

            Label yearLabel = new Label("Year Level: " + s.getYearLevel());
            yearLabel.setFont(Font.font("Poppins", 12));
            yearLabel.setTextFill(Color.web("#555555"));

            VBox labelsVBox = new VBox(2, nameLabel, yearLabel);
            HBox contentHBox = new HBox(10, userIcon, labelsVBox);
            contentHBox.setAlignment(Pos.CENTER_LEFT);

            studentCard.getChildren().add(contentHBox);

            cardContainer.getChildren().add(studentCard);

            // --- Show classes as clickable cards ---
            List<String> classList = DatabaseStudentViewMyAttendance.getStudentClassesWithTeachers();
            if (classList.isEmpty()) {
                Label noClass = new Label("No enrolled classes found.");
                noClass.setFont(Font.font("Poppins", 13));
                noClass.setTextFill(Color.GRAY);
                cardContainer.getChildren().add(noClass);
            } else {
                for (String classInfo : classList) {
                    // Format: courseName | teacherName | profilePath
                    String[] parts = classInfo.split("\\|");
                    String courseName = parts[0].trim();
                    String teacherName = parts.length > 1 ? parts[1].trim() : "";
                    String teacherProfile = parts.length > 2 ? parts[2].trim() : "";

                    VBox classCard = new VBox(4);
                    classCard.setPadding(new Insets(12));
                    classCard.setStyle("-fx-background-color: #fff; -fx-border-color: #8B43BC; -fx-border-width: 1.5; -fx-background-radius: 7; -fx-border-radius: 7;");
                    classCard.setCursor(javafx.scene.Cursor.HAND);

                    HBox top = new HBox(10);
                    ImageView teacherIcon;
                    if (!teacherProfile.isEmpty()) {
                        try {
                            teacherIcon = new ImageView(new Image(teacherProfile, true));
                        } catch (Exception e) {
                            teacherIcon = new ImageView(new Image(StudentIndividualReportPanel.class.getResource("/resources/teacher_icon.png").toExternalForm()));
                        }
                    } else {
                        teacherIcon = new ImageView(new Image(StudentIndividualReportPanel.class.getResource("/resources/teacher_icon.png").toExternalForm()));
                    }
                    teacherIcon.setFitWidth(36);
                    teacherIcon.setFitHeight(36);
                    teacherIcon.setPreserveRatio(true);
                    teacherIcon.setClip(new Circle(18, 18, 18));

                    VBox textBox = new VBox(
                        new Label(courseName),
                        new Label(teacherName)
                    );
                    ((Label)textBox.getChildren().get(0)).setFont(Font.font("Poppins", FontWeight.BOLD, 15));
                    ((Label)textBox.getChildren().get(1)).setFont(Font.font("Poppins", 12));
                    ((Label)textBox.getChildren().get(1)).setTextFill(Color.web("#555"));

                    top.getChildren().addAll(teacherIcon, textBox);
                    classCard.getChildren().add(top);

                    // Hover effect
                    classCard.setOnMouseEntered(e -> classCard.setEffect(new DropShadow(10, Color.web("#8B43BC", 0.18))));
                    classCard.setOnMouseExited(e -> classCard.setEffect(null));

                    // Click: show summary for this class
                    classCard.setOnMouseClicked(e -> {
                        showAttendanceSummaryDialog(studentId, courseName);
                    });

                    cardContainer.getChildren().add(classCard);
                }
            }
        } else {
            Label error = new Label("Student data not found.");
            error.setTextFill(Color.RED);
            error.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
            cardContainer.getChildren().add(error);
        }

        centerPanel.getChildren().addAll(title, shadowView, scrollPane);
        return centerPanel;
    }

    /**
     * Displays a dialog or overlay showing the attendance summary
     * for the specified student in the given course.
     *
     * @param studentId the ID of the student whose attendance summary is to be shown
     * @param courseName the name of the course for which to display attendance history
     */
    private static void showAttendanceSummaryDialog(int studentId, String courseName) {
        List<AttendanceStatusPanel.AttendanceRecord> history =
            DatabaseStudentViewMyAttendance.getAttendanceHistoryForCourse(courseName);

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        int present = 0, absent = 0, excused = 0, late = 0;

        for (AttendanceStatusPanel.AttendanceRecord record : history) {
            String status = record.getStatus();
            LocalDate date = record.getDate();
            if (date.getMonthValue() != currentMonth || date.getYear() != currentYear) continue;
            if (status == null) continue;
            switch (status.trim().toLowerCase()) {
                case "present": present++; break;
                case "absent": absent++; break;
                case "excused": excused++; break;
                case "late": late++; break;
            }
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Attendance Summary");
        dialog.setHeaderText(null);

        VBox content = new VBox(18);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30, 40, 30, 40));

        Text header = new Text("Attendance Summary for\n" + courseName + " (" + now.getMonth() + " " + currentYear + ")");
        header.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        header.setFill(Color.web("#02383E"));
        header.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER);

        stats.getChildren().addAll(
            buildStatBox("/resources/Student_Dashboard/Present_student.png", "Present", present, "#188038"),
            buildStatBox("/resources/Student_Dashboard/Absent_student.png", "Absent", absent, "#d93025"),
            buildStatBox("/resources/Student_Dashboard/Excused_student.png", "Excused", excused, "#fbbc04"),
            buildStatBox("/resources/Student_Dashboard/Late_student.png", "Late", late, "#a142f4")
        );

        content.getChildren().addAll(header, stats);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /**
     * Creates a styled VBox containing an icon, a label, and a count,
     * with a specified color theme.
     *
     * @param iconPath  the path to the icon image resource
     * @param label     the descriptive text label
     * @param count     the numeric value to display
     * @param colorHex  the hex color code used for styling text and icon
     * @return a VBox containing the formatted icon, label, and count
     */
    private static VBox buildStatBox(String iconPath, String label, int count, String colorHex) {
        ImageView icon;
        try {
            icon = new ImageView(new Image(StudentIndividualReportPanel.class.getResource(iconPath).toExternalForm()));
        } catch (Exception e) {
            icon = new ImageView();
        }
        icon.setFitWidth(38);
        icon.setFitHeight(38);

        Label countLabel = new Label(String.valueOf(count));
        countLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        countLabel.setTextFill(Color.web(colorHex));

        Label textLabel = new Label(label);
        textLabel.setFont(Font.font("Poppins", 13));
        textLabel.setTextFill(Color.web("#555"));

        VBox box = new VBox(6, icon, countLabel, textLabel);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #f5f5fa; -fx-background-radius: 10; -fx-padding: 10 18 10 18;");
        return box;
    }
}