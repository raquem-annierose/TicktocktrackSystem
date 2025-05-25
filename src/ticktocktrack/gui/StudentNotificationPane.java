package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.TextAlignment;

import ticktocktrack.database.StudentNotificationDAO;
import ticktocktrack.logic.Notification;
import ticktocktrack.logic.UsersModel;
import ticktocktrack.logic.Session;

import java.time.LocalDateTime;
import javafx.scene.control.ScrollPane;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class StudentNotificationPane {
	
	private StudentDashboardPage dashboardPage;
    private int userId;
    private Popup notificationPopup;
    private ImageView notificationIcon;
    private StackPane notificationIconWrapper;
    private VBox notificationHolder;
    // Declare the notifications list as a class field
    private ObservableList<Notification> notifications;

    public StudentNotificationPane(StudentDashboardPage dashboardPage) {
        this.dashboardPage = dashboardPage;
        
        UsersModel currentUser = Session.getCurrentUser();
        
        
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in. Notifications cannot be loaded.");
        }

        this.userId = currentUser.getUserId();
        System.out.println("DEBUG: Initializing StudentNotificationPane for userId = " + userId);

        // Initialize the list
        notifications = FXCollections.observableArrayList();

     // Create popup and holder
        notificationPopup = new Popup();

        notificationHolder = new VBox(10);
        notificationHolder.setPadding(new Insets(10));
        notificationHolder.setStyle("-fx-background-color: #FFFFFF;");
        notificationHolder.setPrefWidth(260);  // Adjust width inside ScrollPane

        ScrollPane scrollPane = new ScrollPane(notificationHolder);
        scrollPane.setPrefWidth(280); // Set fixed width
        scrollPane.setPrefHeight(300); // Set fixed height
        scrollPane.setFitToWidth(true); // Makes VBox expand to width of ScrollPane

        scrollPane.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: #20B2AA; " +
            "-fx-border-width: 2px;"
        );

        notificationPopup.getContent().add(scrollPane);


        // Load notifications from database
        loadNotificationsFromDatabase();

        // Add loaded notifications to the GUI holder
        for (Notification notification : notifications) {
            addNotificationToHolder(notification);
        }
        notificationPopup.getContent().add(notificationHolder);

        // Setup icon
        String notificationIconPath = getClass().getResource("/resources/Student_Dashboard/Student_notification_icon.png").toExternalForm();
        notificationIcon = new ImageView(new Image(notificationIconPath));
        notificationIcon.setFitWidth(30);
        notificationIcon.setFitHeight(30);
        notificationIcon.setPreserveRatio(true);

        notificationIconWrapper = new StackPane(notificationIcon);
        notificationIconWrapper.setPrefSize(50, 50);
        notificationIconWrapper.setMaxSize(50, 50);
        notificationIconWrapper.setCursor(Cursor.HAND);

        // Toggle popup on click
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

    private void loadNotificationsFromDatabase() {
        System.out.println("DEBUG: Loading notifications for userId = " + userId);
        List<Notification> dbNotifications = StudentNotificationDAO.getNotificationsForUser(userId);

        if (dbNotifications != null) {
            for (Notification n : dbNotifications) {
                System.out.println("DEBUG: Notification loaded -> [Message: " + n.getMessage() +
                        ", Type: " + n.getStatus() + ", Date: " + n.getDateSent() + "]");
            }
            System.out.println("DEBUG: Total notifications loaded: " + dbNotifications.size());
            notifications.addAll(dbNotifications);
        } else {
            System.out.println("DEBUG: No notifications returned from DAO (null)");
        }
    }

 // Original method with no senderUserId; calls overloaded method with default senderUserId = 0
    public void addNotification(String message, LocalDateTime dateSent, String status) {
        addNotification(message, dateSent, status, 0); // default senderUserId
    }

    // New overloaded method with senderUserId parameter
    public void addNotification(String message, LocalDateTime dateSent, String status, int senderUserId) {
        Notification note = new Notification(message, dateSent, status, senderUserId);
        notifications.add(note);
        addNotificationToHolder(note);
    }


    private void addNotificationToHolder(Notification notification) {
        // Create notification message label
        Label notificationLabel = new Label("� " + notification.getMessage());
        notificationLabel.setFont(javafx.scene.text.Font.font("Poppins", 13));
        notificationLabel.setWrapText(true);
        notificationLabel.setMaxWidth(240);
        notificationLabel.setTextAlignment(TextAlignment.LEFT);

        // Date and status label
        Label dateLabel = new Label(notification.getTimeAgo() + " | Status: " + notification.getStatus());
        dateLabel.setFont(javafx.scene.text.Font.font("Poppins", 10));
        dateLabel.setStyle("-fx-text-fill: gray;");

        VBox content = new VBox(2, notificationLabel, dateLabel);

        HBox notificationBox = new HBox(content);
        notificationBox.setPadding(new Insets(5));
        notificationBox.setStyle(
            "-fx-background-color: #f9f9f9; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 5, 0.5, 0, 0);"
        );

        addHoverEffect(notificationBox);
        notificationHolder.getChildren().add(notificationBox);
        
        if (notification.getMessage().contains("marked you as Absent")) {
            notificationBox.setOnMouseClicked(e -> {
                System.out.println("DEBUG: Notification clicked, opening Submit Excuse from dashboard...");
                dashboardPage.openSubmitExcuseFromNotification(); // DIRECTLY call the method!
                notificationPopup.hide();
            });
        }



    }

    private void addHoverEffect(HBox notificationBox) {
        notificationBox.setOnMouseEntered(e -> 
            notificationBox.setStyle("-fx-background-color: #e0e0e0; -fx-border-radius: 5px; -fx-background-radius: 5px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0.5, 0, 0);")
        );
        notificationBox.setOnMouseExited(e -> 
            notificationBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-background-radius: 5px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 5, 0.5, 0, 0);")
        );
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

}
    