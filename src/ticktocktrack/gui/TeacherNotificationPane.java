package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import ticktocktrack.database.StudentNotificationDAO;
import ticktocktrack.database.TeacherApproval;
import ticktocktrack.database.TeacherNotificationDAO;
import ticktocktrack.logic.Notification;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.geometry.Side;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The TeacherNotificationPane class manages the notification UI components
 * for a teacher user, including the notification popup, icon, and list of
 * notifications. It handles displaying notifications and interaction logic.
 */
public class TeacherNotificationPane {

    /** The ID of the teacher user. */
    private int userId;

    /** Popup window that shows the list of notifications. */
    private Popup notificationPopup;

    /** Icon image shown in the UI to represent notifications. */
    private ImageView notificationIcon;

    /** Wrapper pane containing the notification icon for positioning. */
    private StackPane notificationIconWrapper;

    /** Observable list holding all notifications for the user. */
    private ObservableList<Notification> notifications;

    /** Container holding notification UI elements inside the popup. */
    private VBox notificationHolder;

    /** The user ID of the sender of a notification. */
    private int senderUserId;

    /** The message content of a notification. */
    private String message;

    
    /**
     * The number of notifications to load per page when fetching notifications.
     */
    private static final int PAGE_SIZE = 3;

    /**
     * Offset tracking how many notifications have been loaded so far.
     */
    private int notificationsOffset = 0;

    /**
     * Flag indicating whether all notifications have been loaded from the database.
     */
    private boolean allNotificationsLoaded = false;

    /**
     * Gets the user ID of the sender of the notification.
     * 
     * @return the sender user ID
     */
    public int getSenderUserId() {
        return senderUserId;
    }

    /**
     * Gets the message content of the notification.
     * 
     * @return the notification message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Constructs a new TeacherNotificationPane, initializing the notification UI components
     * and data structures.
     */
    public TeacherNotificationPane() {
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in. Notifications cannot be loaded.");
        }
        this.userId = currentUser.getUserId();

        notifications = FXCollections.observableArrayList();

        // Create popup container
        notificationPopup = new Popup();
        notificationHolder = new VBox(10);
        notificationHolder.setPadding(new Insets(10));
        notificationHolder.setStyle("-fx-background-color: #FFFFFF;");
        notificationHolder.setPrefWidth(260);

