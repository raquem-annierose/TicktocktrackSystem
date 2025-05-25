package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ticktocktrack.database.DatabaseStatusAttendance;
import ticktocktrack.logic.AttendanceStats;

import java.util.List;

public class StudentAttendanceStatusCenterPanel {

    private static final String SUBJECT_ICON = StudentAttendanceStatusCenterPanel.class
            .getResource("/resources/Subject_icon.png").toExternalForm();

    private static final String[] SUBJECT_COLORS = {"#8B43BC", "#BA8200", "#147F8A", "#55DC93"};
    private static final Color VIEW_GLOW_COLOR = Color.web("#8B43BC");

    public static Pane createPanel(int studentId) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1300, 750);
        root.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        root.setTop(buildHeader());

        StackPane content = new StackPane();
        content.setPadding(new Insets(20, 20, 20, 50));

        List<String> subjects = DatabaseStatusAttendance.getEnrolledStudentSubjects(studentId);

        content.getChildren().add(buildSubjectGrid(content, subjects, studentId));
        root.setCenter(content);
        return root;
    }

    private static Pane buildHeader() {
        Pane headerPane = new Pane();
        headerPane.setPrefHeight(135);

        ImageView shadow = new ImageView(new Image(StudentAttendanceStatusCenterPanel.class
                .getResource("/resources/SHADOW.png").toExternalForm()));
        shadow.setFitWidth(1300);
        shadow.setFitHeight(250);
        shadow.setLayoutY(-115);

        Text title = new Text("Attendance Status");
        title.setFont(Font.font("Poppins", FontWeight.MEDIUM, 25));
        title.setFill(Color.web("#02383E"));
        title.relocate(90, 50);

        headerPane.getChildren().addAll(shadow, title);
        return headerPane;
    }

    private static GridPane buildSubjectGrid(StackPane content, List<String> subjects, int studentId) {
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(40);

        if (subjects.isEmpty()) {
            Text noSubjects = new Text("No enrolled subjects found.");
            noSubjects.setFont(Font.font("Poppins", FontWeight.MEDIUM, 18));
            noSubjects.setFill(Color.web("#666666"));
            grid.add(noSubjects, 0, 0);
            return grid;
        }

        for (int i = 0; i < subjects.size(); i++) {
            String subjectName = subjects.get(i);
            VBox card = createSubjectCard(subjectName, i, studentId);
            grid.add(card, i % 4, i / 4);
        }
        return grid;
    }

    private static VBox createSubjectCard(String name, int idx, int studentId) {
        final double originalWidth = 180;
        final double originalHeight = 280;
        final double expandedWidth = 210;
        final double expandedHeight = 340;

        ImageView icon = new ImageView(new Image(SUBJECT_ICON));
        icon.setFitWidth(80);
        icon.setFitHeight(80);

        VBox.setMargin(icon, new Insets(15, 0, 0, 0));  // 15px top margin

        Text label = new Text(name);
        label.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));
        label.setFill(Color.web("#02383E"));
        VBox.setMargin(label, new Insets(5, 0, 0, 0)); 

        String professorName = DatabaseStatusAttendance.getProfessorNameBySubject(name);
        Text professor = new Text("Professor: " + professorName);
        professor.setFont(Font.font("Poppins", 10));
        professor.setFill(Color.web("#555555"));
        professor.setWrappingWidth(160);
        professor.setVisible(false);

        Text summaryLabel = new Text("Attendance Summary:");
        summaryLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        summaryLabel.setFill(Color.web("#02383E"));
        summaryLabel.setVisible(false);

        Text presentText = new Text("✔ Present: 0");
        Text absentText = new Text("✖ Absent: 0");
        Text lateText = new Text("🕓 Late: 0");
        Text excusedText = new Text("📝 Excused: 0");

        for (Text t : List.of(presentText, absentText, lateText, excusedText)) {
            t.setFont(Font.font("Poppins", 10));
            t.setFill(Color.web("#555555"));
            t.setVisible(false);
        }

        Tooltip.install(presentText, new Tooltip("Number of times you were present."));
        Tooltip.install(absentText, new Tooltip("Number of times you were absent."));
        Tooltip.install(lateText, new Tooltip("Number of times you were late."));
        Tooltip.install(excusedText, new Tooltip("Number of excused absences."));

        Button view = new Button("View");
        view.setFont(Font.font("Poppins", FontWeight.MEDIUM, 12));
        view.setPrefWidth(100);
        view.setStyle("-fx-background-radius: 5; -fx-background-color: #FFFFFF; -fx-border-color: #8B43BC; -fx-border-width: 1.5; -fx-text-fill: #8B43BC;");
        DropShadow glow = new DropShadow(5, VIEW_GLOW_COLOR);
        glow.setSpread(0.2);

        VBox.setMargin(view, new Insets(0, 0, 10, 0));  // bottom margin for collapsed

        view.setOnMouseEntered(e -> view.setEffect(glow));
        view.setOnMouseExited(e -> view.setEffect(null));

        VBox card = new VBox(10,
                icon, label, professor,
                summaryLabel,
                presentText, absentText, lateText, excusedText,
                view);
        card.setPrefWidth(originalWidth);
        card.setMinWidth(originalWidth);
        card.setMaxWidth(originalWidth);
        card.setPrefHeight(originalHeight);
        card.setMinHeight(originalHeight);
        card.setMaxHeight(originalHeight);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: " + SUBJECT_COLORS[idx % 4] + "; -fx-border-width: 1.5; -fx-background-radius: 5; -fx-border-radius: 5;");
        card.setTranslateX(50);
        card.setTranslateY(-3);

        view.setOnAction(e -> {
            boolean expanded = professor.isVisible();
            if (expanded) {
                // Collapse
                professor.setVisible(false);
                summaryLabel.setVisible(false);
                presentText.setVisible(false);
                absentText.setVisible(false);
                lateText.setVisible(false);
                excusedText.setVisible(false);

                // Remove attendanceStatus if exists
                card.getChildren().removeIf(node -> node.getUserData() != null && node.getUserData().equals("attendanceStatus"));

                label.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));
                professor.setFont(Font.font("Poppins", 10));
                summaryLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
                for (Text t : List.of(presentText, absentText, lateText, excusedText)) {
                    t.setFont(Font.font("Poppins", 10));
                }
                view.setFont(Font.font("Poppins", FontWeight.MEDIUM, 12));

                card.setPrefWidth(originalWidth);
                card.setMinWidth(originalWidth);
                card.setMaxWidth(originalWidth);
                card.setPrefHeight(originalHeight);
                card.setMinHeight(originalHeight);
                card.setMaxHeight(originalHeight);
                card.setSpacing(10);

                VBox.setMargin(icon, new Insets(15, 0, 0, 0));
                VBox.setMargin(view, new Insets(0, 0, 10, 0));

                view.setText("View");
            } else {
                // Expand
                AttendanceStats stats = DatabaseStatusAttendance.getAttendanceStats(studentId, name);
                presentText.setText("✔ Present: " + stats.present);
                absentText.setText("✖ Absent: " + stats.absent);
                lateText.setText("🕓 Late: " + stats.late);
                excusedText.setText("📝 Excused: " + stats.excused);

                // Attendance Status Text
                Text attendanceStatus = new Text();
                attendanceStatus.setUserData("attendanceStatus"); // tag for removal later
                attendanceStatus.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
                String status;
                int absent = stats.absent;
                if (absent == 0) {
                    status = "Good";
                    attendanceStatus.setFill(Color.web("#008000")); // green
                } else if (absent <= 2) {
                    status = "Warning";
                    attendanceStatus.setFill(Color.web("#FFA500")); // orange
                } else {
                    status = "Critical";
                    attendanceStatus.setFill(Color.web("#BA0000")); // red
                }
                attendanceStatus.setText("Attendance Status: " + status);

                // Add attendanceStatus text just before summaryLabel if not already present
                if (!card.getChildren().contains(attendanceStatus)) {
                    card.getChildren().add(card.getChildren().indexOf(summaryLabel), attendanceStatus);
                }
                attendanceStatus.setVisible(true);

                professor.setVisible(true);
                summaryLabel.setVisible(true);
                presentText.setVisible(true);
                absentText.setVisible(true);
                lateText.setVisible(true);
                excusedText.setVisible(true);

                label.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
                professor.setFont(Font.font("Poppins", 12));
                summaryLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
                for (Text t : List.of(presentText, absentText, lateText, excusedText)) {
                    t.setFont(Font.font("Poppins", 12));
                }
                view.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));

                card.setPrefWidth(expandedWidth);
                card.setMinWidth(expandedWidth);
                card.setMaxWidth(expandedWidth);
                card.setPrefHeight(expandedHeight);
                card.setMinHeight(expandedHeight);
                card.setMaxHeight(expandedHeight);
                card.setSpacing(8);

                VBox.setMargin(icon, Insets.EMPTY);
                VBox.setMargin(view, Insets.EMPTY);

                view.setText("Hide");
            }
        });

        return card;
    }


}
