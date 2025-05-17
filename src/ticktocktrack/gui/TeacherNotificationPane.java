package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Duration;
import java.time.LocalDateTime;

public class TeacherNotificationPane {

    private Popup notificationPopup;
    private ImageView notificationIcon;
    private StackPane notificationIconWrapper;

    private ObservableList<Notification> notifications;
    private VBox notificationHolder;

    public TeacherNotificationPane() {
        notifications = FXCollections.observableArrayList();

        notifications.add(new Notification("Student 1 Submitted Excuse Letter"));
        notifications.add(new Notification("Student 2 Submitted Excuse Letter"));
        notifications.add(new Notification("Student 3 Submitted Excuse Letter"));

        notificationPopup = new Popup();
        notificationHolder = new VBox(10);
        notificationHolder.setPadding(new Insets(10));
        notificationHolder.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #20B2AA; -fx-border-width: 2px;");
        notificationHolder.setPrefWidth(250);

        for (Notification notification : notifications) {
            addNotificationToHolder(notification);
        }
        notificationPopup.getContent().add(notificationHolder);

        String notificationIconPath = getClass().getResource("/resources/Teacher_Dashboard/Teacher_notification_icon.png").toExternalForm();
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

    public void addNotification(String message) {
        Notification newNotification = new Notification(message);
        notifications.add(newNotification);
        addNotificationToHolder(newNotification);
    }

    private void addNotificationToHolder(Notification notification) {
        String timeAgo = notification.getTimeAgo();

        Label notificationLabel = new Label("• " + notification.getMessage() + "\n" + timeAgo);
        notificationLabel.setFont(Font.font("Poppins", 14));
        notificationLabel.setWrapText(true);
        notificationLabel.setMaxWidth(220);
        notificationLabel.setPrefHeight(60);
        notificationLabel.setMinHeight(60);
        notificationLabel.setStyle("-fx-text-overrun: ellipsis;"); // Optional: adds "..." for overflow

        HBox notificationBox = new HBox(notificationLabel);
        notificationBox.setPadding(new Insets(5));
        notificationBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px; "
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0.5, 0, 0);");
        notificationBox.setMinHeight(70); // fixed height for uniformity
        notificationBox.setMaxHeight(70);
        notificationBox.setPrefHeight(70);

        addHoverEffect(notificationBox);

        notificationBox.setOnMouseClicked(event -> {
            showExpandedNotification(notification);
        });

        notificationHolder.getChildren().add(notificationBox);
    }


    private void addHoverEffect(HBox notificationBox) {
        notificationBox.setOnMouseEntered(e -> notificationBox.setStyle("-fx-padding: 5px; -fx-background-color: #e0e0e0; -fx-border-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0.5, 0, 0);"));
        notificationBox.setOnMouseExited(e -> notificationBox.setStyle("-fx-padding: 5px; -fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0.5, 0, 0);"));
    }

    private void showExpandedNotification(Notification notification) {
        Stage dialog = new Stage();

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(20));
        contentBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");

        Label messageLabel = new Label(notification.getMessage());
        messageLabel.setFont(Font.font("Poppins", 16));
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.CENTER);

        Label timeLabel = new Label(notification.getTimeAgo());
        timeLabel.setFont(Font.font("Poppins", 12));
        timeLabel.setTextFill(Color.GRAY);

        BorderPane dialogPane = new BorderPane();
        // No close button added to the top anymore
        dialogPane.setCenter(contentBox);

        contentBox.getChildren().addAll(messageLabel, timeLabel);

        StackPane root = new StackPane(dialogPane);
        Scene scene = new Scene(root, 400, 200);
        dialog.setScene(scene);
        dialog.show();
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

    public static class Notification {
        private String message;
        private LocalDateTime dateSent;

        public Notification(String message) {
            this.message = message;
            this.dateSent = LocalDateTime.now();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDateTime getDateSent() {
            return dateSent;
        }

        public void setDateSent(LocalDateTime dateSent) {
            this.dateSent = dateSent;
        }

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
    }
}
