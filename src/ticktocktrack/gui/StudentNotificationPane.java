package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.TextAlignment;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class StudentNotificationPane {

    private Popup notificationPopup;
    private ImageView notificationIcon;
    private StackPane notificationIconWrapper;

    // List to hold notifications
    private ObservableList<Notification> notifications;
    private VBox notificationHolder; // Holder for notifications

    public StudentNotificationPane() {
        notifications = FXCollections.observableArrayList();

        // Sample notifications (database later)
        notifications.add(new Notification(
            "Mr. Campos has marked you absent in English 101.", 
            LocalDateTime.now(), 
            "Absent"
        ));
        notifications.add(new Notification(
            "Mr. Sigme has marked you present in Mathematics 202.", 
            LocalDateTime.now(), 
            "Present"
        ));
        notifications.add(new Notification(
            "Ms. Skibidi has marked you excused in Biology 303.", 
            LocalDateTime.now(), 
            "Excused"
        ));
        notifications.add(new Notification(
            "Mr. Ramos is requesting an excuse letter for your absence in Chemistry 204.", 
            LocalDateTime.now(), 
            "Absent"
        ));
        notifications.add(new Notification(
            "You have accumulated 5 absences in History 101. Further absences may lead to disciplinary action.", 
            LocalDateTime.now(), 
            "Absent"
        ));

        notificationPopup = new Popup();
        notificationHolder = new VBox(10);
        notificationHolder.setPadding(new Insets(10));
        notificationHolder.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #20B2AA; -fx-border-width: 2px;");
        notificationHolder.setPrefWidth(280);

        // Dynamically add notifications
        for (Notification notification : notifications) {
            addNotificationToHolder(notification);
        }
        notificationPopup.getContent().add(notificationHolder);

        // Notification icon
        String notificationIconPath = getClass().getResource("/resources/Student_Dashboard/Student_notification_icon.png").toExternalForm();
        notificationIcon = new ImageView(new Image(notificationIconPath));
        notificationIcon.setFitWidth(50);
        notificationIcon.setFitHeight(50);
        notificationIcon.setPreserveRatio(true);

        notificationIconWrapper = new StackPane(notificationIcon);
        notificationIconWrapper.setPrefSize(50, 50);
        notificationIconWrapper.setMaxSize(50, 50);
        notificationIconWrapper.setCursor(Cursor.HAND);

        notificationIconWrapper.setOnMouseClicked(e -> {
            if (notificationPopup.isShowing()) {
                notificationPopup.hide();
            } else {
                double x = notificationIcon.localToScreen(notificationIcon.getBoundsInLocal()).getMinX();
                double y = notificationIcon.localToScreen(notificationIcon.getBoundsInLocal()).getMaxY();
                notificationPopup.show(notificationIconWrapper, x - 100, y + 5);
            }
        });
    }

    public void addNotification(String message, LocalDateTime dateSent, String status) {
        Notification newNotification = new Notification(message, dateSent, status);
        notifications.add(newNotification);
        addNotificationToHolder(newNotification);
    }

    private void addNotificationToHolder(Notification notification) {
        // Create label
        Label notificationLabel = new Label("• " + notification.getMessage());
        notificationLabel.setFont(javafx.scene.text.Font.font("Poppins", 13));
        notificationLabel.setWrapText(true);
        notificationLabel.setMaxWidth(240); // Important: makes long text wrap instead of stretching
        notificationLabel.setTextAlignment(TextAlignment.LEFT);

        // Date below message (small)
        Label dateLabel = new Label(notification.getTimeAgo() + " | Status: " + notification.getStatus());
        dateLabel.setFont(javafx.scene.text.Font.font("Poppins", 10));
        dateLabel.setStyle("-fx-text-fill: gray;");

        VBox content = new VBox(2, notificationLabel, dateLabel);

        HBox notificationBox = new HBox(content);
        notificationBox.setPadding(new Insets(5));
        notificationBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 5, 0.5, 0, 0);");

        addHoverEffect(notificationBox);

        notificationHolder.getChildren().add(notificationBox);
    }

    private void addHoverEffect(HBox notificationBox) {
        notificationBox.setOnMouseEntered(e -> notificationBox.setStyle("-fx-background-color: #e0e0e0; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0.5, 0, 0);"));
        notificationBox.setOnMouseExited(e -> notificationBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 5, 0.5, 0, 0);"));
    }

    public StackPane getNotificationIconWrapper() {
        return notificationIconWrapper;
    }

    public ImageView getNotificationIcon() {
        return notificationIcon;
    }

    public void showPopup(double x, double y) {
        notificationPopup.show(notificationIconWrapper, x, y);
    }

    public void hidePopup() {
        notificationPopup.hide();
    }

    public boolean isPopupShowing() {
        return notificationPopup.isShowing();
    }

    // Notification class with additional fields
    public static class Notification {
        private String message;
        private LocalDateTime dateSent;
        private String status;

        public Notification(String message, LocalDateTime dateSent, String status) {
            this.message = message;
            this.dateSent = dateSent;
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getDateSent() {
            return dateSent;
        }

        public String getStatus() {
            return status;
        }

        // Calculate time ago
        public String getTimeAgo() {
            Duration duration = Duration.between(dateSent, LocalDateTime.now());
            long seconds = duration.getSeconds();

            if (seconds < 60) {
                return seconds + " seconds ago";
            } else if (seconds < 3600) {
                return (seconds / 60) + " minutes ago";
            } else if (seconds < 86400) {
                return (seconds / 3600) + " hours ago";
            } else if (seconds < 2592000) {
                return (seconds / 86400) + " days ago";
            } else if (seconds < 31536000) {
                return (seconds / 2592000) + " months ago";
            } else {
                return (seconds / 31536000) + " years ago";
            }
        }

        public String getDateSentFormatted() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
            return dateSent.format(formatter);
        }
    }
}
