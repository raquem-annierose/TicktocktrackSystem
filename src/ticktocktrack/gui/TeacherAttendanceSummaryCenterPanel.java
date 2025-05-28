package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import ticktocktrack.database.DatabaseAttendanceSummary;
import ticktocktrack.database.DatabaseAttendance;
import ticktocktrack.logic.Student;
import ticktocktrack.logic.CourseInfo;

import java.util.List;

/**
 * This class provides the user interface panel for displaying a teacher's attendance summary.
 * It includes styled components such as subject icons, background images, and color-coded
 * attendance cards. This panel allows teachers to view summarized attendance data related
 * to their assigned subjects.
 */
public class TeacherAttendanceSummaryCenterPanel {

    /**
     * The file path for the subject icon image used in the UI.
     */
    private static final String SUBJECT_ICON = TeacherAttendanceSummaryCenterPanel.class
            .getResource("/resources/Subject_icon.png").toExternalForm();

    /**
     * The file path for the dashboard background image used in the teacher attendance summary panel.
     */
    private static final String DASHBOARD_BG = TeacherAttendanceSummaryCenterPanel.class
            .getResource("/resources/Teacher_Dashboard/Teacher_Attendance_summary.png").toExternalForm();

    /**
     * Array of hex color strings used to color-code subjects in the attendance summary.
     */
    private static final String[] SUBJECT_COLORS = {"#8B43BC", "#BA8200", "#147F8A", "#55DC93"};

    /**
     * The color used to create a glowing effect on viewed components within the panel.
     */
    private static final Color VIEW_GLOW_COLOR = Color.web("#8B43BC");