        ScrollPane scrollPane = new ScrollPane(notificationHolder);
        scrollPane.setPrefWidth(300);
        scrollPane.setPrefHeight(400);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: #20B2AA; " +
            "-fx-border-width: 2px;"
        );
        
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() >= 0.9) {  // near bottom
            	loadNotificationsFromDatabase();

            }
        });

        notificationPopup.getContent().add(scrollPane);

        loadNotificationsFromDatabase();
        for (Notification n : notifications) {
            addNotificationToHolder(n);
        }

        String iconPath = getClass().getResource("/resources/Teacher_Dashboard/Teacher_notification_icon.png").toExternalForm();
        notificationIcon = new ImageView(new Image(iconPath));
        notificationIcon.setFitWidth(30);
        notificationIcon.setFitHeight(30);
        notificationIcon.setPreserveRatio(true);

        notificationIconWrapper = new StackPane(notificationIcon);
        notificationIconWrapper.setPrefSize(50, 50);
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

    /**
     * Loads notifications for the current user from the database in pages.
     * If all notifications have already been loaded, this method returns immediately.
     * Newly loaded notifications are added to the internal list and UI.
     */
    private void loadNotificationsFromDatabase() {
        if (allNotificationsLoaded) {
            return; // no more notifications to load
        }

        List<Notification> dbNotes = TeacherNotificationDAO.getNotificationsForUser(userId, notificationsOffset, PAGE_SIZE);

        if (dbNotes == null || dbNotes.isEmpty()) {
            allNotificationsLoaded = true; // no more data
        } else {
            notifications.addAll(dbNotes);
            notificationsOffset += dbNotes.size();

            // Add newly loaded notifications to UI:
            for (Notification n : dbNotes) {
                addNotificationToHolder(n);
            }
        }
    }

    /**
     * Adds a new notification to the notifications list and updates the UI.
     * 
     * @param message the notification message content
     * @param dateSent the date and time when the notification was sent
     * @param status the status of the notification (e.g., "unread", "read")
     * @param senderUserId the user ID of the sender of this notification
     */
    public void addNotification(String message, LocalDateTime dateSent, String status, int senderUserId) {
        Notification note = new Notification(message, dateSent, status, senderUserId);
        notifications.add(note);
        addNotificationToHolder(note);
    }

    /**
     * Adds a notification UI element to the notification holder pane.
     * 
     * @param note the Notification object to be added to the UI list
     */
    private void addNotificationToHolder(Notification note) {
        int senderUserId = note.getSenderUserId();

        // Try to get the sender's profile path from the database
        String profilePath = StudentNotificationDAO.getUserProfilePath(senderUserId);

        ImageView iconView;
        if (profilePath != null && !profilePath.isEmpty()) {
            try {
                // If profilePath is a URL or file path, load it
                Image img = new Image(profilePath, 50, 50, true, true);
                iconView = new ImageView(img);
            } catch (Exception e) {
                // If loading fails, fallback to default icon
                System.err.println("Failed to load profile image: " + e.getMessage());
                iconView = getDefaultIcon();
            }
        } else {
            // If no profilePath, use default icon
            iconView = getDefaultIcon();
        }

        // Make the icon circular
        Circle clip = new Circle(25, 25, 25);
        iconView.setFitWidth(50);
        iconView.setFitHeight(50);
        iconView.setPreserveRatio(true);
        iconView.setClip(clip);


        // Notification message
        Label msgLabel = new Label("\u2022 " + note.getMessage());
        msgLabel.setFont(javafx.scene.text.Font.font("Poppins", 13));
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(200); // Adjusted to fit beside larger icon
        msgLabel.setTextAlignment(TextAlignment.LEFT);

        // Notification date + status
        Label dateLabel = new Label(note.getTimeAgo() + " | " + note.getStatus());
        dateLabel.setFont(javafx.scene.text.Font.font("Poppins", 10));
        dateLabel.setStyle("-fx-text-fill: gray;");

        VBox content = new VBox(2, msgLabel, dateLabel);

        HBox box = new HBox(10, iconView, content);  // Icon left, content right
        box.setPadding(new Insets(5));
        box.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-background-radius: 5px; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15),5,0.5,0,0);");

        addHoverEffect(box, note.getNotificationId());

        // Optional: click action for detailed popup
        box.setOnMouseClicked(e -> showDetailedNotificationPopup(note));

        notificationHolder.getChildren().add(box);
    }
    
    /**
     * Returns a default user icon as an ImageView.
     * The icon image is loaded from the resources folder.
     *
     * @return ImageView containing the default user icon with size 50x50 pixels
     */
    private ImageView getDefaultIcon() {
        String iconPath = getClass().getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm();
        return new ImageView(new Image(iconPath, 50, 50, true, true));
    }
    
    /**
     * Adds a hover effect to the given notification box.
     * Typically used to highlight or change appearance when mouse hovers over the notification.
     *
     * @param box the HBox representing the notification UI element
     * @param notificationId the unique identifier of the notification (for event handling)
     */
    private void addHoverEffect(HBox box, int notificationId) {
        // Create the button with the image, but don't add it yet
    	String btnImagePath = getClass().getResource("/resources/others_button.png").toExternalForm();
        ImageView btnIcon = new ImageView(new Image(btnImagePath, 30, 30, true, true));
        Button hoverButton = new Button();
        hoverButton.setGraphic(btnIcon);
        hoverButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        hoverButton.setFocusTraversable(false);

        // Create ContextMenu with "Delete Notification"
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete Notification");
        deleteItem.setOnAction(ev -> {
            boolean success = TeacherNotificationDAO.deleteNotificationById(notificationId);
            if (success) {
                ((VBox) box.getParent()).getChildren().remove(box);
                contextMenu.hide();
            } else {
                System.out.println("Failed to delete notification from database.");
            }
        });


        contextMenu.getItems().add(deleteItem);

        // Show the menu on button click
        hoverButton.setOnAction(e -> {
            contextMenu.show(hoverButton, Side.BOTTOM, 0, 0);
        });

        // Hover effect handlers
        box.setOnMouseEntered(e -> {
            box.setStyle(
                "-fx-background-color: #e0e0e0; -fx-border-radius: 5px; -fx-background-radius: 5px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2),5,0.5,0,0);"
            );
            if (!box.getChildren().contains(hoverButton)) {
                box.getChildren().add(hoverButton);
                HBox.setHgrow(hoverButton, Priority.NEVER);
            }
        });

        PauseTransition exitDelay = new PauseTransition(Duration.millis(200));
        box.setOnMouseExited(e -> {
            exitDelay.setOnFinished(event -> {
                if (!contextMenu.isShowing()) {
                    box.setStyle(
                        "-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-background-radius: 5px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15),5,0.5,0,0);"
                    );
                    box.getChildren().remove(hoverButton);
                }
            });
            exitDelay.play();
        });
    }

    /**
     * Displays a detailed popup for the given notification.
     * This popup typically shows more information or actions related to the notification.
     *
     * @param note the Notification object whose details are to be displayed
     */
    private void showDetailedNotificationPopup(Notification note) {
        if (notificationPopup.isShowing()) {
            notificationPopup.hide();
        }

        StackPane dimOverlay = new StackPane();
        dimOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        dimOverlay.setPrefSize(1300, 750);
        dimOverlay.setPickOnBounds(true);

        Label title = new Label("Notification Details");
        title.setFont(javafx.scene.text.Font.font("Poppins", 13));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        // Wrap message text every 60 chars
        String wrappedMessage = wrapText(note.getMessage(), 60);
        Label message = new Label(wrappedMessage);
        message.setFont(javafx.scene.text.Font.font("Poppins", 12));
        message.setMaxWidth(500);
        message.setWrapText(true);  // enable wrapping inside label

        Label date = new Label("Sent: " + note.getDateSent().toString());
        date.setFont(javafx.scene.text.Font.font("Poppins", 10));
        date.setStyle("-fx-text-fill: gray;");

        // Accept button
        Button acceptButton = new Button("Accept");
        acceptButton.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-text-fill: #01B80A;" +
            "-fx-border-color: #01B80A;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 5px;" +
            "-fx-background-radius: 5px;"
        );
        acceptButton.setFont(javafx.scene.text.Font.font("Poppins", 12));
        acceptButton.setPrefWidth(100);
        acceptButton.setPrefHeight(35);
        acceptButton.setCursor(Cursor.HAND);
        acceptButton.setOnAction(e -> {
            int senderUserId = note.getSenderUserId();
            int studentId = TeacherApproval.getStudentIdByUserId(senderUserId);

            String fullMessage = note.getMessage();
            String dateString;
            String reason;
            String courseName;

            int dateKeywordIndex = fullMessage.indexOf("for ");
            int dateStart = dateKeywordIndex + 4;
            int dateEnd = fullMessage.indexOf(" in course", dateStart);
            dateString = fullMessage.substring(dateStart, dateEnd).trim();

            int courseStart = fullMessage.indexOf(" in course", dateEnd) + " in course".length();
            int courseEnd = fullMessage.indexOf(":", courseStart);
            courseName = fullMessage.substring(courseStart, courseEnd).trim();

            int lastColonIndex = fullMessage.lastIndexOf(":");
            reason = fullMessage.substring(lastColonIndex + 1).trim();

            int teacherId = TeacherApproval.getTeacherIdByUserId(Session.getCurrentUser().getUserId());

            boolean success = TeacherApproval.approveExcuse(studentId, courseName, dateString, reason, teacherId);

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Excuse approved and attendance updated.").showAndWait();
                StudentNotificationDAO.sendExcuseAcceptedNotification(studentId, courseName, LocalDate.parse(dateString));
            }

            closePopup(dimOverlay);
        });


        // Reject button
        Button rejectButton = new Button("Reject");
        rejectButton.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-text-fill: #DD5F61;" +
            "-fx-border-color: #DD5F61;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 5px;" +
            "-fx-background-radius: 5px;"
        );
        rejectButton.setFont(javafx.scene.text.Font.font("Poppins", 12));
        rejectButton.setPrefWidth(100);
        rejectButton.setPrefHeight(35);
        rejectButton.setCursor(Cursor.HAND);
        rejectButton.setOnAction(e -> {
            int senderUserId = note.getSenderUserId();
            int studentId = TeacherApproval.getStudentIdByUserId(senderUserId);

            String fullMessage = note.getMessage();
            String dateString;
            String courseName;

            int dateKeywordIndex = fullMessage.indexOf("for ");
            int dateStart = dateKeywordIndex + 4;
            int dateEnd = fullMessage.indexOf(" in course", dateStart);
            dateString = fullMessage.substring(dateStart, dateEnd).trim();

            int courseStart = fullMessage.indexOf(" in course", dateEnd) + " in course".length();
            int courseEnd = fullMessage.indexOf(":", courseStart);
            courseName = fullMessage.substring(courseStart, courseEnd).trim();

            int teacherId = TeacherApproval.getTeacherIdByUserId(Session.getCurrentUser().getUserId());

            boolean success = TeacherApproval.rejectExcuse(studentId, courseName, dateString, teacherId);

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Excuse rejected and attendance marked as absent.").showAndWait();
                StudentNotificationDAO.sendExcuseRejectedNotification(studentId, courseName, LocalDate.parse(dateString));
            }

            closePopup(dimOverlay);
        });



        // Close button (X)
        Button closeButton = new Button("X");
        closeButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-font-size: 14;" +
            "-fx-text-fill: #666666;" +
            "-fx-font-weight: bold;"
        );
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOnAction(e -> {
            closePopup(dimOverlay);
        });

        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setSpacing(10);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBar.getChildren().addAll(title, spacer, closeButton);

        VBox contentBox = new VBox(10, message, date);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        HBox buttonsBox = new HBox(20, acceptButton, rejectButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox popupBox = new VBox(15, titleBar, contentBox, buttonsBox);
        popupBox.setPadding(new Insets(20));
        popupBox.setMaxWidth(700);
        popupBox.setMaxHeight(350);
        popupBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.2, 0, 0);"
        );

        StackPane popupWrapper = new StackPane(popupBox);
        popupWrapper.setPrefSize(1300, 750);
        popupWrapper.setAlignment(Pos.CENTER);

        StackPane fullOverlay = new StackPane(dimOverlay, popupWrapper);

        Scene scene = notificationIconWrapper.getScene();
        if (scene != null && scene.getRoot() instanceof StackPane) {
            StackPane root = (StackPane) scene.getRoot();
            root.getChildren().add(fullOverlay);
            fullOverlay.toFront();

            dimOverlay.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                root.getChildren().remove(fullOverlay);
            });
        }
    }

    /**
     * Helper method to insert newlines into the given text every 'lineLength' characters,
     * ensuring that words are not broken across lines.
     *
     * @param text       the original text to wrap
     * @param lineLength the maximum number of characters per line
     * @return the text with newline characters inserted appropriately
     */
    private String wrapText(String text, int lineLength) {
        if (text == null) return "";
        StringBuilder wrapped = new StringBuilder();
        int index = 0;
        while (index < text.length()) {
            int nextBreak = Math.min(index + lineLength, text.length());
            // Try to break at space if possible to avoid breaking words
            if (nextBreak < text.length()) {
                int lastSpace = text.lastIndexOf(' ', nextBreak);
                if (lastSpace > index) {
                    nextBreak = lastSpace;
                }
            }
            wrapped.append(text, index, nextBreak).append("\n");
            index = nextBreak + 1; // skip the space
        }
        return wrapped.toString().trim();
    }


    /**
     * Closes the notification popup by removing the given dim overlay from the scene graph.
     * It searches the root StackPane's children for any StackPane containing the dimOverlay and removes it.
     *
     * @param dimOverlay the dim overlay StackPane to be removed from the scene
     */
    private void closePopup(StackPane dimOverlay) {
        Scene scene = notificationIconWrapper.getScene();
        if (scene != null && scene.getRoot() instanceof StackPane) {
            StackPane root = (StackPane) scene.getRoot();
            root.getChildren().removeIf(node -> node instanceof StackPane && ((StackPane)node).getChildren().contains(dimOverlay));
        }
    }

    /**
     * Gets the wrapper StackPane containing the notification icon.
     * 
     * @return the StackPane that wraps the notification icon
     */
    public StackPane getNotificationIconWrapper() {
        return notificationIconWrapper;
    }

    /**
     * Gets the ImageView used as the notification icon.
     * 
     * @return the ImageView representing the notification icon
     */
    public ImageView getNotificationIcon() {
        return notificationIcon;
    }

    /**
     * Shows the notification popup at the specified screen coordinates.
     * 
     * @param x the X coordinate where the popup will be displayed
     * @param y the Y coordinate where the popup will be displayed
     */
    public void showPopup(double x, double y) {
        notificationPopup.show(notificationIconWrapper, x, y);
    }

    /**
     * Hides the notification popup if it is currently showing.
     */
    public void hidePopup() {
        notificationPopup.hide();
    }

    /**
     * Checks if the notification popup is currently visible.
     * 
     * @return true if the popup is showing; false otherwise
     */
    public boolean isPopupShowing() {
        return notificationPopup.isShowing();
    }

}
