package ticktocktrack.gui;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ticktocktrack.database.DatabaseIndividualReport;
import ticktocktrack.logic.ClassAttendanceSummary;
import ticktocktrack.logic.ClassAttendanceSummary.MonthlyAttendanceSummary;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.UsersModel;

public class CardIndividualReport {

    public static void showStudentDetailDialog(Student student) {
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null || currentUser.getTeacherId() == null) {
            System.err.println("No logged-in teacher found.");
            return;
        }
        int teacherId = currentUser.getTeacherId();

        Student fullStudent = DatabaseIndividualReport.getStudentById(student.getStudentId(), teacherId);
        if (fullStudent == null) {
            System.err.println("Student details not found for ID: " + student.getStudentId());
            return;
        }

        List<String> courseNames = DatabaseIndividualReport.getCourseNamesForStudent(fullStudent.getStudentId(), teacherId);
        List<ClassAttendanceSummary> attendanceSummaries = DatabaseIndividualReport.getAttendanceSummaryForStudent(fullStudent.getStudentId(), teacherId);
        List<MonthlyAttendanceSummary> monthlySummaries = DatabaseIndividualReport.getMonthlyAttendanceSummaryForStudent(fullStudent.getStudentId(), teacherId);

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Individual Report");

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f9f9f9;");

        // Header Card with Icon and Name
        HBox profileHeader = new HBox(15);
        profileHeader.setAlignment(Pos.CENTER_LEFT);

        String userIconPath = CardIndividualReport.class.getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm();
        ImageView userIcon = new ImageView(new Image(userIconPath));
        userIcon.setFitWidth(80);
        userIcon.setFitHeight(80);

        VBox studentInfo = new VBox(5);
        studentInfo.getChildren().addAll(
            styledLabel("Name: " + fullStudent.getLastName() + ", " + fullStudent.getFirstName() + " " + fullStudent.getMiddleName()),
            styledLabel("Email: " + fullStudent.getEmail()),
            styledLabel("Program: " + fullStudent.getProgram() + " | Year: " + fullStudent.getYearLevel() + " | Section: " + fullStudent.getSection()),
            styledLabel("Total Classes: " + fullStudent.getTotalClasses())
        );

        profileHeader.getChildren().addAll(userIcon, studentInfo);
        root.getChildren().add(profileHeader);

        root.getChildren().add(new Separator());

        // Course Info
        Label courseLabel = sectionTitle("Enrolled Courses:");
        String coursesText = courseNames.isEmpty() ? "None" : String.join(", ", courseNames);
        Label coursesList = styledLabel(coursesText);

        root.getChildren().addAll(courseLabel, coursesList, new Separator());

        // Monthly Attendance Summary
        root.getChildren().add(sectionTitle("Monthly Attendance Summary:"));
        if (monthlySummaries.isEmpty()) {
            root.getChildren().add(styledLabel("No monthly attendance records found."));
        } else {
            for (MonthlyAttendanceSummary summary : monthlySummaries) {
                String monthName = java.time.Month.of(summary.getMonth()).name();
                String attendanceText = String.format(
                    "%s %d — Present: %d, Absent: %d, Excused: %d, Late: %d",
                    monthName, summary.getYear(),
                    summary.getPresentCount(), summary.getAbsentCount(),
                    summary.getExcusedCount(), summary.getLateCount()
                );
                root.getChildren().add(styledLabel(attendanceText));
            }
        }

        root.getChildren().add(new Separator());

        // Per-Class Attendance Summary
        root.getChildren().add(sectionTitle("Attendance Per Class:"));
        if (attendanceSummaries.isEmpty()) {
            root.getChildren().add(styledLabel("No attendance records found."));
        } else {
            for (ClassAttendanceSummary summary : attendanceSummaries) {
                String classSummary = String.format(
                    "%s — Present: %d, Absent: %d, Excused: %d, Late: %d",
                    summary.getCourseName(),
                    summary.getPresentCount(), summary.getAbsentCount(),
                    summary.getExcusedCount(), summary.getLateCount()
                );
                root.getChildren().add(styledLabel(classSummary));
            }
        }

        Scene scene = new Scene(root, 600, 700);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private static Label styledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        return label;
    }

    private static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        return label;
    }
}