    /**
     * Creates and returns the attendance summary panel for the specified teacher.
     *
     * @param teacherId the unique identifier of the teacher whose attendance summary is to be displayed
     * @return a Pane containing the teacher's attendance summary UI components
     */
    public static Pane createPanel(int teacherId) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1300, 750);
        root.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        root.setTop(buildHeader());

        StackPane content = new StackPane();
	     // REDUCE top padding to pull everything up
	    content.setPadding(new Insets(0, 20, 20, 50)); 
	    content.setPrefHeight(100); // use full height
	
	    ScrollPane scrollPane = new ScrollPane();
	    scrollPane.setContent(buildSubjectGrid(content, teacherId));
	    scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");
	    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	    scrollPane.getStylesheets().add(TeacherAttendanceSummaryCenterPanel.class
	             .getResource("/resources/css/scrollbar.css").toExternalForm());
	    
	     // Optional: slightly increase scroll pane height
	     scrollPane.setPrefHeight(680); // Previously maybe 615 (750 - 135)
	
	     content.getChildren().add(scrollPane);
	     root.setCenter(content);

        return root;
    }

    /**
     * Builds and returns the header pane for the attendance summary panel.
     * The header includes a shadow image and a title text styled appropriately.
     *
     * @return a Pane representing the header section with shadow and title
     */
    private static Pane buildHeader() {
        Pane headerPane = new Pane();
        headerPane.setPrefHeight(135);

        // Shadow image under the header for visual depth effect
        ImageView shadow = new ImageView(new Image(TeacherAttendanceSummaryCenterPanel.class
                .getResource("/resources/SHADOW.png").toExternalForm()));
        shadow.setFitWidth(1300);
        shadow.setFitHeight(250);
        shadow.setLayoutY(-115);

        headerPane.getChildren().addAll(shadow);
        return headerPane;
    }

    /**
     * Builds the grid layout pane that displays the subjects and related attendance
     * information for the given teacher.
     *
     * @param content the StackPane container in which the subject grid will be placed
     * @param teacherId the ID of the teacher whose subjects' attendance data will be shown
     * @return a Pane representing the grid of subjects with attendance summary cards
     */
    private static Pane buildSubjectGrid(StackPane content, int teacherId) {
        Pane layout = new Pane();
        layout.setPadding(new Insets(0, 20, 0, 0));
        layout.prefWidthProperty().bind(content.widthProperty().subtract(20));

        double startX = 40;
        double startY = 0; // Positive startY so cards start below the title
        double cardWidth = 200;
        double cardHeight = 250;
        double gapX = 20;
        double gapY = 30;
        int cardsPerRow = 4;

        CourseInfo[] courses = DatabaseAttendance.getCoursesForTeacher(teacherId);

        for (int i = 0; i < courses.length; i++) {
            CourseInfo course = courses[i];
            String displayName = course.getCourseName() + " - " + course.getSection() + " (" + course.getProgram() + ")";
            VBox card = createSubjectCard(displayName, i, content, teacherId);
            double x = startX + (i % cardsPerRow) * (cardWidth + gapX);
            double y = startY + (i / cardsPerRow) * (cardHeight + gapY);
            card.setLayoutX(x);
            card.setLayoutY(y);
            layout.getChildren().add(card);
        }

        // Adjust height so ScrollPane knows exactly how tall the content is
        int rows = (courses.length + cardsPerRow - 1) / cardsPerRow;  // Ceiling division for rows
        double totalHeight = rows * (cardHeight + gapY) + startY + 70; // Add some bottom padding
        layout.setPrefHeight(totalHeight);

        return layout;
    }

    /**
     * Creates a visual card representing a subject with attendance summary details.
     * Each card displays the subject name, uses a color from a predefined palette,
     * and integrates with the main content pane for interaction.
     *
     * @param name the name of the subject to display on the card
     * @param idx the index of the subject used to select the card's color from a palette
     * @param content the StackPane where the card can add interactive elements or overlays
     * @param teacherId the ID of the teacher to retrieve related attendance data
     * @return a VBox representing the styled subject card UI component
     */
    private static VBox createSubjectCard(String name, int idx, StackPane content, int teacherId) {
        ImageView icon = new ImageView(new Image(SUBJECT_ICON));
        icon.setFitWidth(80);
        icon.setFitHeight(80);

        Text label = new Text(name);
        label.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        label.setFill(Color.web("#02383E"));
        label.setWrappingWidth(160);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setStyle("-fx-text-overrun: ellipsis;");
        Tooltip.install(label, new Tooltip(name));  // Show full name on hover

        Button view = new Button("View");
        view.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        view.setPrefWidth(100);
        view.setStyle(
            "-fx-background-radius: 5; " +
            "-fx-background-color: #FFFFFF; " +
            "-fx-border-color: #8B43BC; " +
            "-fx-border-width: 1.5; " +
            "-fx-text-fill: #8B43BC;"
        );
        DropShadow glow = new DropShadow(5, VIEW_GLOW_COLOR);
        glow.setSpread(0.2);
        view.setOnMouseEntered(e -> view.setEffect(glow));
        view.setOnMouseExited(e -> view.setEffect(null));
        view.setOnAction(e -> showDetailView(name, content, teacherId));

        VBox card = new VBox(10, icon, label, view);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: " + SUBJECT_COLORS[idx % 4] + "; " +
            "-fx-border-width: 1.5; " +
            "-fx-background-radius: 5; " +
            "-fx-border-radius: 5;"
        );

        // Fixed size
        double fixedWidth = 180;
        double fixedHeight = 250;
        card.setPrefSize(fixedWidth, fixedHeight);
        card.setMinSize(fixedWidth, fixedHeight);
        card.setMaxSize(fixedWidth, fixedHeight);

        return card;
    }

    /**
     * Displays a detailed attendance view for the specified subject within the provided content pane.
     * This method fetches attendance data related to the subject and teacher, then populates
     * the content pane with detailed information and visuals.
     *
     * @param subjectName the name of the subject to display detailed attendance for
     * @param content the StackPane where the detailed view will be shown
     * @param teacherId the ID of the teacher whose attendance data is being viewed
     */
    private static void showDetailView(String subjectName, StackPane content, int teacherId) {
        BorderPane detail = new BorderPane();

        Pane left = new Pane();
        ImageView bg = new ImageView(new Image(DASHBOARD_BG));
        bg.setPreserveRatio(true);
        bg.setFitWidth(370);
        bg.setFitHeight(370);
        bg.relocate(600, 1);
        left.getChildren().add(bg);
        detail.setLeft(left);

        Pane centerPane = new Pane();

        ImageView subjectIcon = new ImageView(new Image(SUBJECT_ICON));
        subjectIcon.setFitWidth(26);
        subjectIcon.setFitHeight(26);

        Text title = new Text(subjectName);
        title.setFont(Font.font("Poppins", FontWeight.MEDIUM, 20));
        title.setFill(Color.web("#02383E"));

        HBox titleBox = new HBox(8, subjectIcon, title);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.relocate(-960, -65);

        TableView<AttendanceRecord> table = createAttendanceTable();

        String[] parts = subjectName.split(" - | \\(|\\)");
        String courseName = parts[0].trim();
        String section = parts[1].trim();
        String program = parts[2].trim();

        List<Student> enrolledStudents = DatabaseAttendanceSummary.getStudentsEnrolledForTeacher(courseName, section, program, teacherId);

        for (Student s : enrolledStudents) {
            String fullName = s.getLastName() + ", " + s.getFirstName();
            if (s.getMiddleName() != null && !s.getMiddleName().isEmpty()) {
                fullName += " " + s.getMiddleName();
            }

            int present = DatabaseAttendanceSummary.countPresent(s.getStudentId(), courseName, section, program, teacherId);
            int absent = DatabaseAttendanceSummary.countAbsences(s.getStudentId(), courseName, section, program, teacherId);
            int excused = DatabaseAttendanceSummary.countExcused(s.getStudentId(), courseName, section, program, teacherId);
            int late = DatabaseAttendanceSummary.countLate(s.getStudentId(), courseName, section, program, teacherId);

            String status;
            if (absent <= 2) { 
                status = "Good";
            } else if (absent == 3) {
                status = "Warning";
            } else {
                status = "Critical";
            }


            table.getItems().add(new AttendanceRecord(fullName, status, present, absent, excused, late));
        }

        table.setPrefSize(545, 400);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.relocate(-960, 5);

        Button ok = new Button("OK");
        ok.setFont(Font.font("Poppins", FontWeight.MEDIUM, 12));
        ok.setPrefWidth(85);
        ok.setPrefHeight(20);
        ok.setStyle("-fx-background-color: white;-fx-text-fill: #02383E;-fx-border-color: #8B43BC;-fx-border-width: 1;-fx-border-radius: 2;-fx-background-radius: 2;");
        ok.relocate(-960, 540);
        ok.setOnAction(e -> content.getChildren().setAll(buildSubjectGrid(content, teacherId)));

        centerPane.getChildren().addAll(titleBox, table, ok);
        detail.setCenter(centerPane);
        content.getChildren().setAll(detail);
    }

    /**
     * Creates and configures a TableView for displaying attendance records.
     * The table includes columns such as date, status, and any other relevant
     * attendance information.
     *
     * @return a TableView configured to display AttendanceRecord objects
     */
    private static TableView<AttendanceRecord> createAttendanceTable() {
        TableView<AttendanceRecord> table = new TableView<>();
        table.setEditable(false);

        TableColumn<AttendanceRecord, String> cStudent = new TableColumn<>("Student");
        cStudent.setCellValueFactory(new PropertyValueFactory<>("student"));
        cStudent.setPrefWidth(180);

        TableColumn<AttendanceRecord, String> cStatus = new TableColumn<>("Status");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        cStatus.setPrefWidth(100);

        TableColumn<AttendanceRecord, Integer> cPresent = new TableColumn<>("Present");
        cPresent.setCellValueFactory(new PropertyValueFactory<>("totalPresent"));
        cPresent.setPrefWidth(80);

        TableColumn<AttendanceRecord, Integer> cAbsent = new TableColumn<>("Absent");
        cAbsent.setCellValueFactory(new PropertyValueFactory<>("totalAbsent"));
        cAbsent.setPrefWidth(80);

        TableColumn<AttendanceRecord, Integer> cExcused = new TableColumn<>("Excused");
        cExcused.setCellValueFactory(new PropertyValueFactory<>("totalExcused"));
        cExcused.setPrefWidth(80);
        
        TableColumn<AttendanceRecord, Integer> cLate = new TableColumn<>("Late");
        cLate.setCellValueFactory(new PropertyValueFactory<>("totalLate"));
        cLate.setPrefWidth(80);

        table.getColumns().addAll(cStudent, cStatus, cPresent, cAbsent, cExcused, cLate);
        
        String cssPath = TeacherAttendanceSummaryCenterPanel.class.getResource("/resources/css/adminsummary.css").toExternalForm();
        table.getStylesheets().add(cssPath);
        
        return table;
    }

    /**
     * Represents an attendance record summary for a student.
     * Contains details about the student's attendance status and totals.
     */
    public static class AttendanceRecord {

        /** The student's full name or identifier. */
        private final String student;

        /** The current attendance status (e.g., Present, Absent). */
        private final String status;

        /** Total number of times the student was present. */
        private final int totalPresent;

        /** Total number of times the student was absent. */
        private final int totalAbsent;

        /** Total number of times the student was excused. */
        private final int totalExcused;

        /** Total number of times the student was late. */
        private final int totalLate;

        /**
         * Constructs an AttendanceRecord instance with detailed attendance data.
         *
         * @param student      the name or identifier of the student
         * @param status       the current attendance status (e.g., Present, Absent)
         * @param totalPresent total number of times the student was present
         * @param totalAbsent  total number of times the student was absent
         * @param totalExcused total number of times the student was excused
         * @param totalLate    total number of times the student was late
         */
        public AttendanceRecord(String student, String status, int totalPresent, int totalAbsent, int totalExcused, int totalLate) {
            this.student = student;
            this.status = status;
            this.totalPresent = totalPresent;
            this.totalAbsent = totalAbsent;
            this.totalExcused = totalExcused;
            this.totalLate = totalLate;
        }

        /**
         * Returns the student's name or identifier.
         *
         * @return the student name
         */
        public String getStudent() {
            return student;
        }

        /**
         * Returns the current attendance status.
         *
         * @return the attendance status
         */
        public String getStatus() {
            return status;
        }

        /**
         * Returns the total number of times the student was present.
         *
         * @return total times present
         */
        public int getTotalPresent() {
            return totalPresent;
        }

        /**
         * Returns the total number of times the student was absent.
         *
         * @return total times absent
         */
        public int getTotalAbsent() {
            return totalAbsent;
        }

        /**
         * Returns the total number of times the student was excused.
         *
         * @return total times excused
         */
        public int getTotalExcused() {
            return totalExcused;
        }

        /**
         * Returns the total number of times the student was late.
         *
         * @return total times late
         */
        public int getTotalLate() {
            return totalLate;
        }
    }
}
