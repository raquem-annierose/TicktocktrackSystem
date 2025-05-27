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
	
	/**
	 * Represents the notification panel in the student dashboard.
	 * Manages loading, displaying, and interacting with student notifications.
	 */
	public class StudentNotificationPane {

	    /** Number of notifications to load each time (pagination size). */
	    private final int PAGE_SIZE = 3;

	    /** Count of how many notifications have been loaded so far. */
	    private int loadedNotificationCount = 0;

	    /** Flag to indicate whether all notifications have been loaded. */
	    private boolean allNotificationsLoaded = false;

	    /** Reference to the parent student dashboard page. */
	    private StudentDashboardPage dashboardPage;

	    /** The user ID of the current student. */
	    private int userId;

	    /** Popup window for displaying notifications. */
	    private Popup notificationPopup;

	    /** ImageView representing the notification icon. */
	    private ImageView notificationIcon;

	    /** Wrapper around the notification icon, useful for styling and event handling. */
	    private StackPane notificationIconWrapper;

	    /** Container VBox that holds the notification items in the popup. */
	    private VBox notificationHolder;

	    /** Observable list holding the loaded notifications. */
	    private ObservableList<Notification> notifications;

	    /**
	     * Constructs a new StudentNotificationPane linked to the specified dashboard page.
	     *
	     * @param dashboardPage the StudentDashboardPage that this notification pane belongs to
	     */
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
	
	    /**
	     * Loads a batch of notifications from the database for the current user.
	     * Uses pagination to load notifications in chunks defined by PAGE_SIZE.
	     * If all notifications have already been loaded, this method returns immediately.
	     */
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

	
	    /**
	     * Adds a new notification with the given message, date sent, status, and sender user ID.
	     * 
	     * @param message The notification message content.
	     * @param dateSent The date and time the notification was sent.
	     * @param status The status of the notification (e.g., "read", "unread").
	     * @param senderUserId The user ID of the sender who generated this notification.
	     */
	    public void addNotification(String message, LocalDateTime dateSent, String status, int senderUserId) {
	        Notification note = new Notification(message, dateSent, status, senderUserId);
	        notifications.add(note);
	        addNotificationToHolder(note);
	    }
	    
	    /**
	     * Adds a single notification item to the notification holder UI container.
	     * Creates the visual representation of the notification and appends it to the notification list.
	     * 
	     * @param notification The Notification object to be displayed.
	     */
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
	    
	    /**
	     * Returns the default user icon as an ImageView with preset size.
	     * This icon is used when no specific user icon is available for a notification.
	     * 
	     * @return ImageView containing the default user icon image.
	     */
	    private ImageView getDefaultIcon() {
	        String iconPath = getClass().getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm();
	        return new ImageView(new Image(iconPath, 50, 50, true, true));
	    }
	    
	    /**
	     * Adds a hover effect to the given notification box.
	     * The effect typically involves changing styles or visuals on mouse enter and exit.
	     * 
	     * @param notificationBox The HBox representing the notification UI element.
	     * @param notificationId The unique identifier for the notification.
	     */
	    private void addHoverEffect(HBox notificationBox, int notificationId) {
	        // Create the button with the image
	        String btnImagePath = null;
	        try {
	            java.net.URL url = getClass().getResource("/resources/others_button.png");
	            if (url != null) {
	                btnImagePath = url.toExternalForm();
	            }
	        } catch (Exception e) {
	            btnImagePath = null;
	        }
	        ImageView btnIcon;
	        if (btnImagePath != null) {
	            btnIcon = new ImageView(new Image(btnImagePath, 30, 30, true, true));
	        } else {
	            // fallback to a default icon or just a blank button
	            btnIcon = new ImageView();
	        }
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

	
	    /**
	     * Gets the wrapper StackPane containing the notification icon.
	     * This wrapper may be used for positioning or styling purposes.
	     * 
	     * @return The StackPane that wraps the notification icon.
	     */
	    public StackPane getNotificationIconWrapper() {
	        return notificationIconWrapper;
	    }

	    /**
	     * Gets the ImageView representing the notification icon.
	     * This icon is typically displayed in the dashboard UI.
	     * 
	     * @return The ImageView of the notification icon.
	     */
	    public ImageView getNotificationIcon() {
	        return notificationIcon;
	    }

	    /**
	     * Shows the notification popup at the specified screen coordinates.
	     * 
	     * @param x The x-coordinate where the popup should appear.
	     * @param y The y-coordinate where the popup should appear.
	     */
	    public void showPopup(double x, double y) {
	        notificationPopup.show(notificationIconWrapper, x, y);
	    }

	    /**
	     * Hides the notification popup if it is currently visible.
	     */
	    public void hidePopup() {
	        notificationPopup.hide();
	    }

	    /**
	     * Checks whether the notification popup is currently visible on the screen.
	     * 
	     * @return true if the popup is showing, false otherwise.
	     */
	    public boolean isPopupShowing() {
	        return notificationPopup.isShowing();
	    }

	
	}
