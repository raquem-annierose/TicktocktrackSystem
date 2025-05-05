package ticktocktrack.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class AdminDashboardPage extends Application {
	private Stage adminDashboardStage;
	private Pane centerContentPane;
	// Store reference to the currently selected text
	 @Override
	    public void start(Stage primaryStage) {
	        this.adminDashboardStage = primaryStage;

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
        Text adminText = new Text("Admin");
        adminText.setFont(Font.font("Poppins Medium", 25));
        adminText.setFill(Color.web("#02383E"));
        adminText.setLayoutX(1125);
        adminText.setLayoutY(65);
        
        // User Icon
        String userIconPath = getClass().getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm();
        ImageView userIcon = new ImageView(new Image(userIconPath));
        userIcon.setFitWidth(90);
        userIcon.setFitHeight(90);
        userIcon.setLayoutX(1200);
        userIcon.setLayoutY(5);
        userIcon.setCursor(Cursor.HAND);
        
     // --- User Icon Popup Handling (keep your event here) ---
        userIcon.setOnMouseClicked(event -> {
        Popup popup = new Popup();

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label profileLabel = new Label("Profile");
        Label settingsLabel = new Label("Settings");
        Label logoutLabel = new Label("Logout");

        profileLabel.setCursor(Cursor.HAND);
        settingsLabel.setCursor(Cursor.HAND);
        logoutLabel.setCursor(Cursor.HAND);

        String normalStyle = "-fx-text-fill: black; -fx-font-size: 16px;";
        String hoverStyle = "-fx-text-fill: #0077cc; -fx-font-size: 16px; -fx-underline: true;";

        for (Label label : new Label[]{profileLabel, settingsLabel, logoutLabel}) {
            label.setStyle(normalStyle);
            label.setOnMouseEntered(e -> label.setStyle(hoverStyle));
            label.setOnMouseExited(e -> label.setStyle(normalStyle));
        }

        profileLabel.setOnMouseClicked(this::onProfileClicked);
        settingsLabel.setOnMouseClicked(this::onSettingsClicked);
        logoutLabel.setOnMouseClicked(this::onLogoutClicked);

        box.getChildren().addAll(profileLabel, settingsLabel, logoutLabel);
        popup.getContent().add(box);
        popup.setAutoHide(true);

        double popupX = userIcon.localToScreen(userIcon.getBoundsInLocal()).getMinX() + userIcon.getFitWidth() / 2 - 50;
        double popupY = userIcon.localToScreen(userIcon.getBoundsInLocal()).getMaxY() + 5;
        popup.show(userIcon.getScene().getWindow(), popupX, popupY);
        });
      
        // Search Box
        StackPane searchBox = new StackPane();
        searchBox.setPrefWidth(300);
        searchBox.setPrefHeight(40);
        searchBox.setLayoutX(130);
        searchBox.setLayoutY(40);

        String searchIconPath = getClass().getResource("/resources/search_icon.png").toExternalForm();
        ImageView searchIcon = new ImageView(new Image(searchIconPath));
        searchIcon.setFitWidth(20);
        searchIcon.setFitHeight(20);
        StackPane.setAlignment(searchIcon, Pos.CENTER_LEFT);
        searchIcon.setTranslateX(10);

        TextField searchBar = new TextField();
        searchBar.setPromptText("Search and filter...");
        searchBar.setPrefWidth(300);
        searchBar.setPrefHeight(40);
        searchBar.setStyle("-fx-background-radius: 20;" +
                "-fx-border-radius: 20;" +
                "-fx-border-color: #ccc;" +
                "-fx-font-size: 16px;" +
                "-fx-padding: 0 15 0 30;");

        StackPane.setAlignment(searchBar, Pos.CENTER);

       
      
        // --- Search Bar Handling ---
        searchBar.setOnMouseClicked(event -> {
            System.out.println("Search bar clicked");
            searchBar.setFocusTraversable(true); // Make the search bar focusable
            searchBar.requestFocus(); // Focus the search bar when clicked
            if (searchBar.getText().equals("Search and filter...")) {
                searchBar.clear(); // Clear prompt text if clicked
            }
        });

        // Handle when text changes in the search bar
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            onSearchTextChanged(newValue); // Call the search function as text changes
        });

        // Handle when the "Enter" key is pressed (submit search)
        searchBar.setOnAction(event -> {
            String searchText = searchBar.getText();
            onSearchSubmit(searchText);  // Trigger the search logic when "Enter" is pressed
            // Reset prompt text after submit if the search bar is empty
            if (searchBar.getText().isEmpty()) {
                searchBar.setPromptText("Search and filter..."); // Reset to default prompt text
            }
        });

        // Handle losing focus and resetting prompt text
        Platform.runLater(() -> {
            searchBar.getScene().setOnMouseClicked(event -> {
                // Check if the click was outside the search bar
                if (!searchBox.contains(event.getX(), event.getY())) {
                    // If the search bar is focused but the click is outside, reset the search bar
                    if (searchBar.isFocused()) {
                        if (searchBar.getText().isEmpty()) {
                            searchBar.setPromptText("Search and filter..."); // Reset prompt text when empty
                        }
                    }
                    // Also handle losing focus case
                    if (!searchBar.isFocused() && searchBar.getText().isEmpty()) {
                        searchBar.setPromptText("Search and filter..."); // Reset prompt text
                    }
                }
            });

            // Ensure the search bar doesn't automatically focus when the scene is shown
            searchBar.setFocusTraversable(false); // Ensure focus is not automatically set
            
            // Explicitly remove focus from the search bar when the scene is displayed
            searchBar.requestFocus(); // Removing focus from search bar initially
            searchBar.getParent().requestFocus(); // Request focus on the parent container instead
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
        dashboardIcon.setFitWidth(55);
        dashboardIcon.setFitHeight(55);
        dashboardIcon.setLayoutX(22);
        dashboardIcon.setLayoutY(20);
      
        // Dashboard text
        Text dashboardText = new Text("Dashboard");
        dashboardText.setFont(Font.font("Poppins", 18));
        dashboardText.setFill(Color.web("#20B2AA"));
        dashboardText.setLayoutX(90);
        dashboardText.setLayoutY(60);
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
        addIcon.setFitWidth(51);
        addIcon.setFitHeight(51);
        addIcon.setLayoutX(26);
        addIcon.setLayoutY(130);
       
     // Create Users Text
        Text createUsersText = new Text("Create Users");
        createUsersText.setFont(Font.font("Poppins", 18)); // Set font size to 18 and Poppins font
        createUsersText.setFill(Color.web("#02383E")); // Set text color
        createUsersText.setLayoutX(90); // x position
        createUsersText.setLayoutY(160); // y position
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
        viewAllIcon.setFitWidth(69);
        viewAllIcon.setFitHeight(69);
        viewAllIcon.setLayoutX(17); // x position
        viewAllIcon.setLayoutY(210); // y position
        

        // Add "View All Users" text
        Text viewAllUsersText = new Text("View All Users");
        viewAllUsersText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 18)); // Set font size to 18 and Poppins font (medium weight)
        viewAllUsersText.setFill(Color.web("#02383E")); // Set text color
        viewAllUsersText.setLayoutX(90); // x position
        viewAllUsersText.setLayoutY(250); // y position
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
        line2.setLayoutY(310); // y position
        
        // Report icon image
        String reportIconPath = getClass().getResource("/resources/Admin_Dashboard/Admin_report_icon.png").toExternalForm();
        ImageView reportIcon = new ImageView(new Image(reportIconPath));
        reportIcon.setFitWidth(56);
        reportIcon.setFitHeight(56);
        reportIcon.setLayoutX(25); // x position
        reportIcon.setLayoutY(355); // y position
        
     // Create "Attendance Reports" Text
        Text attendanceReportsText = new Text("Attendance Reports");
        attendanceReportsText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 18));  // Set font size and weight
        attendanceReportsText.setFill(Color.web("#02383E"));  // Set initial text color
        attendanceReportsText.setLayoutX(90);  // x position
        attendanceReportsText.setLayoutY(375);  // y position
        attendanceReportsText.setWrappingWidth(135);  // Set width for wrapping

        // Hover effect for Attendance Reports text
        attendanceReportsText.setOnMouseMoved(e -> {
            if (selectedText != attendanceReportsText) {
                attendanceReportsText.setFill(Color.web("#20B2AA"));  // Change color on hover (if not selected)
            }
            attendanceReportsText.setStyle("-fx-cursor: hand;");  // Change cursor to hand on hover
        });

        // Reset the color when the mouse moves out
        attendanceReportsText.setOnMouseExited(e -> {
            if (selectedText != attendanceReportsText) {
                attendanceReportsText.setFill(Color.web("#02383E"));  // Revert to original color if not selected
            }
        });

        // Click event for Attendance Reports text
        attendanceReportsText.setOnMouseClicked(e -> {
            selectSidebarText(attendanceReportsText);  // Call this method to handle selection behavior (define as needed)
            onAttendanceReportsClicked(e);  // Define this method to handle the click (e.g., open a new page or show report)
        });
        
        // Hammer wrench icon image
        String hammerWrenchPath = getClass().getResource("/resources/Admin_Dashboard/Admin_hammer_wrench_icon.png").toExternalForm();
        ImageView hammerWrenchIcon = new ImageView(new Image(hammerWrenchPath));
        hammerWrenchIcon.setFitWidth(53);
        hammerWrenchIcon.setFitHeight(53);
        hammerWrenchIcon.setLayoutX(26); // x position
        hammerWrenchIcon.setLayoutY(455); // y position

        // Add "Manage Accounts" text
        Text manageAccountsText = new Text("Manage Accounts");
        manageAccountsText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 18)); // Set font size to 18 and Poppins font (medium weight)
        manageAccountsText.setFill(Color.web("#02383E")); // Set text color
        manageAccountsText.setLayoutX(90); // x position
        manageAccountsText.setLayoutY(480); // y position
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
        
        searchBox.getChildren().addAll(searchBar, searchIcon);
        
        // Add all elements to topPanel
        topPanel.getChildren().addAll(logoView, searchBox, adminText);
        
        sidePanel.getChildren().addAll( dashboardIcon, line1, addIcon, createUsersText, viewAllIcon, viewAllUsersText, reportIcon, attendanceReportsText,  line2, hammerWrenchIcon, manageAccountsText );
        
        overlayPane.getChildren().addAll(topPanel, sidePanel, centerContentPane, userIcon);
        
        dashboardText.setOnMouseClicked(this::onDashboardClicked);
        
        
        
       
    }
 // --- Search Logic Functions ---

    // Handle search text change dynamically
    private void onSearchTextChanged(String searchText) {
        System.out.println("Search text changed: " + searchText);
        // You can add your filtering or search logic here
        // For example, dynamically filtering a list of items based on searchText
    }

    // Handle search submit (when "Enter" key is pressed)
    private void onSearchSubmit(String searchText) {
        System.out.println("Search submitted: " + searchText);
        // Trigger your search or filtering logic here
        // Example: searching in a database or filtering items in a list
    }

    // Add a global listener to detect clicks outside the search box
    
    // Profile Functions
    private void onProfileClicked(MouseEvent event) {
        System.out.println("Profile clicked");
        // TODO: Open Profile page
    }

    private void onSettingsClicked(MouseEvent event) {
        System.out.println("Settings clicked");
        // TODO: Open Settings page
    }

    // Define the logout click handler
    private void onLogoutClicked(MouseEvent event) {
        System.out.println("Logout clicked");

        // Close the current window (AdminDashboardPage)
        if (adminDashboardStage != null) {
            adminDashboardStage.close();
        }

        // Launch the HomePage (login screen)
        HomePage homePage = new HomePage();
        Stage homeStage = new Stage();
        homePage.start(homeStage);  // Show HomePage
    }

    
    private Text selectedText; // Track the currently selected sidebar Text
    
    private void selectSidebarText(Text newSelectedText) {
        if (selectedText != null && selectedText != newSelectedText) {
            selectedText.setFill(Color.web("#02383E")); // Reset old selected color
        }
        selectedText = newSelectedText;
        selectedText.setFill(Color.web("#20B2AA")); 
        // Set new selected color
    }

    @SuppressWarnings("unused")
	private void clearSidebarHighlights() {
        if (selectedText != null) {
            selectedText.setFill(Color.web("#02383E")); // Reset the color of the previously selected text
            selectedText = null;
        }
    }

    private void onDashboardClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();
        Pane dashboardPanel = AdminDashboardCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }
    private void onCreateUsersClicked(MouseEvent event) {
        System.out.println("Create Users clicked!");
        centerContentPane.getChildren().clear();
        Pane createUsersPanel = AdminCreateUsersCenterPanel.createPanel();
        centerContentPane.getChildren().add(createUsersPanel);
    }
    
    private void onViewAllUsersClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();
        Pane dashboardPanel = AdminViewAllUsersCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }
    
    private void onAttendanceReportsClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();
        Pane dashboardPanel = AdminAttendanceReportsCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }
    
    private void onManageAccountsClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();
        Pane dashboardPanel = AdminManageAccountsCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
