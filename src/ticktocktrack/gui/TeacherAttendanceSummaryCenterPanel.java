package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TeacherAttendanceSummaryCenterPanel {

    private static final String SUBJECT_ICON = TeacherAttendanceSummaryCenterPanel.class
            .getResource("/resources/Subject_icon.png").toExternalForm();
    private static final String DASHBOARD_BG = TeacherAttendanceSummaryCenterPanel.class
            .getResource("/resources/Teacher_Dashboard/Teacher_Attendance_summary.png").toExternalForm();

    private static final String COMBO_STYLE =
            "-fx-background-color: white;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 0.7;" +
            "-fx-font-family: 'Poppins';" +
            "-fx-font-weight: normal;" +
            "-fx-text-fill: black;";

    private static final String[] SUBJECT_COLORS = {"#8B43BC", "#BA8200", "#147F8A", "#55DC93"};
    private static final Color VIEW_GLOW_COLOR = Color.web("#8B43BC");

    public static Pane createPanel(int studentId) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1300, 750);
        root.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        root.setTop(buildHeader());

        StackPane content = new StackPane();
        content.setPadding(new Insets(20, 20, 20, 50));
        content.getChildren().add(buildSubjectGrid(content));
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

        headerPane.getChildren().addAll(shadow);
        return headerPane;
    }

    private static Pane buildSubjectGrid(StackPane content) {
        Pane layout = new Pane();

        Text summaryTitle = new Text("Attendance Summary");
        summaryTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 26));
        summaryTitle.setFill(Color.web("#02383E"));
        summaryTitle.setLayoutX(325);
        summaryTitle.setLayoutY(-55);
        layout.getChildren().add(summaryTitle);

        double startX = 70;
        double startY = 2;
        double cardWidth = 200;
        double cardHeight = 200;
        double gapX = 20;
        double gapY = 20;

        for (int i = 0; i < 8; i++) {
            VBox card = createSubjectCard("Subject " + (i + 1), i, content);
            double x = startX + (i % 4) * (cardWidth + gapX);
            double y = startY + (i / 4) * (cardHeight + gapY);
            card.setLayoutX(x);
            card.setLayoutY(y);
            layout.getChildren().add(card);
        }

        return layout;
    }

    private static VBox createSubjectCard(String name, int idx, StackPane content) {
        ImageView icon = new ImageView(new Image(SUBJECT_ICON));
        icon.setFitWidth(80);
        icon.setFitHeight(80);

        Text label = new Text(name);
        label.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        label.setFill(Color.web("#02383E"));

        Button view = new Button("View");
        view.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
        view.setPrefWidth(100);
        view.setStyle("-fx-background-radius: 5; -fx-background-color: #FFFFFF; -fx-border-color: #8B43BC; -fx-border-width: 1.5; -fx-text-fill: #8B43BC;");
        DropShadow glow = new DropShadow(5, VIEW_GLOW_COLOR);
        glow.setSpread(0.2);
        view.setOnMouseEntered(e -> view.setEffect(glow));
        view.setOnMouseExited(e -> view.setEffect(null));
        view.setOnAction(e -> showDetailView(name, content));

        VBox card = new VBox(10, icon, label, view);
        card.setPrefSize(180, 180);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: " + SUBJECT_COLORS[idx % 4] + "; -fx-border-width: 1.5; -fx-background-radius: 5; -fx-border-radius: 5;");
        return card;
    }

    private static void showDetailView(String subjectName, StackPane content) {
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

        ComboBox<String> year = new ComboBox<>();
        year.setPromptText("Select Year Level");
        year.getItems().addAll("1st Year", "2nd Year", "3rd Year", "4th Year");
        year.setStyle(COMBO_STYLE);
        year.relocate(-960, -50);

        ComboBox<String> section = new ComboBox<>();
        section.setPromptText("Select Section");
        section.getItems().addAll("A", "B", "C", "D");
        section.setStyle(COMBO_STYLE);
        section.relocate(-545, -50);

        Text attendance = new Text("Attendance Summary");
        attendance.setFont(Font.font("Poppins", FontWeight.BOLD, 25));
        attendance.setFill(Color.web("#02383E"));
        attendance.relocate(-300, -120);

        TableView<AttendanceRecord> table = createAttendanceTable();
        table.setPrefSize(545, 400);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.relocate(-960, 5);

        Button ok = new Button("OK");
        ok.setFont(Font.font("Poppins", FontWeight.MEDIUM, 12));
        ok.setPrefWidth(85);
        ok.setPrefHeight(20);
        ok.setStyle("-fx-background-color: white;-fx-text-fill: #02383E;-fx-border-color: #8B43BC;-fx-border-width: 1;-fx-border-radius: 2;-fx-background-radius: 2;");
        ok.relocate(-960, 540);
        ok.setOnAction(e -> content.getChildren().setAll(buildSubjectGrid(content)));

        centerPane.getChildren().addAll(titleBox, section, year, table, ok, attendance);
        detail.setCenter(centerPane);

        content.getChildren().setAll(detail);
    }

    private static TableView<AttendanceRecord> createAttendanceTable() {
        TableView<AttendanceRecord> table = new TableView<>();
        table.setEditable(true);

        TableColumn<AttendanceRecord, String> cStudent = new TableColumn<>("Student");
        cStudent.setCellValueFactory(new PropertyValueFactory<>("student"));
        cStudent.setPrefWidth(180);

        TableColumn<AttendanceRecord, String> cStatus = new TableColumn<>("Status");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        cStatus.setPrefWidth(165);

        TableColumn<AttendanceRecord, String> cRemarks = new TableColumn<>("Remarks");
        cRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        cRemarks.setCellFactory(TextFieldTableCell.forTableColumn());
        cRemarks.setPrefWidth(200);

        table.getColumns().addAll(cStudent, cStatus, cRemarks);
        return table;
    }

    public static class AttendanceRecord {
        private final String student;
        private final String status;
        private String remarks;

        public AttendanceRecord(String student, String status, String remarks) {
            this.student = student;
            this.status = status;
            this.remarks = remarks;
        }

        public String getStudent() {
            return student;
        }

        public String getStatus() {
            return status;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}
