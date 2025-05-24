package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendanceStatusPanel {

    /**
     * Creates an overlay panel showing attendance status for the given course,
     * today's attendance status, and attendance history.
     * The caller should set layout position and add it to the scene.
     */
    public static Pane createStatusPanel(String courseName, String attendanceStatus, List<AttendanceRecord> history, Runnable onClose) {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(25));
        panel.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #2c3e50; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");
        panel.setPrefSize(950, 550);
        panel.setLayoutX(50);
        panel.setLayoutY(50);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        // --- Top horizontal row ---
        HBox topRow = new HBox(15);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label todayLabel = new Label("Today: " + LocalDate.now().format(dtf));
        todayLabel.setFont(Font.font("Segoe UI", 16));
        todayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");

        Label statusLabel = new Label(attendanceStatus);
        statusLabel.setFont(Font.font("Segoe UI", 16));
        statusLabel.setStyle("-fx-text-fill: #27ae60;"); // green, adjust color by status if needed

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Load image on right
        String imagePath = getImagePathForStatus(attendanceStatus);
        ImageView statusImageView = null;
        if (imagePath != null) {
            Image image = new Image(AttendanceStatusPanel.class.getResource(imagePath).toExternalForm(), 70, 70, true, true);
            statusImageView = new ImageView(image);
            statusImageView.setSmooth(true);
        }

        topRow.getChildren().addAll(todayLabel, statusLabel, spacer);
        if (statusImageView != null) {
            topRow.getChildren().add(statusImageView);
        }

        // --- Header row for history ---
        HBox headerRow = new HBox();
        headerRow.setPadding(new Insets(10, 0, 5, 0));
        headerRow.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 0 0 1 0;");
        headerRow.setPrefWidth(900);

        Label dateHeader = new Label("Date");
        dateHeader.setFont(Font.font("Segoe UI", 14));
        dateHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        dateHeader.setPrefWidth(450);

        Label statusHeader = new Label("Status");
        statusHeader.setFont(Font.font("Segoe UI", 14));
        statusHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        statusHeader.setPrefWidth(450);

        headerRow.getChildren().addAll(dateHeader, statusHeader);

        // --- History list ---
        VBox historyList = new VBox(8);
        historyList.setPrefWidth(900);

        if (history != null && !history.isEmpty()) {
            for (AttendanceRecord record : history) {
                HBox recordRow = new HBox();
                recordRow.setPrefWidth(900);

                Label date = new Label(record.getDate().format(dtf));
                date.setFont(Font.font("Segoe UI", 13));
                date.setPrefWidth(450);
                date.setStyle("-fx-text-fill: #34495e;");

                Label status = new Label(record.getStatus());
                status.setFont(Font.font("Segoe UI", 13));
                status.setPrefWidth(450);
                status.setStyle("-fx-text-fill: #7f8c8d;");

                recordRow.getChildren().addAll(date, status);
                historyList.getChildren().add(recordRow);
            }
        } else {
            Label noHistory = new Label("No attendance history available.");
            noHistory.setFont(Font.font("Segoe UI", 13));
            noHistory.setStyle("-fx-text-fill: #95a5a6;");
            historyList.getChildren().add(noHistory);
        }

        // --- Close button ---
        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        closeBtn.setOnAction(e -> onClose.run());

        panel.getChildren().addAll(topRow, headerRow, historyList, closeBtn);
        return panel;
    }

    private static String getImagePathForStatus(String attendanceStatus) {
        if (attendanceStatus == null) return null;
        switch (attendanceStatus.toLowerCase()) {
            case "present":
                return "/resources/Student_Dashboard/Present_student.png";
            case "absent":
                return "/resources/Student_Dashboard/Absent_student.png";
            case "late":
                return "/resources/Student_Dashboard/Late_student.png";
            case "excused":
                return "/resources/Student_Dashboard/Excused_student.png";
            default:
                return null;
        }
    }

    // Simple DTO class for attendance record
    public static class AttendanceRecord {
        private final LocalDate date;
        private final String status;

        public AttendanceRecord(LocalDate date, String status) {
            this.date = date;
            this.status = status;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getStatus() {
            return status;
        }
    }
}
