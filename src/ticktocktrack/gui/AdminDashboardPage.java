package ticktocktrack.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.UserIconUpdate;
import ticktocktrack.logic.UsersModel;

/**
 * Main JavaFX application class for the Admin Dashboard page.
 * 
 * <p>This class initializes and displays the admin dashboard window,
 * manages the primary stage, center content pane, and user icon display.</p>
 */
public class AdminDashboardPage extends Application {
    /**
     * The main stage (window) for the admin dashboard.
     */
    private Stage adminDashboardStage;

    /**
     * The pane that holds the center content of the dashboard UI.
     */
    private Pane centerContentPane;

    /**
     * The image view used to display the current user's icon/avatar.
     */
    private ImageView userIcon;

    /**
     * The main entry point for the JavaFX application.
     * Initializes and shows the primary stage with the admin dashboard UI.
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     */
	 @Override
	    public void start(Stage primaryStage) {
	        this.adminDashboardStage = primaryStage;
	        IconHelper.applyIcon(primaryStage);
	        
	        UsersModel currentUser = Session.getCurrentUser();
	        if (currentUser == null) {
	            System.err.println("No admin user logged in!");
	            Platform.exit();
	            return;
	        }

	        System.out.println("Logged in as: " + currentUser.getUsername());

	        // Root pane
	        StackPane root = new StackPane();
	        root.setPrefSize(1300, 750);
	        root.setStyle("-fx-background-color: white;");

	        // Create the center content using your AdminDashboardCenterPanel class
	        centerContentPane = AdminDashboardCenterPanel.createPanel();

	        root.getChildren().add(centerContentPane);

	        Scene scene = new Scene(root, 1300, 750);
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Admin - Dashboard");
	        primaryStage.setX(130);
	        primaryStage.setY(25);
	        primaryStage.show();

        // Overlay background (main colored background)
        Pane overlayPane = new Pane();
        overlayPane.setPrefSize(1300, 750);
        overlayPane.setStyle("-fx-background-color: #02383E;");
        root.getChildren().add(overlayPane);

        // Top panel
        Pane topPanel = new Pane();
        topPanel.setPrefSize(1700, 120);
        topPanel.setStyle("-fx-background-color: white;");
       
        // Logo
        String logoPath = getClass().getResource("/resources/TTT_logo.png").toExternalForm();
        ImageView logoView = new ImageView(new Image(logoPath));
        logoView.setFitWidth(85);
        logoView.setFitHeight(85);
        logoView.setLayoutX(17);
        logoView.setLayoutY(14);
        
        // Admin text
        Text adminText = new Text(currentUser.getUsername());
        adminText.setFont(Font.font("Poppins Medium", 20));
        adminText.setFill(Color.web("#02383E"));
        adminText.setLayoutX(1050);
        adminText.setLayoutY(65);
        
        // User icon setup
        String userIconPath = getClass().getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm();

        Image profileImage = UserIconUpdate.getCurrentUserProfileImage();
        Image userImage = profileImage != null ? profileImage : new Image(userIconPath);

        userIcon = new ImageView(userImage);
        userIcon.setFitWidth(67);
        userIcon.setFitHeight(67);
        userIcon.setLayoutX(1190);
        userIcon.setLayoutY(20);
        userIcon.setCursor(Cursor.HAND);

        Circle clip = new Circle(33.5, 33.5, 33.5);
        userIcon.setClip(clip);

     // --- User Icon Popup Handling (keep your event here) ---
        userIcon.setOnMouseClicked(event -> {
        Popup popup = new Popup();

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label profileLabel = new Label("Profile");
      
        Label logoutLabel = new Label("Logout");

        profileLabel.setCursor(Cursor.HAND);
       
        logoutLabel.setCursor(Cursor.HAND);

        String normalStyle = "-fx-text-fill: black; -fx-font-size: 16px;";
        String hoverStyle = "-fx-text-fill: #0077cc; -fx-font-size: 16px; -fx-underline: true;";

        for (Label label : new Label[]{profileLabel,  logoutLabel}) {
            label.setStyle(normalStyle);
            label.setOnMouseEntered(e -> label.setStyle(hoverStyle));
            label.setOnMouseExited(e -> label.setStyle(normalStyle));
        }

        profileLabel.setOnMouseClicked(this::onProfileClicked);
       
        logoutLabel.setOnMouseClicked(this::onLogoutClicked);

        box.getChildren().addAll(profileLabel,  logoutLabel);
        popup.getContent().add(box);
        popup.setAutoHide(true);

        double popupX = userIcon.localToScreen(userIcon.getBoundsInLocal()).getMinX() + userIcon.getFitWidth() / 2 - 50;
        double popupY = userIcon.localToScreen(userIcon.getBoundsInLocal()).getMaxY() + 5;
        popup.show(userIcon.getScene().getWindow(), popupX, popupY);
        });
      
       
        // Sidebar panel
        Pane sidePanel = new Pane();
        sidePanel.setPrefSize(258, 750);
        sidePanel.setLayoutX(0);
        sidePanel.setLayoutY(120);
        sidePanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1px;");
       

        // Dashboard icon
        String dashboardIconPath = getClass().getResource("/resources/Dashboard_icon.png").toExternalForm();
        ImageView dashboardIcon = new ImageView(new Image(dashboardIconPath));
        dashboardIcon.setFitWidth(35);
        dashboardIcon.setFitHeight(35);
        dashboardIcon.setLayoutX(46);
        dashboardIcon.setLayoutY(25);
      
        // Dashboard text
        Text dashboardText = new Text("Dashboard");
        dashboardText.setFont(Font.font("Poppins", 15));
        dashboardText.setFill(Color.web("#20B2AA"));
        dashboardText.setLayoutX(93);
        dashboardText.setLayoutY(52);
        sidePanel.getChildren().add(dashboardText);
        
     // Set the default selected text when the app launches
        selectedText = dashboardText;


     // Hover effect for Dashboard text
        dashboardText.setOnMouseMoved(e -> {
            if (selectedText != dashboardText) {
                dashboardText.setFill(Color.web("#20B2AA")); // Hover effect
            }
            dashboardText.setStyle("-fx-cursor: hand;"); // Change cursor on hover
        });
        

        dashboardText.setOnMouseExited(e -> {
            if (selectedText != dashboardText) {
                dashboardText.setFill(Color.web("#02383E")); // Revert if not selected
            }
        });

        dashboardText.setOnMouseClicked(e -> {
            selectSidebarText(dashboardText);
            onDashboardClicked(e);
        });


        // Line under dashboard
        String line1Path = getClass().getResource("/resources/Line1.png").toExternalForm();
        ImageView line1 = new ImageView(new Image(line1Path));
        line1.setFitWidth(180);
        line1.setFitHeight(2);
        line1.setLayoutX(30);
        line1.setLayoutY(90);
     
        // Add Icon
        String addIconPath = getClass().getResource("/resources/Admin_Dashboard/Admin_add_icon.png").toExternalForm();
        ImageView addIcon = new ImageView(new Image(addIconPath));
        addIcon.setFitWidth(31);
        addIcon.setFitHeight(31);
        addIcon.setLayoutX(46);
        addIcon.setLayoutY(129);
       
     // Create Users Text
        Text createUsersText = new Text("Create Users");
        createUsersText.setFont(Font.font("Poppins", 15)); // Set font size to 18 and Poppins font
        createUsersText.setFill(Color.web("#02383E")); // Set text color
        createUsersText.setLayoutX(93); // x position
        createUsersText.setLayoutY(153); // y position
        createUsersText.setWrappingWidth(135); // Set width for wrapping

        // Hover effect
        createUsersText.setOnMouseMoved(e -> {
            if (selectedText != createUsersText) {
                createUsersText.setFill(Color.web("#20B2AA")); // Change color on hover (if not selected)
            }
            createUsersText.setStyle("-fx-cursor: hand;"); // Change cursor on hover
        });

        // Reset the color when the mouse moves out
        createUsersText.setOnMouseExited(e -> {
            if (selectedText != createUsersText) {
                createUsersText.setFill(Color.web("#02383E")); // Revert to original color if not selected
            }
        });

        createUsersText.setOnMouseClicked(e -> {
            selectSidebarText(createUsersText);
            onCreateUsersClicked(e);
        });
        
     // View All icon image
        String viewAllPath = getClass().getResource("/resources/Admin_Dashboard/Admin_view_all_icon.png").toExternalForm();
        ImageView viewAllIcon = new ImageView(new Image(viewAllPath));
        viewAllIcon.setFitWidth(42);
        viewAllIcon.setFitHeight(42);
        viewAllIcon.setLayoutX(46); // x position
        viewAllIcon.setLayoutY(210); // y position
        

        // Add "View All Users" text
        Text viewAllUsersText = new Text("View All Users");
        viewAllUsersText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 15)); // Set font size to 18 and Poppins font (medium weight)
        viewAllUsersText.setFill(Color.web("#02383E")); // Set text color
        viewAllUsersText.setLayoutX(93); // x position
        viewAllUsersText.setLayoutY(237); // y position
        viewAllUsersText.setWrappingWidth(153); // Set width for wrapping
        
     // Hover effect
        viewAllUsersText.setOnMouseMoved(e -> {
            if (selectedText != viewAllUsersText) {
                viewAllUsersText.setFill(Color.web("#20B2AA")); // Change color on hover (if not selected)
            }
            viewAllUsersText.setStyle("-fx-cursor: hand;"); // Change cursor on hover
        });

        // Reset the color when the mouse moves out
        viewAllUsersText.setOnMouseExited(e -> {
            if (selectedText != viewAllUsersText) {
                viewAllUsersText.setFill(Color.web("#02383E")); // Revert to original color if not selected
            }
        });

        // Click event
        viewAllUsersText.setOnMouseClicked(e -> {
            selectSidebarText(viewAllUsersText);
            onViewAllUsersClicked(e); // You can define this method to handle the click
        });
        
        // Line 2 image
        String line2Path = getClass().getResource("/resources/Line2.png").toExternalForm();
        ImageView line2 = new ImageView(new Image(line2Path));
        line2.setFitWidth(180);  // width of line 2
        line2.setFitHeight(0);   // height of line 2 (0 for a thin line)
        line2.setLayoutX(30); // x position
        line2.setLayoutY(288); // y position
        
        

        // Hammer wrench icon image
        String hammerWrenchPath = getClass().getResource("/resources/Admin_Dashboard/Admin_hammer_wrench_icon.png").toExternalForm();
        ImageView hammerWrenchIcon = new ImageView(new Image(hammerWrenchPath));
        hammerWrenchIcon.setFitWidth(34);
        hammerWrenchIcon.setFitHeight(34);
        hammerWrenchIcon.setLayoutX(46); // x position
        hammerWrenchIcon.setLayoutY(335); // y position

        // Add "Manage Accounts" text
        Text manageAccountsText = new Text("Manage Accounts");
        manageAccountsText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 15)); // Set font size to 18 and Poppins font (medium weight)
        manageAccountsText.setFill(Color.web("#02383E")); // Set text color
        manageAccountsText.setLayoutX(93); // x position
        manageAccountsText.setLayoutY(355); // y position
        manageAccountsText.setWrappingWidth(135); // Set width for wrapping
     
        // Hover effect for Manage Accounts text
        manageAccountsText.setOnMouseMoved(e -> {
            if (selectedText != manageAccountsText) {
                manageAccountsText.setFill(Color.web("#20B2AA"));  // Change color on hover (if not selected)
            }
            manageAccountsText.setStyle("-fx-cursor: hand;");  // Change cursor to hand on hover
        });

        // Reset the color when the mouse moves out
        manageAccountsText.setOnMouseExited(e -> {
            if (selectedText != manageAccountsText) {
                manageAccountsText.setFill(Color.web("#02383E"));  // Revert to original color if not selected
            }
        });

        // Click event for Manage Accounts text
        manageAccountsText.setOnMouseClicked(e -> {
            selectSidebarText(manageAccountsText);  // Call this method to handle selection behavior (define as needed)
            onManageAccountsClicked(e);  // Define this method to handle the click (e.g., open the Manage Accounts page)
        });
     // center panel (initially set to dashboard panel content)
        centerContentPane = AdminDashboardCenterPanel.createPanel();
        centerContentPane.setLayoutX(258);
        centerContentPane.setLayoutY(120);
        
        
        
        // Add all elements to topPanel
        topPanel.getChildren().addAll(logoView, adminText);
        
        sidePanel.getChildren().addAll( dashboardIcon, line1, addIcon, createUsersText, viewAllIcon, viewAllUsersText,  line2, hammerWrenchIcon, manageAccountsText );
        
        overlayPane.getChildren().addAll(topPanel, sidePanel, centerContentPane, userIcon);
        
        dashboardText.setOnMouseClicked(this::onDashboardClicked);
        
        
        
       
    }
	 
	    /**
	     * Loads the current user's profile image into the user icon.
	     * If no custom profile image is found, loads a default admin icon.
	     */
	 public void loadUserIcon() {
	        Image profileImage = UserIconUpdate.getCurrentUserProfileImage();
	        if (profileImage != null) {
	            userIcon.setImage(profileImage);
	        } else {
	            String defaultIconPath = getClass().getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm();
	            userIcon.setImage(new Image(defaultIconPath));
	        }
	    }
 
	    /**
	     * Handles the event when the profile icon is clicked.
	     * Opens the user profile window and refreshes the user icon after changes.
	     * 
	     * @param event the mouse click event triggering this handler
	     */
	 private void onProfileClicked(MouseEvent event) {
		    System.out.println("Profile clicked");

		    try {
		        UserProfile userProfileWindow = new UserProfile(() -> {
		            loadUserIcon();
		        });
		        Stage stage = new Stage();
		        userProfileWindow.start(stage);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}  

	    /**
	     * Handles the logout click event.
	     * Logs out the current session and navigates back to the home screen.
	     * 
	     * @param event the mouse click event triggering this handler
	     */
    private void onLogoutClicked(MouseEvent event) {
        Session.logoutAndGoHome(adminDashboardStage);
    }
   
    /**
     * Tracks the currently selected sidebar Text element to manage highlight state.
     */
    private Text selectedText; // Track the currently selected sidebar Text
    
    /**
     * Highlights the selected sidebar text and resets the previous selection.
     * 
     * @param newSelectedText the new Text element to be highlighted
     */
    private void selectSidebarText(Text newSelectedText) {
        if (selectedText != null && selectedText != newSelectedText) {
            selectedText.setFill(Color.web("#02383E")); // Reset old selected color
        }
        selectedText = newSelectedText;
        selectedText.setFill(Color.web("#20B2AA")); 
        // Set new selected color
    }

    /**
     * Clears any highlights on the sidebar by resetting the selected text color.
     */
    @SuppressWarnings("unused")
	private void clearSidebarHighlights() {
        if (selectedText != null) {
            selectedText.setFill(Color.web("#02383E")); // Reset the color of the previously selected text
            selectedText = null;
        }
    }

    /**
     * Handles the event when the Dashboard sidebar option is clicked.
     * Updates the center content pane to show the dashboard panel.
     * 
     * @param event the mouse click event triggering this handler
     */
    private void onDashboardClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();
        Pane dashboardPanel = AdminDashboardCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }
    
    /**
     * Handles the event when the Create Users sidebar option is clicked.
     * Loads the user creation panel into the center content pane.
     * Verifies that the current user is logged in and is an admin.
     * 
     * @param event the mouse click event triggering this handler
     */
    private void onCreateUsersClicked(MouseEvent event) {
        System.out.println("Create Users clicked!");

        selectSidebarText((Text) event.getSource());

        centerContentPane.getChildren().clear();

        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            return;
        }

        Integer adminId = currentUser.getAdminId();
        if (adminId == null) {
            System.err.println("Current user is not an admin.");
            return;
        }

        Pane createUsersPanel = AdminCreateUsersCenterPanel.createPanel(adminId);
        centerContentPane.getChildren().add(createUsersPanel);
    }
    
    /**
     * Handles the event when the View All Users sidebar option is clicked.
     * Loads the view all users panel into the center content pane.
     * Verifies that the current user is logged in and is an admin.
     * 
     * @param event the mouse click event triggering this handler
     */
    private void onViewAllUsersClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();

        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            return;
        }

        Integer adminId = currentUser.getAdminId();
        if (adminId == null) {
            System.err.println("Current user is not an admin.");
            return;
        }

        Pane dashboardPanel = AdminViewAllUsersCenterPanel.createPanel(adminId);
        centerContentPane.getChildren().add(dashboardPanel);
    }
    
    /**
     * Handles the event when the Manage Accounts sidebar option is clicked.
     * Loads the manage accounts panel into the center content pane.
     * Verifies that the current user is logged in and is an admin.
     * 
     * @param event the mouse click event triggering this handler
     */
    private void onManageAccountsClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();

        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            return;
        }

        Integer adminId = currentUser.getAdminId();
        if (adminId == null) {
            System.err.println("Current user is not an admin.");
            return;
        }

        Pane dashboardPanel = AdminManageAccountsCenterPanel.createPanel(adminId);
        centerContentPane.getChildren().add(dashboardPanel);
    }
    

    /**
     * The main entry point for launching the application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
