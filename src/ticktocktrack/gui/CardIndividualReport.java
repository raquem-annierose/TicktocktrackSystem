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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ticktocktrack.database.DatabaseIndividualReport;
import ticktocktrack.logic.ClassAttendanceSummary;
import ticktocktrack.logic.ClassAttendanceSummary.MonthlyAttendanceSummary;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.UsersModel;

public class CardIndividualReport {

	public static Pane createStudentDetailOverlay(Student student) {
	    UsersModel currentUser = Session.getCurrentUser();
	    if (currentUser == null || currentUser.getTeacherId() == null) {
	        System.err.println("No logged-in teacher found.");
	        return new VBox(); // empty container
	    }
	    int teacherId = currentUser.getTeacherId();

	    Student fullStudent = DatabaseIndividualReport.getStudentById(student.getStudentId(), teacherId);
	    if (fullStudent == null) {
	        System.err.println("Student details not found for ID: " + student.getStudentId());
	        return new VBox();
	    }

	    List<String> courseNames = DatabaseIndividualReport.getCourseNamesForStudent(fullStudent.getStudentId(), teacherId);
	    List<ClassAttendanceSummary> attendanceSummaries = DatabaseIndividualReport.getAttendanceSummaryForStudent(fullStudent.getStudentId(), teacherId);
	    List<MonthlyAttendanceSummary> monthlySummaries = DatabaseIndividualReport.getMonthlyAttendanceSummaryForStudent(fullStudent.getStudentId(), teacherId);

	    // Overlay root
	    StackPane overlay = new StackPane();
	    overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
	    overlay.setPrefSize(1300, 750);

	    VBox modal = new VBox(15);
	    modal.setPadding(new Insets(25));
	    modal.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 10;");
	    modal.setMaxWidth(750);
	    modal.setAlignment(Pos.TOP_LEFT);

	    Label closeBtn = new Label("✕");
	    closeBtn.setTextFill(Color.web("#333"));
	    closeBtn.setStyle("-fx-font-size: 18px;");
	    closeBtn.setOnMouseClicked(e -> ((Pane)overlay.getParent()).getChildren().remove(overlay));
	    closeBtn.setPadding(new Insets(5));
	    closeBtn.setAlignment(Pos.TOP_RIGHT);

	    HBox closeRow = new HBox(closeBtn);
	    closeRow.setAlignment(Pos.TOP_RIGHT);

	    // Header
	    HBox profileHeader = new HBox(15);
	    profileHeader.setAlignment(Pos.CENTER_LEFT);
	    String userIconPath = CardIndividualReport.class.getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm();
	    ImageView userIcon = new ImageView(new Image(userIconPath));
	    userIcon.setFitWidth(80);
	    userIcon.setFitHeight(80);

	    VBox studentInfo = new VBox(5,
	        styledLabel("Name: " + fullStudent.getLastName() + ", " + fullStudent.getFirstName() + " " + fullStudent.getMiddleName()),
	        styledLabel("Email: " + fullStudent.getEmail()),
	        styledLabel("Program: " + fullStudent.getProgram() + " | Year: " + fullStudent.getYearLevel() + " | Section: " + fullStudent.getSection()),
	        styledLabel("Total Classes: " + fullStudent.getTotalClasses())
	    );

	    profileHeader.getChildren().addAll(userIcon, studentInfo);

	    modal.getChildren().addAll(closeRow, profileHeader, new Separator());

	    // === TWO COLUMNS SETUP ===
	    GridPane grid = new GridPane();
	    grid.setHgap(40);
	    grid.setVgap(10);

	    // LEFT COLUMN (col 0)
	    VBox leftCol = new VBox(10);
	    leftCol.getChildren().add(sectionTitle("Enrolled Courses:"));
	    String coursesText = courseNames.isEmpty() ? "None" : String.join(", ", courseNames);
	    leftCol.getChildren().add(styledLabel(coursesText));
	    leftCol.getChildren().add(new Separator());

	    leftCol.getChildren().add(sectionTitle("Monthly Attendance Summary:"));
	    if (monthlySummaries.isEmpty()) {
	        leftCol.getChildren().add(styledLabel("No monthly attendance records found."));
	    } else {
	        for (MonthlyAttendanceSummary summary : monthlySummaries) {
	            String monthName = java.time.Month.of(summary.getMonth()).name();
	            String attendanceText = String.format(
	                "%s %d — Present: %d, Absent: %d, Excused: %d, Late: %d",
	                monthName, summary.getYear(),
	                summary.getPresentCount(), summary.getAbsentCount(),
	                summary.getExcusedCount(), summary.getLateCount()
	            );
	            leftCol.getChildren().add(styledLabel(attendanceText));
	        }
	    }

	    // RIGHT COLUMN (col 1)
	    VBox rightCol = new VBox(10);
	    rightCol.getChildren().add(sectionTitle("Attendance Per Class:"));
	    if (attendanceSummaries.isEmpty()) {
	        rightCol.getChildren().add(styledLabel("No attendance records found."));
	    } else {
	        for (ClassAttendanceSummary summary : attendanceSummaries) {
	            String classSummary = String.format(
	                "%s — Present: %d, Absent: %d, Excused: %d, Late: %d",
	                summary.getCourseName(),
	                summary.getPresentCount(), summary.getAbsentCount(),
	                summary.getExcusedCount(), summary.getLateCount()
	            );
	            rightCol.getChildren().add(styledLabel(classSummary));
	        }
	    }

	    // Add columns to grid
	    grid.add(leftCol, 0, 0);
	    grid.add(rightCol, 1, 0);

	    modal.getChildren().add(grid);

	    overlay.getChildren().add(modal);
	    StackPane.setAlignment(modal, Pos.CENTER_LEFT);
	    StackPane.setMargin(modal, new Insets(0, 0, 0, 170));

	    return overlay;
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
