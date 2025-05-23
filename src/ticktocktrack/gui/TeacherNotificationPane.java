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
import javafx.scene.control.ScrollPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.TextAlignment;
import ticktocktrack.logic.Notification;
import ticktocktrack.logic.UsersModel;
import ticktocktrack.logic.Session;
import ticktocktrack.database.TeacherNotificationDAO;

import java.time.LocalDateTime;
import java.util.List;

public class TeacherNotificationPane {
    private int userId;
    private Popup notificationPopup;
    private ImageView notificationIcon;
    private StackPane notificationIconWrapper;
    private ObservableList<Notification> notifications;
    private VBox notificationHolder;

    public TeacherNotificationPane() {
        // Get current teacher
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in. Notifications cannot be loaded.");
        }
        this.userId = currentUser.getUserId();
        System.out.println("DEBUG: Initializing TeacherNotificationPane for userId = " + userId);

        notifications = FXCollections.observableArrayList();

        // Popup & container
        notificationPopup = new Popup();
        notificationHolder = new VBox(10);
        notificationHolder.setPadding(new Insets(10));
        notificationHolder.setStyle("-fx-background-color: #FFFFFF;");
        notificationHolder.setPrefWidth(260);

        ScrollPane scrollPane = new ScrollPane(notificationHolder);
        scrollPane.setPrefWidth(280);
        scrollPane.setPrefHeight(300);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: #20B2AA; " +
            "-fx-border-width: 2px;"
        );

        notificationPopup.getContent().add(scrollPane);

        // Load from DB
        loadNotificationsFromDatabase();
        for (Notification n : notifications) {
            addNotificationToHolder(n);
        }

        // Icon
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

    private void loadNotificationsFromDatabase() {
        System.out.println("DEBUG: Loading notifications for teacherId = " + userId);
        List<Notification> dbNotes = TeacherNotificationDAO.getNotificationsForUser(userId);
        if (dbNotes != null) {
            System.out.println("DEBUG: Total notifications loaded: " + dbNotes.size());
            notifications.addAll(dbNotes);
        } else {
            System.out.println("DEBUG: No notifications returned from DAO (null)");
        }
    }

    public void addNotification(String message, LocalDateTime dateSent, String status) {
        Notification note = new Notification(message, dateSent, status);
        notifications.add(note);
        addNotificationToHolder(note);
    }

    private void addNotificationToHolder(Notification note) {
        Label msgLabel = new Label("� " + note.getMessage());
        msgLabel.setFont(javafx.scene.text.Font.font("Poppins", 13));
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(240);
        msgLabel.setTextAlignment(TextAlignment.LEFT);

        Label dateLabel = new Label(note.getTimeAgo() + " | " + note.getStatus());
        dateLabel.setFont(javafx.scene.text.Font.font("Poppins", 10));
        dateLabel.setStyle("-fx-text-fill: gray;");

        VBox content = new VBox(2, msgLabel, dateLabel);
        HBox box = new HBox(content);
        box.setPadding(new Insets(5));
        box.setStyle(
            "-fx-background-color: #f9f9f9; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15),5,0.5,0,0);"
        );
        addHoverEffect(box);
        notificationHolder.getChildren().add(box);
    }

    private void addHoverEffect(HBox box) {
        box.setOnMouseEntered(e -> box.setStyle(
            "-fx-background-color: #e0e0e0; -fx-border-radius: 5px; -fx-background-radius: 5px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2),5,0.5,0,0);"
        ));
        box.setOnMouseExited(e -> box.setStyle(
            "-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-background-radius: 5px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15),5,0.5,0,0);"
        ));
    }

    public StackPane getNotificationIconWrapper() { return notificationIconWrapper; }
    public ImageView getNotificationIcon() { return notificationIcon; }
    public void showPopup(double x, double y) { notificationPopup.show(notificationIconWrapper, x, y); }
    public void hidePopup() { notificationPopup.hide(); }
    public boolean isPopupShowing() { return notificationPopup.isShowing(); }
}
