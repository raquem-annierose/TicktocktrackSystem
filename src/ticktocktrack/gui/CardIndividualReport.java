package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ticktocktrack.logic.AttendanceSummary;
import ticktocktrack.logic.Student;
import ticktocktrack.database.DatabaseAttendanceSummary;

public class CardIndividualReport {

    public static void showStudentDetailDialog(Student student, String courseName, int teacherId) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Student Details");

        VBox dialogVBox = new VBox(12);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.TOP_LEFT);
        dialogVBox.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1;");

        // Prepare middle initial
        String middleInitial = "";
        if (student.getMiddleName() != null && !student.getMiddleName().isBlank()) {
            middleInitial = student.getMiddleName().substring(0, 1).toUpperCase() + ".";
        }

        Label nameLabel = new Label("Name: " + student.getLastName() + ", " + student.getFirstName() + " " + middleInitial);
        nameLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));

        Label yearLabel = new Label("Year: " + student.getYearLevel());
        yearLabel.setFont(Font.font("Poppins", 14));

        Label programLabel = new Label("Program: " + student.getProgram());
        programLabel.setFont(Font.font("Poppins", 14));

        // Use student's section and program for the query parameters
        AttendanceSummary attendanceSummary = DatabaseAttendanceSummary.getAttendanceSummary(
            student.getStudentId(),
            courseName,
            student.getSection(),
            student.getProgram(),
            teacherId
        );

        Label totalClassesLabel = new Label("Total Classes: " + attendanceSummary.getTotalClasses());
        totalClassesLabel.setFont(Font.font("Poppins", 14));

        Label attendanceSummaryTitle = new Label("Attendance Summary:");
        attendanceSummaryTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 14));

        VBox attendanceDetailsVBox = new VBox(6);
        attendanceDetailsVBox.setPadding(new Insets(0, 0, 0, 15));

        Label presentLabel = new Label("• Present: " + attendanceSummary.getPresent());
        presentLabel.setFont(Font.font("Poppins", 14));

        Label absentLabel = new Label("• Absent: " + attendanceSummary.getAbsent());
        absentLabel.setFont(Font.font("Poppins", 14));

        Label lateLabel = new Label("• Late: " + attendanceSummary.getLate());
        lateLabel.setFont(Font.font("Poppins", 14));

        Label excusedLabel = new Label("• Excused: " + attendanceSummary.getExcused());
        excusedLabel.setFont(Font.font("Poppins", 14));

        attendanceDetailsVBox.getChildren().addAll(presentLabel, absentLabel, lateLabel, excusedLabel);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> dialog.close());

        dialogVBox.getChildren().addAll(
            nameLabel,
            yearLabel,
            programLabel,
            totalClassesLabel,
            attendanceSummaryTitle,
            attendanceDetailsVBox,
            closeButton
        );

        Scene dialogScene = new Scene(dialogVBox, 380, 320);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }
}
