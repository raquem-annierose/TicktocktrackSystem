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

public class TeacherNotificationPane {
    private int userId;
    private Popup notificationPopup;
    private ImageView notificationIcon;
    private StackPane notificationIconWrapper;
    private ObservableList<Notification> notifications;
    private VBox notificationHolder;
    private int senderUserId;
    private String message;
    
    private static final int PAGE_SIZE = 3;
    private int notificationsOffset = 0;
    private boolean allNotificationsLoaded = false;

    public int getSenderUserId() { return senderUserId; }
    public String getMessage() { return message; }

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

    
    public void addNotification(String message, LocalDateTime dateSent, String status) {
        addNotification(message, dateSent, status, 0); // default senderUserId
    }

    public void addNotification(String message, LocalDateTime dateSent, String status, int senderUserId) {
        Notification note = new Notification(message, dateSent, status, senderUserId);
        notifications.add(note);
        addNotificationToHolder(note);
    }

    private void addNotificationToHolder(Notification note) {
        int senderUserId = note.getSenderUserId(); // use 'note' not 'notification'

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
    
    private ImageView getDefaultIcon() {
        String iconPath = getClass().getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm();
        return new ImageView(new Image(iconPath, 50, 50, true, true));
    }


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
            System.out.println("Sender UserId: " + senderUserId);

            if (senderUserId <= 0) {
                System.out.println("Invalid sender userId from notification.");
                new Alert(Alert.AlertType.ERROR, "Invalid sender user ID in the notification.").showAndWait();
                return;
            }

            // Verify senderUserId corresponds to a student
            if (!TeacherApproval.isUserStudent(senderUserId)) {
                System.out.println("UserId " + senderUserId + " is not a student.");
                new Alert(Alert.AlertType.ERROR, "Notification sender is not a student.").showAndWait();
                return;
            }

            int studentId = TeacherApproval.getStudentIdByUserId(senderUserId);
            if (studentId == -1) {
                System.out.println("Could not resolve student from sender userId: " + senderUserId);
                new Alert(Alert.AlertType.ERROR, "Cannot find student linked to this notification sender.").showAndWait();
                return;
            }
            System.out.println("Resolved studentId: " + studentId);

            // Parse message fields
            String fullMessage = note.getMessage();
            String dateString;
            String reason;
            String courseName;

            try {
                int dateKeywordIndex = fullMessage.indexOf("for ");
                int dateStart = dateKeywordIndex + 4;
                int dateEnd = fullMessage.indexOf(" in course", dateStart);
                dateString = fullMessage.substring(dateStart, dateEnd).trim();

                LocalDate.parse(dateString);

                int courseStart = fullMessage.indexOf(" in course", dateEnd) + " in course".length();
                int courseEnd = fullMessage.indexOf(":", courseStart);
                courseName = fullMessage.substring(courseStart, courseEnd).trim();

                // Just take everything after the last colon (:) as reason text
                int lastColonIndex = fullMessage.lastIndexOf(":");
                reason = fullMessage.substring(lastColonIndex + 1).trim();

            } catch (Exception ex) {
                System.out.println("Failed to parse date/course/reason: " + ex.getMessage());
                new Alert(Alert.AlertType.ERROR, "Failed to parse excuse details from notification message.").showAndWait();
                return;
            }



            System.out.println("Parsed details -> Date: " + dateString + ", Course: " + courseName + ", Reason: " + reason);

            // Get enrollment ID
            int enrollmentId = TeacherApproval.getEnrollmentId(studentId, courseName);
            if (enrollmentId == -1) {
                System.out.println("Enrollment not found for student " + studentId + " and course " + courseName);
                new Alert(Alert.AlertType.ERROR, "Enrollment not found for this student and course.").showAndWait();
                return;
            }

            // Get current teacher ID from logged-in user ID (recipient_id)
            int currentUserId = Session.getCurrentUser().getUserId();
            int teacherId = TeacherApproval.getTeacherIdByUserId(currentUserId);

            if (teacherId == -1) {
                System.out.println("Current user is not a teacher or teacher ID not found.");
                new Alert(Alert.AlertType.ERROR, "You must be logged in as a teacher to approve excuses.").showAndWait();
                return;
            }

            // Approve excuse and update attendance table
            boolean success = TeacherApproval.approveExcuse(studentId, courseName, dateString, reason, teacherId);

            if (success) {
                System.out.println("Attendance updated to Excused.");
                new Alert(Alert.AlertType.INFORMATION, "Excuse approved and attendance updated.").showAndWait();

                // Send notification to student that excuse is accepted
                StudentNotificationDAO.sendExcuseAcceptedNotification(studentId, courseName, LocalDate.parse(dateString));
            } else {
                // ...
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
            System.out.println("Sender UserId: " + senderUserId);

            if (senderUserId <= 0) {
                System.out.println("Invalid sender userId from notification.");
                new Alert(Alert.AlertType.ERROR, "Invalid sender user ID in the notification.").showAndWait();
                return;
            }

            // Verify senderUserId corresponds to a student
            if (!TeacherApproval.isUserStudent(senderUserId)) {
                System.out.println("UserId " + senderUserId + " is not a student.");
                new Alert(Alert.AlertType.ERROR, "Notification sender is not a student.").showAndWait();
                return;
            }

            int studentId = TeacherApproval.getStudentIdByUserId(senderUserId);
            if (studentId == -1) {
                System.out.println("Could not resolve student from sender userId: " + senderUserId);
                new Alert(Alert.AlertType.ERROR, "Cannot find student linked to this notification sender.").showAndWait();
                return;
            }
            System.out.println("Resolved studentId: " + studentId);

            // Parse message fields
            String fullMessage = note.getMessage();
            String dateString;
            String courseName;

            try {
                int dateKeywordIndex = fullMessage.indexOf("for ");
                int dateStart = dateKeywordIndex + 4;
                int dateEnd = fullMessage.indexOf(" in course", dateStart);
                dateString = fullMessage.substring(dateStart, dateEnd).trim();

                // Validate date format
                LocalDate.parse(dateString);

                int courseStart = fullMessage.indexOf(" in course", dateEnd) + " in course".length();
                int courseEnd = fullMessage.indexOf(":", courseStart);
                courseName = fullMessage.substring(courseStart, courseEnd).trim();

            } catch (Exception ex) {
                System.out.println("Failed to parse date/course: " + ex.getMessage());
                new Alert(Alert.AlertType.ERROR, "Failed to parse excuse details from notification message.").showAndWait();
                return;
            }

            System.out.println("Parsed details -> Date: " + dateString + ", Course: " + courseName);

            // Get enrollment ID
            int enrollmentId = TeacherApproval.getEnrollmentId(studentId, courseName);
            if (enrollmentId == -1) {
                System.out.println("Enrollment not found for student " + studentId + " and course " + courseName);
                new Alert(Alert.AlertType.ERROR, "Enrollment not found for this student and course.").showAndWait();
                return;
            }

            // Get current teacher ID from logged-in user ID (recipient_id)
            int currentUserId = Session.getCurrentUser().getUserId();
            int teacherId = TeacherApproval.getTeacherIdByUserId(currentUserId);

            if (teacherId == -1) {
                System.out.println("Current user is not a teacher or teacher ID not found.");
                new Alert(Alert.AlertType.ERROR, "You must be logged in as a teacher to reject excuses.").showAndWait();
                return;
            }

            // Reject excuse (mark absent)
            boolean success = TeacherApproval.rejectExcuse(studentId, courseName, dateString, teacherId);

            if (success) {
                System.out.println("Excuse rejected and marked as Absent.");
                new Alert(Alert.AlertType.INFORMATION, "Excuse rejected and attendance marked as absent.").showAndWait();
                StudentNotificationDAO.sendExcuseRejectedNotification(studentId, courseName, LocalDate.parse(dateString));

            } else {
                System.out.println("Failed to reject excuse.");
                new Alert(Alert.AlertType.ERROR, "Failed to update attendance record.").showAndWait();
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

    // Helper method to insert newlines every 'lineLength' characters without breaking words
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
