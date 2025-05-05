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

public class TeacherDashboardPage extends Application {
	private Stage teacherDashboardStage;
	private Pane centerContentPane;
	private TeacherNotificationPane notificationPane; 
	 // Declare the notificationDot as an instance variable to update its visibility later
   
	// Store reference to the currently selected text
    @Override
    public void start(Stage primaryStage) {
    	 // Store the reference to the primaryStage
        this.teacherDashboardStage = primaryStage;
        
        // Root pane
        StackPane root = new StackPane();
        root.setPrefSize(1300, 750);
        root.setStyle("-fx-background-color: white;");
        
     // Create the center content using your AdminDashboardCenterPanel class
        centerContentPane = TeacherDashboardCenterPanel.createPanel();

        Scene scene = new Scene(root, 1300, 750);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Teacher - Dashboard");
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
        
        // Notification
        notificationPane = new TeacherNotificationPane();
        notificationPane.getNotificationIconWrapper().setLayoutX(1030);
        notificationPane.getNotificationIconWrapper().setLayoutY(30);

        // Add a click event to show/hide the notification popup
        notificationPane.getNotificationIconWrapper().setOnMouseClicked(e -> {
            if (notificationPane.isPopupShowing()) {
                notificationPane.hidePopup();
            } else {
                double x = notificationPane.getNotificationIcon().localToScreen(notificationPane.getNotificationIcon().getBoundsInLocal()).getMinX();
                double y = notificationPane.getNotificationIcon().localToScreen(notificationPane.getNotificationIcon().getBoundsInLocal()).getMaxY();
                notificationPane.showPopup(x - 100, y + 5);
            }
        });

        
     // Teacher text
        Text teacherText = new Text("Teacher");
        teacherText.setFont(Font.font("Poppins Medium", 25));
        teacherText.setFill(Color.web("#02383E"));
        teacherText.setLayoutX(1100);
        teacherText.setLayoutY(65);
        
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
     
     // Calendar Icon
        String calendarIconPath = getClass().getResource("/resources/Teacher_Dashboard/Teacher_calendar_icon.png").toExternalForm();
        ImageView calendarIcon = new ImageView(new Image(calendarIconPath));
        calendarIcon.setFitWidth(51); // Set width for the calendar icon
        calendarIcon.setFitHeight(51); // Set height for the calendar icon
        calendarIcon.setLayoutX(26); // x position
        calendarIcon.setLayoutY(130); // y position (adjust as needed)

       
     // Mark Attendance Text
        Text markAttendanceText = new Text("Mark Attendance");
        markAttendanceText.setFont(Font.font("Poppins", 18)); // Set font size to 18 and Poppins font
        markAttendanceText.setFill(Color.web("#02383E")); // Set text color
        markAttendanceText.setLayoutX(90); // x position
        markAttendanceText.setLayoutY(150); // y position (adjust as needed)
        markAttendanceText.setWrappingWidth(135); // Set width for wrapping

        // Hover effect
        markAttendanceText.setOnMouseMoved(e -> {
            if (selectedText != markAttendanceText) {
                markAttendanceText.setFill(Color.web("#20B2AA")); // Change color on hover (if not selected)
            }
            markAttendanceText.setStyle("-fx-cursor: hand;"); // Change cursor on hover
        });

        // Reset the color when the mouse moves out
        markAttendanceText.setOnMouseExited(e -> {
            if (selectedText != markAttendanceText) {
                markAttendanceText.setFill(Color.web("#02383E")); // Revert to original color if not selected
            }
        });

        // Click handler
        markAttendanceText.setOnMouseClicked(e -> {
            selectSidebarText(markAttendanceText);
            onMarkAttendanceClicked(e);
        });
        
     // Line 2 image
        String line2Path = getClass().getResource("/resources/Line2.png").toExternalForm();
        ImageView line2 = new ImageView(new Image(line2Path));
        line2.setFitWidth(180);  // width of line 2
        line2.setFitHeight(0);   // height of line 2 (0 for a thin line)
        line2.setLayoutX(30); // x position
        line2.setLayoutY(220); // y position
        
     // View Class List icon image
        String viewAllPath = getClass().getResource("/resources/Teacher_Dashboard/Teacher_view_all_icon.png").toExternalForm();
        ImageView viewAllIcon = new ImageView(new Image(viewAllPath));
        viewAllIcon.setFitWidth(69);
        viewAllIcon.setFitHeight(69);
        viewAllIcon.setLayoutX(17); // x position
        viewAllIcon.setLayoutY(250); // y position

     // View Class List Text
        Text viewClassListText = new Text("View Class List");
        viewClassListText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 18)); // Set font size to 18 and Poppins font (medium weight)
        viewClassListText.setFill(Color.web("#02383E")); // Set text color
        viewClassListText.setLayoutX(90); // x position
        viewClassListText.setLayoutY(285); // y position (adjust as needed)
        viewClassListText.setWrappingWidth(153); // Set width for wrapping

        // Hover effect
        viewClassListText.setOnMouseMoved(e -> {
            if (selectedText != viewClassListText) {
                viewClassListText.setFill(Color.web("#20B2AA")); // Change color on hover (if not selected)
            }
            viewClassListText.setStyle("-fx-cursor: hand;"); // Change cursor on hover
        });

        // Reset the color when the mouse moves out
        viewClassListText.setOnMouseExited(e -> {
            if (selectedText != viewClassListText) {
                viewClassListText.setFill(Color.web("#02383E")); // Revert to original color if not selected
            }
        });

        // Click event
        viewClassListText.setOnMouseClicked(e -> {
            selectSidebarText(viewClassListText);
            onViewClassListClicked(e); // You can define this method to handle the click
        });

        
     // Add Course icon image
        String addCourseIconPath = getClass().getResource("/resources/Teacher_Dashboard/Teacher_add_course_icon.png").toExternalForm();
        ImageView addCourseIcon = new ImageView(new Image(addCourseIconPath));
        addCourseIcon.setFitWidth(56); // Set width
        addCourseIcon.setFitHeight(56); // Set height
        addCourseIcon.setLayoutX(25); // x position (adjust if needed)
        addCourseIcon.setLayoutY(340); // y position (adjust if needed)

        
     // Create "Add Course" Text
        Text addCourseText = new Text("Add Course");
        addCourseText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 18)); // Set font size and weight
        addCourseText.setFill(Color.web("#02383E")); // Set initial text color
        addCourseText.setLayoutX(90); // x position
        addCourseText.setLayoutY(373); // y position (adjust if needed)
        addCourseText.setWrappingWidth(135); // Set width for wrapping

        // Hover effect for Add Course text
        addCourseText.setOnMouseMoved(e -> {
            if (selectedText != addCourseText) {
                addCourseText.setFill(Color.web("#20B2AA")); // Change color on hover (if not selected)
            }
            addCourseText.setStyle("-fx-cursor: hand;"); // Change cursor to hand on hover
        });

        // Reset the color when the mouse moves out
        addCourseText.setOnMouseExited(e -> {
            if (selectedText != addCourseText) {
                addCourseText.setFill(Color.web("#02383E")); // Revert to original color if not selected
            }
        });

        // Click event for Add Course text
        addCourseText.setOnMouseClicked(e -> {
            selectSidebarText(addCourseText); // Highlight selection
            onAddCourseClicked(e); // Handle the click (define this method)
        });
        
     // Line 3 image
        String line3Path = getClass().getResource("/resources/Line3.png").toExternalForm();
        ImageView line3 = new ImageView(new Image(line3Path));
        line3.setFitWidth(180);  // width of line 3
        line3.setFitHeight(0);   // height of line 3 (0 for a thin line)
        line3.setLayoutX(30);    // x position (adjust if needed)
        line3.setLayoutY(425);   // y position (adjust for spacing between sections)
        
     // Attendance icon image
        String attendanceIconPath = getClass().getResource("/resources/Teacher_Dashboard/Teacher_summary_icon.png").toExternalForm();
        ImageView attendanceIcon = new ImageView(new Image(attendanceIconPath));
        attendanceIcon.setFitWidth(53);   // width
        attendanceIcon.setFitHeight(53);  // height
        attendanceIcon.setLayoutX(26);    // x position
        attendanceIcon.setLayoutY(455);   // y position

     // Add "Attendance Summary" text
        Text attendanceSummaryText = new Text("Attendance Summary");
        attendanceSummaryText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 18)); // Set font size to 18 and Poppins font (medium weight)
        attendanceSummaryText.setFill(Color.web("#02383E")); // Set text color
        attendanceSummaryText.setLayoutX(90); // x position
        attendanceSummaryText.setLayoutY(475); // y position
        attendanceSummaryText.setWrappingWidth(135); // Set width for wrapping

        // Hover effect for Attendance Summary text
        attendanceSummaryText.setOnMouseMoved(e -> {
            if (selectedText != attendanceSummaryText) {
                attendanceSummaryText.setFill(Color.web("#20B2AA"));  // Change color on hover (if not selected)
            }
            attendanceSummaryText.setStyle("-fx-cursor: hand;");  // Change cursor to hand on hover
        });

        // Reset the color when the mouse moves out
        attendanceSummaryText.setOnMouseExited(e -> {
            if (selectedText != attendanceSummaryText) {
                attendanceSummaryText.setFill(Color.web("#02383E"));  // Revert to original color if not selected
            }
        });

        // Click event for Attendance Summary text
        attendanceSummaryText.setOnMouseClicked(e -> {
            selectSidebarText(attendanceSummaryText);  // Call this method to handle selection behavior (define as needed)
            onAttendanceSummaryClicked(e);  // Define this method to handle the click (e.g., open Attendance Summary page)
        });
        
     // Individual Reports icon image
        String individualReportsIconPath = getClass().getResource("/resources/Teacher_Dashboard/Teacher_report_icon.png").toExternalForm();
        ImageView individualReportsIcon = new ImageView(new Image(individualReportsIconPath));
        individualReportsIcon.setFitWidth(53);   // width
        individualReportsIcon.setFitHeight(53);  // height
        individualReportsIcon.setLayoutX(26);    // x position
        individualReportsIcon.setLayoutY(540);   // y position
        
     // Add "Individual Reports" text
        Text individualReportsText = new Text("Individual Reports");
        individualReportsText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 18)); // Set font size to 18 and Poppins font (medium weight)
        individualReportsText.setFill(Color.web("#02383E")); // Set text color
        individualReportsText.setLayoutX(90); // x position
        individualReportsText.setLayoutY(560); // y position
        individualReportsText.setWrappingWidth(135); // Set width for wrapping

        // Hover effect for Individual Reports text
        individualReportsText.setOnMouseMoved(e -> {
            if (selectedText != individualReportsText) {
                individualReportsText.setFill(Color.web("#20B2AA"));  // Change color on hover (if not selected)
            }
            individualReportsText.setStyle("-fx-cursor: hand;");  // Change cursor to hand on hover
        });

        // Reset the color when the mouse moves out
        individualReportsText.setOnMouseExited(e -> {
            if (selectedText != individualReportsText) {
                individualReportsText.setFill(Color.web("#02383E"));  // Revert to original color if not selected
            }
        });

        // Click event for Individual Reports text
        individualReportsText.setOnMouseClicked(e -> {
            selectSidebarText(individualReportsText);  // Call this method to handle selection behavior (define as needed)
            onIndividualReportsClicked(e);  // Define this method to handle the click (e.g., open Individual Reports page)
        });
        
     // center panel (initially set to dashboard panel content)
        centerContentPane = TeacherDashboardCenterPanel.createPanel();
        centerContentPane.setLayoutX(258);
        centerContentPane.setLayoutY(120);
        
        searchBox.getChildren().addAll(searchBar, searchIcon);
        
        // Add all elements to topPanel
        topPanel.getChildren().addAll(logoView, searchBox, teacherText);
        
        sidePanel.getChildren().addAll( dashboardIcon, line1, calendarIcon, markAttendanceText, viewAllIcon, viewClassListText, line3, addCourseIcon, addCourseText,  line2, attendanceIcon, attendanceSummaryText, individualReportsIcon, individualReportsText);
        

     // Ensure the notificationPane icon is added last to overlayPane
        overlayPane.getChildren().addAll(topPanel, sidePanel, centerContentPane, userIcon, notificationPane.getNotificationIconWrapper());

    


        
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
        if (teacherDashboardStage != null) {
            teacherDashboardStage.close();
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
        Pane dashboardPanel = TeacherDashboardCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }
    private void onMarkAttendanceClicked(MouseEvent event) {
        System.out.println("Create Users clicked!");
        centerContentPane.getChildren().clear();
        Pane createUsersPanel = TeacherMarkAttendanceCenterPanel.createPanel();
        centerContentPane.getChildren().add(createUsersPanel);
    }
    
    private void  onViewClassListClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();
        Pane dashboardPanel = TeacherViewClassListCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }
    
    private void onAddCourseClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();
        Pane dashboardPanel = TeacherAddCourseCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }
    
    private void onAttendanceSummaryClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();
        Pane dashboardPanel = TeacherAttendanceSummaryCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }
    
    private void onIndividualReportsClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();
        Pane dashboardPanel = TeacherIndividualReportsCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
