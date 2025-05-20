package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class StudentAttendanceStatusCenterPanel {

    // ── resource paths ───────────────────────────────────────────────────────
    private static final String SUBJECT_ICON = StudentAttendanceStatusCenterPanel.class
            .getResource("/resources/Subject_icon.png").toExternalForm();
    private static final String DASHBOARD_BG = StudentAttendanceStatusCenterPanel.class
            .getResource("/resources/Student_Dashboard/Student_Attendance_status.png").toExternalForm();

    // ── easy‑tweak position constants ─────────────────────────────────────────
    private static final double TABLE_X = 40;   // ← change here to move table horizontally
    private static final double TABLE_Y = 20;  // ← change here to move table vertically
    private static final double OK_BTN_X = 450;
    private static final double OK_BTN_Y = 400;

    // ── styling constants ─────────────────────────────────────────────────────
    private static final String COMBO_STYLE = "-fx-background-color: white;" +
            "-fx-font-size: 11px;" + "-fx-padding: 6 12 6 12;" +
            "-fx-background-radius: 5;" + "-fx-border-radius: 5;" +
            "-fx-border-color: black;" + "-fx-border-width: 0.7;" +
            "-fx-font-family: 'Poppins';" + "-fx-font-weight: normal;" +
            "-fx-text-fill: black;";

    private static final String[] SUBJECT_COLORS = {"#8B43BC", "#BA8200", "#147F8A", "#55DC93"};
    private static final Color VIEW_GLOW_COLOR = Color.web("#8B43BC");

    // ──────────────────────────────────────────────────────────────────────────
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

    // ── header ───────────────────────────────────────────────────────────────
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

    // ── subject grid ─────────────────────────────────────────────────────────
    private static GridPane buildSubjectGrid(StackPane content) {
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(40);
        for (int i = 0; i < 8; i++) {
            VBox card = createSubjectCard("Subject " + (i + 1), i, content);
            grid.add(card, i % 4, i / 4);
        }
        return grid;
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
        view.setOnAction(e -> showDetailView(name, content));
        DropShadow glow = new DropShadow(5, VIEW_GLOW_COLOR);
        glow.setSpread(0.2);
        view.setOnMouseEntered(e -> view.setEffect(glow));
        view.setOnMouseExited(e -> view.setEffect(null));

        VBox card = new VBox(10, icon, label, view);
        card.setPrefSize(180, 180);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: " + SUBJECT_COLORS[idx % 4] + "; -fx-border-width: 1.5; -fx-background-radius: 5; -fx-border-radius: 5;");
        card.setTranslateX(50);
        card.setTranslateY(-3);
        return card;
    }

    // ── detail view ──────────────────────────────────────────────────────────
    private static void showDetailView(String subjectName, StackPane content) {
        BorderPane detail = new BorderPane();

        // left illustration
        Pane left = new Pane();
        ImageView bg = new ImageView(new Image(DASHBOARD_BG));
        bg.setPreserveRatio(true);
        bg.setFitWidth(350);
        bg.relocate(-1, -10);
        left.getChildren().add(bg);
        detail.setLeft(left);

        // right free‑layout pane
        Pane free = new Pane();

        Text title = new Text(subjectName);
        title.setFont(Font.font("Poppins", FontWeight.MEDIUM, 16));
        title.setFill(Color.web("#02383E"));
        title.relocate(40, -100);

        Text prof = new Text("Professor Name");
        prof.setFont(Font.font("Poppins", 16));
        prof.setFill(Color.web("#666666"));
        prof.relocate(40, -60);

        ComboBox<String> sem = new ComboBox<>();
        sem.getItems().addAll("1st Semester", "2nd Semester");
        sem.getSelectionModel().selectFirst();
        sem.setStyle(COMBO_STYLE);
        sem.relocate(400, -35);

        TableView<AttendanceRecord> table = createAttendanceTable();
        table.setPrefSize(500, 350);
        table.relocate(TABLE_X, TABLE_Y); // ← use constants

        Button ok = new Button("OK");
        ok.setFont(Font.font("Poppins", FontWeight.MEDIUM, 12));
        ok.setPrefWidth(85);
        ok.setPrefHeight(20);
        
        ok.setStyle(
        	      "-fx-background-color: white;"     
        	    + "-fx-text-fill: #02383E;"          
        	    + "-fx-border-color: #8B43BC;"       
        	    + "-fx-border-width: 1;"             
        	    + "-fx-border-radius: 2;"            
        	    + "-fx-background-radius: 2;"        
        	);
        ok.relocate(OK_BTN_X, OK_BTN_Y);
        ok.setOnAction(e -> content.getChildren().setAll(buildSubjectGrid(content)));

        free.getChildren().addAll(title, prof, sem, table, ok);
        detail.setCenter(free);

        content.getChildren().setAll(detail);
    }

    // ── attendance table factory ─────────────────────────────────────────────
    private static TableView<AttendanceRecord> createAttendanceTable() {
        TableView<AttendanceRecord> table = new TableView<>();

        TableColumn<AttendanceRecord, String> cDate = new TableColumn<>("Date");
        cDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        cDate.setPrefWidth(150);

        TableColumn<AttendanceRecord, String> cStatus = new TableColumn<>("Status");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        cStatus.setPrefWidth(150);

        TableColumn<AttendanceRecord, String> cRemarks = new TableColumn<>("Remarks");
        cRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        cRemarks.setPrefWidth(200);

        table.getColumns().addAll(cDate, cStatus, cRemarks);
        return table;
    }

    // ── simple POJO ──────────────────────────────────────────────────────────
    public static class AttendanceRecord {
        private final String date, status, remarks;
        public AttendanceRecord(String d, String s, String r) { date = d; status = s; remarks = r; }
        public String getDate() { return date; }
        public String getStatus() { return status; }
        public String getRemarks() { return remarks; }
    }
}
