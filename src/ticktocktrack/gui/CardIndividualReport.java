package ticktocktrack.gui;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ticktocktrack.database.DatabaseIndividualReport;
import ticktocktrack.logic.ClassAttendanceSummary;
import ticktocktrack.logic.ClassAttendanceSummary.MonthlyAttendanceSummary;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.UsersModel;

/**
 * Provides UI components related to displaying detailed individual student reports.
 * Includes functionality to create overlays showing student details accessible to teachers.
 */
public class CardIndividualReport {

	/**
	 * Creates an overlay panel displaying detailed information about a specific student.
	 * Ensures that a logged-in teacher is available before proceeding.
	 *
	 * @param student The student whose details will be shown.
	 * @return A Pane containing the student detail UI overlay, or an empty container if no teacher is logged in.
	 */
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

        VBox modalContent = new VBox(10);
        // VERY IMPORTANT: reduce top padding to 0 or small value to make content start at very top
        modalContent.setPadding(new Insets(5, 30, 10, 30)); // Top padding 5px only
        modalContent.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.12), 12, 0, 0, 4);"
        );
        modalContent.setMaxWidth(900);
        modalContent.setAlignment(Pos.TOP_LEFT);

        Label closeBtn = new Label("✕");
        closeBtn.setTextFill(Color.web("#555"));
        closeBtn.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-cursor: hand;");
        closeBtn.setOnMouseEntered(e -> closeBtn.setTextFill(Color.web("#e74c3c")));
        closeBtn.setOnMouseExited(e -> closeBtn.setTextFill(Color.web("#555")));
        closeBtn.setOnMouseClicked(e -> ((Pane)overlay.getParent()).getChildren().remove(overlay));
        closeBtn.setPadding(new Insets(5));

        HBox closeRow = new HBox(closeBtn);
        closeRow.setAlignment(Pos.TOP_RIGHT);

        // Header
        HBox profileHeader = new HBox(10);
        profileHeader.setAlignment(Pos.CENTER_LEFT);

        ImageView userIcon;
        if (fullStudent.getProfilePath() != null && !fullStudent.getProfilePath().isEmpty()) {
            try {
                Image profileImage = new Image(fullStudent.getProfilePath(), true);
                userIcon = new ImageView(profileImage);
            } catch (Exception e) {
                System.err.println("Failed to load profile image: " + e.getMessage());
                userIcon = new ImageView(new Image(CardIndividualReport.class.getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm()));
            }
        } else {
            userIcon = new ImageView(new Image(CardIndividualReport.class.getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm()));
        }
        userIcon.setFitWidth(90);
        userIcon.setFitHeight(90);
        Circle clip = new Circle(45, 45, 45);
        userIcon.setClip(clip);

        VBox studentInfo = new VBox(8,
            styledLabelBold("Name: " + fullStudent.getLastName() + ", " + fullStudent.getFirstName() + " " + fullStudent.getMiddleName()),
            styledLabel("Email: " + fullStudent.getEmail()),
            styledLabel("Program: " + fullStudent.getProgram() + " | Year: " + fullStudent.getYearLevel() + " | Section: " + fullStudent.getSection()),
            styledLabel("Total Classes: " + fullStudent.getTotalClasses())
        );
        studentInfo.setTranslateX(15);


        profileHeader.getChildren().addAll(userIcon, studentInfo);

        modalContent.getChildren().addAll(closeRow, profileHeader, new Separator());

        // === TWO COLUMNS SETUP ===
        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(20);

        // LEFT COLUMN (col 0)
        VBox leftCol = new VBox(15);
        leftCol.setPrefWidth(350);
        leftCol.getChildren().add(sectionTitle("Enrolled Courses:"));
        FlowPane coursesFlow = new FlowPane();
        coursesFlow.setHgap(8);
        coursesFlow.setVgap(6);
        coursesFlow.setPrefWrapLength(320); // wrap width to arrange nicely

        if (courseNames.isEmpty()) {
            coursesFlow.getChildren().add(styledLabel("None"));
        } else {
            for (String course : courseNames) {
                Label courseLabel = new Label(course);
                courseLabel.setStyle(
                    "-fx-padding: 5 12 5 12;" +
                    "-fx-background-color: #e0e7ff;" +
                    "-fx-text-fill: #1e40af;" +
                    "-fx-background-radius: 10;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: 600;"
                );
                coursesFlow.getChildren().add(courseLabel);
            }
        }
        leftCol.getChildren().add(coursesFlow);
        leftCol.getChildren().add(new Separator());

        leftCol.getChildren().add(sectionTitle("Monthly Attendance Summary:"));
        if (monthlySummaries.isEmpty()) {
            leftCol.getChildren().add(styledLabel("No monthly attendance records found."));
        } else {
            for (MonthlyAttendanceSummary summary : monthlySummaries) {
                String monthName = java.time.Month.of(summary.getMonth()).name();
                int total = summary.getPresentCount() + summary.getAbsentCount() + summary.getExcusedCount() + summary.getLateCount();
                int attended = summary.getPresentCount() + summary.getExcusedCount(); // Consider excused as attended
                double attendancePercent = total == 0 ? 0 : ((double) attended / total) * 100;

                String attendanceText = String.format(
                    "%s %d — Present: %d, Absent: %d, Excused: %d, Late: %d (%.1f%% attendance)",
                    monthName, summary.getYear(),
                    summary.getPresentCount(), summary.getAbsentCount(),
                    summary.getExcusedCount(), summary.getLateCount(),
                    attendancePercent
                );

                HBox monthBox = new HBox(10);
                monthBox.setAlignment(Pos.CENTER_LEFT);

                Label monthLabel = styledLabel(attendanceText);
                monthLabel.setMaxWidth(230);
                monthLabel.setWrapText(true);

                PieChart pie = createAttendanceStatusPieChart(summary);

                pie.setPrefSize(100, 100);
                pie.setLegendVisible(false);
                pie.setLabelsVisible(false);

                monthBox.getChildren().addAll(pie, monthLabel);
                leftCol.getChildren().add(monthBox);
            }
        }

        // RIGHT COLUMN (col 1)
        VBox rightCol = new VBox(15);
        rightCol.setPrefWidth(450);
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
                Label classSummaryLabel = styledLabel(classSummary);
                classSummaryLabel.setWrapText(true);
                classSummaryLabel.setMaxWidth(350);
                rightCol.setPadding(new Insets(0, 0, 0, 30));
                rightCol.getChildren().add(classSummaryLabel);

            }
        }


        // Add columns to grid
        grid.add(leftCol, 0, 0);
        grid.add(rightCol, 1, 0);

        modalContent.getChildren().add(grid);

        // Wrap modalContent in ScrollPane
        ScrollPane scrollPane = new ScrollPane(modalContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false); // Important: don't force vertical fit
        scrollPane.setMaxHeight(500);
        scrollPane.setStyle("-fx-background-color:transparent; -fx-padding: 0;");

        scrollPane.setVvalue(0); // scroll to top initially

        // Outer container to hold scrollpane
        StackPane modalContainer = new StackPane(scrollPane);
        modalContainer.setMaxWidth(800);

        modalContainer.setPadding(Insets.EMPTY);
        StackPane.setAlignment(modalContainer, Pos.TOP_LEFT);
        StackPane.setMargin(modalContainer, new Insets(0, 0, 0, 140));
        modalContainer.setTranslateY(-20); // zero vertical translation for top alignment

        overlay.getChildren().add(modalContainer);

        return overlay;
    }

    /**
     * Creates a PieChart representing the attendance status distribution
     * for a given monthly attendance summary.
     *
     * @param summary The monthly attendance summary containing counts of different attendance statuses.
     * @return A PieChart visualizing present, absent, excused, and late attendance counts.
     */
    private static PieChart createAttendanceStatusPieChart(MonthlyAttendanceSummary summary) {
        int present = summary.getPresentCount();
        int absent = summary.getAbsentCount();
        int excused = summary.getExcusedCount();
        int late = summary.getLateCount();

        int total = present + absent + excused + late;
        int attended = present + excused; // Excused counts as attended here

        PieChart.Data presentSlice = new PieChart.Data("Attended", attended);
        PieChart.Data absentSlice = new PieChart.Data("Absent", absent);
        PieChart.Data lateSlice = new PieChart.Data("Late", late);

        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(presentSlice, absentSlice, lateSlice);

        pieChart.applyCss();
        pieChart.layout();

        if (presentSlice.getNode() != null) presentSlice.getNode().setStyle("-fx-pie-color: #4CAF50;");
        if (absentSlice.getNode() != null) absentSlice.getNode().setStyle("-fx-pie-color: #F44336;");
        if (lateSlice.getNode() != null) lateSlice.getNode().setStyle("-fx-pie-color: #FFC107;");

        pieChart.setLabelsVisible(false);
        pieChart.setLegendVisible(false);

        return pieChart;
    }

    /**
     * Creates a Label with standard font size and color styling.
     *
     * @param text The text to display in the label.
     * @return A styled Label with font size 13px and dark gray text color.
     */
    private static Label styledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");
        return label;
    }

    /**
     * Creates a Label with bold font styling and slightly larger font size.
     *
     * @param text The text to display in the label.
     * @return A styled Label with font size 15px, bold weight, and dark text color.
     */
    private static Label styledLabelBold(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #222;");
        return label;
    }

    /**
     * Creates a section title Label with larger bold font and padding.
     *
     * @param text The section title text.
     * @return A styled Label with font size 16px, bold weight, and top padding.
     */
    private static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #555;");
        label.setPadding(new Insets(10, 0, 0, 0));
        return label;
    }
}
