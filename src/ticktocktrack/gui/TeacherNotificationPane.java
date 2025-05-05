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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Duration;
import java.time.LocalDateTime;

public class TeacherNotificationPane {

    private Popup notificationPopup;
    private ImageView notificationIcon;
    private StackPane notificationIconWrapper;

    // List to hold notifications
    private ObservableList<Notification> notifications;
    private VBox notificationHolder; // Holder for notifications

    public TeacherNotificationPane() {
        // Initialize the notification list
        notifications = FXCollections.observableArrayList();

        // Example notifications (these will later be loaded from the database)
        notifications.add(new Notification("Student 1 Submitted Excuse Letter"));
        notifications.add(new Notification("Student 2 Submitted Excuse Letter"));
        notifications.add(new Notification("Student 3 Submitted Excuse Letter"));

        // Initialize the popup and content
        notificationPopup = new Popup();
        notificationHolder = new VBox(10); // Notification holder (VBox)
        notificationHolder.setPadding(new Insets(10));
        notificationHolder.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #20B2AA; -fx-border-width: 2px;");
        notificationHolder.setPrefWidth(250);

        // Dynamically add notification holders (HBox) based on the list of notifications
        for (Notification notification : notifications) {
            addNotificationToHolder(notification);
        }
        notificationPopup.getContent().add(notificationHolder);

        // Notification icon
        String notificationIconPath = getClass().getResource("/resources/Teacher_Dashboard/Teacher_notification_icon.png").toExternalForm();
        notificationIcon = new ImageView(new Image(notificationIconPath));
        notificationIcon.setFitWidth(50);
        notificationIcon.setFitHeight(50);
        notificationIcon.setPreserveRatio(true);

        // Wrap the icon in a StackPane to ensure its clickable area matches its bounds
        notificationIconWrapper = new StackPane(notificationIcon);
        notificationIconWrapper.setPrefSize(50, 50); // Make the wrapper large enough to be entirely clickable
        notificationIconWrapper.setMaxSize(50, 50); // Ensure the wrapper doesn't expand

        // Set cursor to hand for the entire area of the icon's wrapper
        notificationIconWrapper.setCursor(Cursor.HAND);

        // Make the whole region clickable, not just the icon
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

    // Method to add a new notification (this will be connected to your database later)
    public void addNotification(String message) {
        Notification newNotification = new Notification(message);
        notifications.add(newNotification);

        // Add the new notification to the holder without clearing the existing ones
        addNotificationToHolder(newNotification);
    }

 // Method to add a notification to the holder
    private void addNotificationToHolder(Notification notification) {
        // Get the "time ago" instead of the date
        String timeAgo = notification.getTimeAgo();  // Now we use getTimeAgo() from Notification

        // Create a label for each notification
        Label notificationLabel = new Label("• " + notification.getMessage() + "\n" + timeAgo);
        notificationLabel.setFont(javafx.scene.text.Font.font("Poppins", 14));

        // Enable text wrapping
        notificationLabel.setWrapText(true);
        notificationLabel.setMaxWidth(Region.USE_COMPUTED_SIZE);  // Allow the label to take up the width of the container

        // Create an HBox for each notification, add the label inside
        HBox notificationBox = new HBox(10, notificationLabel);
        notificationBox.setStyle("-fx-padding: 5px; -fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0.5, 0, 0);");

        // Add hover effect for highlighting
        addHoverEffect(notificationBox);

        // Set the HBox's height to be flexible and adjust based on its content
        notificationBox.setMinHeight(Region.USE_COMPUTED_SIZE);  // Let the HBox grow/shrink based on content
        notificationLabel.setMaxHeight(Double.MAX_VALUE);  // Let the label expand as needed

        // Add the HBox to the VBox holder
        notificationHolder.getChildren().add(notificationBox);
    }

    // Method to add hover effects to a notification box
    private void addHoverEffect(HBox notificationBox) {
        notificationBox.setOnMouseEntered(e -> notificationBox.setStyle("-fx-padding: 5px; -fx-background-color: #e0e0e0; -fx-border-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0.5, 0, 0);"));
        notificationBox.setOnMouseExited(e -> notificationBox.setStyle("-fx-padding: 5px; -fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0.5, 0, 0);"));
    }

    public StackPane getNotificationIconWrapper() {
        return notificationIconWrapper;
    }

    public ImageView getNotificationIcon() {
        return notificationIcon;
    }

    public void showPopup(double x, double y) {
        notificationPopup.show(notificationIconWrapper, x, y); // Use the wrapper for showing the popup
    }

    public void hidePopup() {
        notificationPopup.hide();
    }

    public boolean isPopupShowing() {
        return notificationPopup.isShowing();
    }

    // Inner class to represent a single notification
    public static class Notification {
        private String message;
        private LocalDateTime dateSent;

        public Notification(String message) {
            this.message = message;
            this.dateSent = LocalDateTime.now(); // Default to current time when the notification is created
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
    }
}
