	package ticktocktrack.gui;
	
	import javafx.geometry.Insets;
	import javafx.scene.control.Label;
	import javafx.scene.layout.HBox;
	import javafx.scene.layout.Pane;
	import javafx.scene.layout.VBox;
	import javafx.scene.shape.Circle;
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
	
	import javafx.util.Duration;
	import java.time.format.DateTimeFormatter;
	import java.util.List;
	import java.util.function.Consumer;
	import javafx.scene.control.ContextMenu;
	import javafx.scene.control.MenuItem;
	import javafx.geometry.Side;
	import javafx.animation.PauseTransition;
	import javafx.scene.layout.Priority;
	import javafx.scene.control.Button;
	
	public class StudentNotificationPane {
		private final int PAGE_SIZE = 3;  // Number of notifications to load each time
		private int loadedNotificationCount = 0; // How many have been loaded so far
		private boolean allNotificationsLoaded = false; // Flag when no more notifications left

		
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
	        scrollPane.setPrefWidth(300);
	        scrollPane.setPrefHeight(400);
	
	        scrollPane.setFitToWidth(true); // Makes VBox expand to width of ScrollPane
	
	        scrollPane.setStyle(
	            "-fx-background-color: transparent; " +
	            "-fx-border-color: #20B2AA; " +
	            "-fx-border-width: 2px;"
	        );
	        
	        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
	            if (newVal.doubleValue() >= 0.9) { // Adjust threshold if needed
	                loadNotificationsFromDatabase();
	            }
	        });
	
	        notificationPopup.getContent().add(scrollPane);

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
	        if (allNotificationsLoaded) return; // no more to load
	        
	        System.out.println("DEBUG: Loading notifications for userId = " + userId + ", offset = " + loadedNotificationCount);
	        List<Notification> dbNotifications = StudentNotificationDAO.getNotificationsForUser(userId, loadedNotificationCount, PAGE_SIZE);

	        if (dbNotifications != null && !dbNotifications.isEmpty()) {
	            for (Notification n : dbNotifications) {
	                System.out.println("DEBUG: Notification loaded -> [Message: " + n.getMessage() +
	                        ", Type: " + n.getStatus() + ", Date: " + n.getDateSent() + "]");
	            }
	            notifications.addAll(dbNotifications);
	            loadedNotificationCount += dbNotifications.size();
	            for (Notification notification : dbNotifications) {
	                addNotificationToHolder(notification);
	            }
	        } else {
	            System.out.println("DEBUG: No more notifications returned from DAO");
	            allNotificationsLoaded = true;
	        }
	    }

	
	    // New overloaded method with senderUserId parameter
	    public void addNotification(String message, LocalDateTime dateSent, String status, int senderUserId) {
	        Notification note = new Notification(message, dateSent, status, senderUserId);
	        notifications.add(note);
	        addNotificationToHolder(note);
	    }
	
	
	    private void addNotificationToHolder(Notification notification) {
	        int senderUserId = notification.getSenderUserId();
	
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
	
	        iconView.setFitWidth(50);
	        iconView.setFitHeight(50);
	        iconView.setPreserveRatio(true);
	        
	        Circle clip = new Circle(25, 25, 25); // centerX, centerY, radius
	        iconView.setClip(clip);
	
	        // Message and date/status labels
	        Label notificationLabel = new Label(notification.getMessage());
	        notificationLabel.setFont(javafx.scene.text.Font.font("Poppins", 13));
	        notificationLabel.setWrapText(true);
	        notificationLabel.setMaxWidth(200);  // Leave space for icon
	        notificationLabel.setTextAlignment(TextAlignment.LEFT);
	
	        Label dateLabel = new Label(notification.getTimeAgo() + " | Status: " + notification.getStatus());
	        dateLabel.setFont(javafx.scene.text.Font.font("Poppins", 10));
	        dateLabel.setStyle("-fx-text-fill: gray;");
	
	        VBox content = new VBox(2, notificationLabel, dateLabel);
	
	        HBox notificationBox = new HBox(10, iconView, content); // Add icon to the left
	        notificationBox.setPadding(new Insets(5));
	        notificationBox.setStyle(
	            "-fx-background-color: #f9f9f9; " +
	            "-fx-border-radius: 5px; " +
	            "-fx-background-radius: 5px; " +
	            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 5, 0.5, 0, 0);"
	        );
	
	        addHoverEffect(notificationBox, notification.getNotificationId());
	        notificationHolder.getChildren().add(notificationBox);
	
	        // Optional: handle click for specific types
	        if (notification.getMessage().contains("marked you as Absent")) {
	            notificationBox.setOnMouseClicked(e -> {
	                System.out.println("DEBUG: Notification clicked, opening Submit Excuse from dashboard...");
	                dashboardPage.openSubmitExcuseFromNotification();
	                notificationPopup.hide();
	            });
	        }
	    }
	    
	    private ImageView getDefaultIcon() {
	        String iconPath = getClass().getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm();
	        return new ImageView(new Image(iconPath, 50, 50, true, true));
	    }
	
	
	    private void addHoverEffect(HBox notificationBox, int notificationId) {
	        // Create the button with the image
	    	String btnImagePath = getClass().getResource("/resources/others_button.png").toExternalForm();
	        ImageView btnIcon = new ImageView(new Image(btnImagePath, 30, 30, true, true));
	        Button hoverButton = new Button();
	        hoverButton.setGraphic(btnIcon);
	        hoverButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
	        hoverButton.setFocusTraversable(false);

	        // Create context menu
	        ContextMenu contextMenu = new ContextMenu();
	        MenuItem deleteItem = new MenuItem("Delete Notification");
	        deleteItem.setOnAction(ev -> {
	            boolean success = StudentNotificationDAO.deleteNotificationById(notificationId);
	            if (success) {
	                ((VBox) notificationBox.getParent()).getChildren().remove(notificationBox);
	                contextMenu.hide();
	            } else {
	                System.out.println("Failed to delete notification from database.");
	            }
	        });
	        contextMenu.getItems().add(deleteItem);

	        // Show context menu when button is clicked
	        hoverButton.setOnAction(e -> {
	            contextMenu.show(hoverButton, Side.BOTTOM, 0, 0);
	        });

	        // Hover styling and button display
	        notificationBox.setOnMouseEntered(e -> {
	            notificationBox.setStyle(
	                "-fx-background-color: #e0e0e0; -fx-border-radius: 5px; -fx-background-radius: 5px; " +
	                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2),5,0.5,0,0);"
	            );
	            if (!notificationBox.getChildren().contains(hoverButton)) {
	                notificationBox.getChildren().add(hoverButton);
	                HBox.setHgrow(hoverButton, Priority.NEVER);
	            }
	        });

	        // Delayed removal of button on exit (unless context menu is open)
	        PauseTransition exitDelay = new PauseTransition(Duration.millis(200));
	        notificationBox.setOnMouseExited(e -> {
	            exitDelay.setOnFinished(event -> {
	                if (!contextMenu.isShowing()) {
	                    notificationBox.setStyle(
	                        "-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-background-radius: 5px; " +
	                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15),5,0.5,0,0);"
	                    );
	                    notificationBox.getChildren().remove(hoverButton);
	                }
	            });
	            exitDelay.play();
	        });
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
	    