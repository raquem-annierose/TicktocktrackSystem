package ticktocktrack.gui;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;

public class StudentViewMyAttendanceCenterPanel {

    private static final ObservableList<AttendanceRecord> data = FXCollections.observableArrayList();
    private static final ImageView studentImageView = new ImageView();
    private static final Label statusLabel = new Label();

    public static Pane createPanel() {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: white;");

        // Background shadow
        try {
            String shadowPath = Objects.requireNonNull(StudentViewMyAttendanceCenterPanel.class.getResource("/resources/SHADOW.png")).toExternalForm();
            ImageView shadowView = new ImageView(new Image(shadowPath));
            shadowView.setFitWidth(1300);
            shadowView.setFitHeight(250);
            shadowView.setLayoutY(-115);
            centerPanel.getChildren().add(shadowView);
        } catch (Exception e) {
            System.out.println("Shadow image error: " + e.getMessage());
        }

        // Rectangle
        Rectangle centeredRect = new Rectangle(840, 511);
        centeredRect.setFill(Color.WHITE);
        centeredRect.setStroke(Color.web("#CBCBCB"));
        centeredRect.setStrokeWidth(2);
        centeredRect.setLayoutX((1300 - 1075) / 2.0);
        centeredRect.setLayoutY((750 - 630) / 2.0);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetY(4);
        dropShadow.setRadius(10);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        centeredRect.setEffect(dropShadow);
        centerPanel.getChildren().add(centeredRect);

        // "TODAY:" Label
        Label todayLabel = new Label("TODAY:");
        todayLabel.setFont(Font.font("Poppins", 16));
        todayLabel.setLayoutX(150);
        todayLabel.setLayoutY(90);
        centerPanel.getChildren().add(todayLabel);

        // Status Label
        statusLabel.setFont(Font.font("Poppins", 20));
        statusLabel.setLayoutX(240);
        statusLabel.setLayoutY(88);
        centerPanel.getChildren().add(statusLabel);

        // Student Image
        studentImageView.setFitWidth(120);
        studentImageView.setFitHeight(120);
        studentImageView.setLayoutX(900);
        studentImageView.setLayoutY(100);
        centerPanel.getChildren().add(studentImageView);

        // Table
        TableView<AttendanceRecord> table = new TableView<>();
        table.setPrefSize(780, 300);
        table.setLayoutX(145);
        table.setLayoutY(200);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<AttendanceRecord, String> dateCol = new TableColumn<>("DATE");
        dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty());

        TableColumn<AttendanceRecord, String> statusCol = new TableColumn<>("STATUS");
        statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());

        statusCol.setCellFactory(column -> new TableCell<>() {
            final Label label = new Label();
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    label.setText(status.toUpperCase());
                    label.setFont(Font.font("Poppins", 14));
                    label.setPadding(new Insets(2, 12, 2, 12));
                    label.setStyle(getStyleForStatus(status.toLowerCase()));
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        table.getColumns().addAll(dateCol, statusCol);
        table.setItems(data);

        // Load attendance from DB
        loadAttendanceFromDatabase("123456"); // Replace with student ID

        // Show today's image
        AttendanceRecord today = data.stream().filter(r -> r.getDate().equals(LocalDate.now().toString())).findFirst().orElse(null);
        if (today != null) {
            updateStatusAndImage(today.getStatus());
        }

        // Handle click event for yesterday's or any day
        table.setRowFactory(tv -> {
            TableRow<AttendanceRecord> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty()) {
                    AttendanceRecord clickedRecord = row.getItem();
                    updateStatusAndImage(clickedRecord.getStatus());
                }
            });
            return row;
        });

        centerPanel.getChildren().add(table);
        return centerPanel;
    }

    private static void updateStatusAndImage(String status) {
        statusLabel.setText(status.toUpperCase());
        statusLabel.setTextFill(Color.web(getTextColorForStatus(status.toLowerCase())));

        String imagePath = switch (status.toLowerCase()) {
            case "present" -> "/resources/Student_Dashboard/Present_student.png";
            case "absent" -> "/resources/Student_Dashboard/Absent_student.png";
            case "late" -> "/resources/Student_Dashboard/Late_student.png";
            case "excused" -> "/resources/Student_Dashboard/Excused_student.png";
            default -> null;
        };

        try {
            Image newImage = new Image(Objects.requireNonNull(StudentViewMyAttendanceCenterPanel.class.getResource(imagePath)).toExternalForm());
            studentImageView.setImage(newImage);

            FadeTransition fade = new FadeTransition(Duration.millis(800), studentImageView);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        } catch (Exception e) {
            System.out.println("Failed to load image for status: " + status);
        }
    }

    private static String getStyleForStatus(String status) {
        return switch (status) {
            case "present" -> "-fx-background-color: #E7FAF1; -fx-text-fill: #40A79B; -fx-border-color: #55DC93; -fx-border-width: 1; -fx-background-radius: 15px;";
            case "absent" -> "-fx-background-color: #FFC9CA; -fx-text-fill: #DC5046; -fx-border-color: #FFC9CA; -fx-border-width: 1; -fx-background-radius: 15px;";
            case "late" -> "-fx-background-color: #E9D1FF; -fx-text-fill: #8B43BC; -fx-border-color: #A976E8; -fx-border-width: 1; -fx-background-radius: 15px;";
            case "excused" -> "-fx-background-color: #CCE4FF; -fx-text-fill: #3A3CC0; -fx-border-color: #76A2E8; -fx-border-width: 1; -fx-background-radius: 15px;";
            default -> "-fx-background-color: gray;";
        };
    }

    private static String getTextColorForStatus(String status) {
        return switch (status) {
            case "present" -> "#40A79B";
            case "absent" -> "#DC5046";
            case "late" -> "#8B43BC";
            case "excused" -> "#3A3CC0";
            default -> "black";
        };
    }

    private static void loadAttendanceFromDatabase(String studentId) {
        String url = "jdbc:mysql://localhost:3306/your_database";
        String user = "your_username";
        String password = "your_password";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT date, status FROM attendance WHERE student_id = ? ORDER BY date DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String date = rs.getString("date");
                String status = rs.getString("status");
                data.add(new AttendanceRecord(date, status));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class AttendanceRecord {
        private final javafx.beans.property.SimpleStringProperty date;
        private final javafx.beans.property.SimpleStringProperty status;

        public AttendanceRecord(String date, String status) {
            this.date = new javafx.beans.property.SimpleStringProperty(date);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }

        public String getDate() {
            return date.get();
        }

        public String getStatus() {
            return status.get();
        }

        public javafx.beans.property.StringProperty dateProperty() {
            return date;
        }

        public javafx.beans.property.StringProperty statusProperty() {
            return status;
        }
    }
}
