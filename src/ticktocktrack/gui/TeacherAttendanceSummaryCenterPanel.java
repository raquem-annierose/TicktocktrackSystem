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

public class TeacherAttendanceSummaryCenterPanel {

    private static final String SUBJECT_ICON = TeacherAttendanceSummaryCenterPanel.class
            .getResource("/resources/Subject_icon.png").toExternalForm();
    private static final String DASHBOARD_BG = TeacherAttendanceSummaryCenterPanel.class
            .getResource("/resources/Teacher_Dashboard/Teacher_Attendance_summary.png").toExternalForm();

    private static final String[] SUBJECT_COLORS = {"#8B43BC", "#BA8200", "#147F8A", "#55DC93"};
    private static final Color VIEW_GLOW_COLOR = Color.web("#8B43BC");

    public static Pane createPanel(int teacherId) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1300, 750);
        root.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        root.setTop(buildHeader());

        StackPane content = new StackPane();
	     // REDUCE top padding to pull everything up
	    content.setPadding(new Insets(0, 20, 20, 50)); // was (20, 20, 20, 50) — now top is 0
	    content.setPrefHeight(750); // use full height
	
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

    private static Pane buildHeader() {
        Pane headerPane = new Pane();
        headerPane.setPrefHeight(135);

        ImageView shadow = new ImageView(new Image(TeacherAttendanceSummaryCenterPanel.class
                .getResource("/resources/SHADOW.png").toExternalForm()));
        shadow.setFitWidth(1300);
        shadow.setFitHeight(250);
        shadow.setLayoutY(-115);

        Text summaryTitle = new Text("Attendance Summary");
        summaryTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 26));
        summaryTitle.setFill(Color.web("#02383E"));
        summaryTitle.setLayoutX(50); // adjust as needed for position
        summaryTitle.setLayoutY(75); // adjust for vertical centering

        headerPane.getChildren().addAll(shadow, summaryTitle);
        return headerPane;
    }


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
        titleBox.relocate(-960, -110);

        Text attendance = new Text("Attendance Summary");
        attendance.setFont(Font.font("Poppins", FontWeight.BOLD, 25));
        attendance.setFill(Color.web("#02383E"));
        attendance.relocate(-300, -120);

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

        centerPane.getChildren().addAll(titleBox, table, ok, attendance);
        detail.setCenter(centerPane);
        content.getChildren().setAll(detail);
    }

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
        return table;
    }

    public static class AttendanceRecord {
        private final String student;
        private final String status;
        private final int totalPresent;
        private final int totalAbsent;
        private final int totalExcused;
        private final int totalLate;

        public AttendanceRecord(String student, String status, int totalPresent, int totalAbsent, int totalExcused, int totalLate) {
            this.student = student;
            this.status = status;
            this.totalPresent = totalPresent;
            this.totalAbsent = totalAbsent;
            this.totalExcused = totalExcused;
            this.totalLate = totalLate;
        }

        public String getStudent() {
            return student;
        }

        public String getStatus() {
            return status;
        }

        public int getTotalPresent() {
            return totalPresent;
        }

        public int getTotalAbsent() {
            return totalAbsent;
        }

        public int getTotalExcused() {
            return totalExcused;
        }
        
        public int getTotalLate() {
        	return totalLate;
        }
    }
}
