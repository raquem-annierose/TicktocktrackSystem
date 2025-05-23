package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import ticktocktrack.database.TeacherNotificationDAO;
import ticktocktrack.logic.Notification;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;

import java.time.LocalDateTime;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TeacherNotificationPane {
    private int userId;
    private Popup notificationPopup;
    private ImageView notificationIcon;
    private StackPane notificationIconWrapper;
    private ObservableList<Notification> notifications;
    private VBox notificationHolder;

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
        scrollPane.setPrefWidth(280);
        scrollPane.setPrefHeight(300);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: #20B2AA; " +
            "-fx-border-width: 2px;"
        );

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

    private void loadNotificationsFromDatabase() {
        List<Notification> dbNotes = TeacherNotificationDAO.getNotificationsForUser(userId);
        if (dbNotes != null) {
            notifications.addAll(dbNotes);
        }
    }

    public void addNotification(String message, LocalDateTime dateSent, String status) {
        Notification note = new Notification(message, dateSent, status);
        notifications.add(note);
        addNotificationToHolder(note);
    }

    private void addNotificationToHolder(Notification note) {
        Label msgLabel = new Label("\u2022 " + note.getMessage());
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
        box.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-background-radius: 5px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15),5,0.5,0,0);");

        addHoverEffect(box);

        box.setOnMouseClicked(e -> showDetailedNotificationPopup(note));

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

    private void showDetailedNotificationPopup(Notification note) {
        // Hide main notification popup so it won't cover detailed popup
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

        Label message = new Label(note.getMessage());
        message.setFont(javafx.scene.text.Font.font("Poppins", 12));
        message.setMaxWidth(500);
        message.setMaxHeight(500);

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
            System.out.println("Accepted notification: " + note.getMessage());
            // TODO: Add accept logic here
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
            System.out.println("Rejected notification: " + note.getMessage());
            // TODO: Add reject logic here
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

        // Title bar
        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setSpacing(10);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBar.getChildren().addAll(title, spacer, closeButton);

        VBox contentBox = new VBox(10, message, date);
        VBox.setVgrow(contentBox, Priority.ALWAYS); // Pushes buttons to the bottom

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

            // Clicking outside popup closes it
            dimOverlay.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                root.getChildren().remove(fullOverlay);
            });
        }
    }


    private void closePopup(StackPane dimOverlay) {
        Scene scene = notificationIconWrapper.getScene();
        if (scene != null && scene.getRoot() instanceof StackPane) {
            StackPane root = (StackPane) scene.getRoot();
            root.getChildren().removeIf(node -> node instanceof StackPane && ((StackPane)node).getChildren().contains(dimOverlay));
        }
    }

    public StackPane getNotificationIconWrapper() { return notificationIconWrapper; }
    public ImageView getNotificationIcon() { return notificationIcon; }
    public void showPopup(double x, double y) { notificationPopup.show(notificationIconWrapper, x, y); }
    public void hidePopup() { notificationPopup.hide(); }
    public boolean isPopupShowing() { return notificationPopup.isShowing(); }
}
