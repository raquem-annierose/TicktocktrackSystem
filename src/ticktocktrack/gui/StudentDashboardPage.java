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
import ticktocktrack.logic.Session;
import ticktocktrack.logic.UsersModel;
import ticktocktrack.gui.StudentNotificationPane;

public class StudentDashboardPage extends Application {

    private Stage studentDashboardStage;
    private Pane centerContentPane;
    private StudentNotificationPane notificationPane;
    private Text selectedText;

    @Override
    public void start(Stage primaryStage) {
        this.studentDashboardStage = primaryStage;

        // Retrieve current user
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user logged in!");
            Platform.exit();
            return;
        }

        System.out.println("Logged in as: " + currentUser.getUsername());

        // Get userId from currentUser
        Integer userId = currentUser.getUserId(); // assuming method exists

        // Initialize NotificationPane with userId
        notificationPane = new StudentNotificationPane();

        // Root pane
        StackPane root = new StackPane();
        root.setPrefSize(1300, 750);
        root.setStyle("-fx-background-color: white;");

        // Create the center content using your StudentDashboardCenterPanel class
        centerContentPane = StudentDashboardCenterPanel.createPanel();

        Scene scene = new Scene(root, 1300, 750);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student - Dashboard");
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
        notificationPane.getNotificationIconWrapper().setLayoutX(990);
        notificationPane.getNotificationIconWrapper().setLayoutY(30);
        notificationPane.getNotificationIconWrapper().setOnMouseClicked(e -> {
            if (notificationPane.isPopupShowing()) {
                notificationPane.hidePopup();
            } else {
                double x = notificationPane.getNotificationIcon().localToScreen(notificationPane.getNotificationIcon().getBoundsInLocal()).getMinX();
                double y = notificationPane.getNotificationIcon().localToScreen(notificationPane.getNotificationIcon().getBoundsInLocal()).getMaxY();
                notificationPane.showPopup(x - 100, y + 5);
            }
        });

        // Student text
        Text studentText = new Text(currentUser.getUsername());
        studentText.setFont(Font.font("Poppins Medium", 20));
        studentText.setFill(Color.web("#02383E"));
        studentText.setLayoutX(1080);
        studentText.setLayoutY(65);

        // User Icon
        String userIconPath = getClass().getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm();
        ImageView userIcon = new ImageView(new Image(userIconPath));
        userIcon.setFitWidth(67);
        userIcon.setFitHeight(67);
        userIcon.setLayoutX(1190);
        userIcon.setLayoutY(20);
        userIcon.setCursor(Cursor.HAND);
        
        // --- User Icon Popup Handling ---
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
                // Check if the click was outside the search box
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
        dashboardIcon.setFitWidth(35);
        dashboardIcon.setFitHeight(35);
        dashboardIcon.setLayoutX(46);
        dashboardIcon.setLayoutY(25);

        // Dashboard text
        Text dashboardText = new Text("Dashboard");
        dashboardText.setFont(Font.font("Poppins", 15));
        dashboardText.setFill(Color.web("#20B2AA"));
        dashboardText.setLayoutX(93);
        dashboardText.setLayoutY(169);

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

        // Add "View My Attendance" Icon
        String viewAttendanceIconPath = getClass().getResource("/resources/Student_Dashboard/Student_view_attendance_icon.png").toExternalForm();
        ImageView viewAttendanceIcon = new ImageView(new Image(viewAttendanceIconPath));
        viewAttendanceIcon.setFitWidth(35);
        viewAttendanceIcon.setFitHeight(35);
        viewAttendanceIcon.setLayoutX(46);
        viewAttendanceIcon.setLayoutY(139);

        // Create View My Attendance Text
        Text viewAttendanceText = new Text("View My Attendance");
        viewAttendanceText.setFont(Font.font("Poppins", 15));
        viewAttendanceText.setFill(Color.web("#02383E"));
        viewAttendanceText.setLayoutX(93);
        viewAttendanceText.setLayoutY(152);
        viewAttendanceText.setWrappingWidth(135);

        // Hover effect
        viewAttendanceText.setOnMouseMoved(e -> {
            if (selectedText != viewAttendanceText) {
                viewAttendanceText.setFill(Color.web("#20B2AA"));
            }
            viewAttendanceText.setStyle("-fx-cursor: hand;");
        });
        viewAttendanceText.setOnMouseExited(e -> {
            if (selectedText != viewAttendanceText) {
                viewAttendanceText.setFill(Color.web("#02383E"));
            }
        });
        viewAttendanceText.setOnMouseClicked(e -> {
            selectSidebarText(viewAttendanceText);
            onViewMyAttendanceClicked(e);
        });

        // Line 2 image
        String line2Path = getClass().getResource("/resources/Line2.png").toExternalForm();
        ImageView line2 = new ImageView(new Image(line2Path));
        line2.setFitWidth(180);
        line2.setFitHeight(0);
        line2.setLayoutX(30);
        line2.setLayoutY(220);

        // Attendance Status icon image
        String attendanceStatusPath = getClass().getResource("/resources/Student_Dashboard/Student_attendance_status_icon.png").toExternalForm();
        ImageView attendanceStatusIcon = new ImageView(new Image(attendanceStatusPath));
        attendanceStatusIcon.setFitWidth(35);
        attendanceStatusIcon.setFitHeight(35);
        attendanceStatusIcon.setLayoutX(46);
        attendanceStatusIcon.setLayoutY(276);

        // Create Attendance Status Text
        Text attendanceStatusText = new Text("Attendance Status");
        attendanceStatusText.setFont(Font.font("Poppins", 15));
        attendanceStatusText.setFill(Color.web("#02383E"));
        attendanceStatusText.setLayoutX(93);
        attendanceStatusText.setLayoutY(295);
        attendanceStatusText.setWrappingWidth(135);

        // Hover effect
        attendanceStatusText.setOnMouseMoved(e -> {
            if (selectedText != attendanceStatusText) {
                attendanceStatusText.setFill(Color.web("#20B2AA"));
            }
            attendanceStatusText.setStyle("-fx-cursor: hand;");
        });
        attendanceStatusText.setOnMouseExited(e -> {
            if (selectedText != attendanceStatusText) {
                attendanceStatusText.setFill(Color.web("#02383E"));
            }
        });
        attendanceStatusText.setOnMouseClicked(e -> {
            selectSidebarText(attendanceStatusText);
            onAttendanceStatusClicked(e);
        });

        // Line 3 image
        String line3Path = getClass().getResource("/resources/Line3.png").toExternalForm();
        ImageView line3 = new ImageView(new Image(line3Path));
        line3.setFitWidth(180);
        line3.setFitHeight(0);
        line3.setLayoutX(30);
        line3.setLayoutY(360);

        // Submit Excuse icon image
        String submitExcuseIconPath = getClass().getResource("/resources/Student_Dashboard/Student_excuse_letter_icon.png").toExternalForm();
        ImageView submitExcuseIcon = new ImageView(new Image(submitExcuseIconPath));
        submitExcuseIcon.setFitWidth(35);
        submitExcuseIcon.setFitHeight(35);
        submitExcuseIcon.setLayoutX(46);
        submitExcuseIcon.setLayoutY(418);

        // Create "Submit Excuse" Text
        Text submitExcuseText = new Text("Submit Excuse");
        submitExcuseText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 15));
        submitExcuseText.setFill(Color.web("#02383E"));
        submitExcuseText.setLayoutX(93);
        submitExcuseText.setLayoutY(440);
        submitExcuseText.setWrappingWidth(135);

        // Hover effect for Submit Excuse text
        submitExcuseText.setOnMouseMoved(e -> {
            if (selectedText != submitExcuseText) {
                submitExcuseText.setFill(Color.web("#20B2AA"));
            }
            submitExcuseText.setStyle("-fx-cursor: hand;");
        });
        submitExcuseText.setOnMouseExited(e -> {
            if (selectedText != submitExcuseText) {
                submitExcuseText.setFill(Color.web("#02383E"));
            }
        });
        // Click event for Submit Excuse text
        submitExcuseText.setOnMouseClicked(e -> {
            selectSidebarText(submitExcuseText);
            onSubmitExcuseClicked(e);
        });

        // center panel (initially set to dashboard panel content)
        centerContentPane = StudentDashboardCenterPanel.createPanel();
        centerContentPane.setLayoutX(258);
        centerContentPane.setLayoutY(120);

        // Assemble search box
        searchBox.getChildren().addAll(searchBar, searchIcon);

        // Add all elements to topPanel
        topPanel.getChildren().addAll(logoView, searchBox, studentText);

        sidePanel.getChildren().addAll( dashboardIcon, line1, viewAttendanceIcon, viewAttendanceText, line2, attendanceStatusIcon, attendanceStatusText,   line3, submitExcuseIcon, submitExcuseText);

        // Overlay all to root
        overlayPane.getChildren().addAll(topPanel, sidePanel, centerContentPane, userIcon, dashboardText, notificationPane.getNotificationIconWrapper());

      
        // Add click handler
        dashboardText.setOnMouseClicked(this::onDashboardClicked);
    }

    // --- Search Logic Functions ---
    private void onSearchTextChanged(String searchText) {
        System.out.println("Search text changed: " + searchText);
        // Your filtering logic here
    }

    private void onSearchSubmit(String searchText) {
        System.out.println("Search submitted: " + searchText);
        // Your search submission logic here
    }

    // Profile Functions
    private void onProfileClicked(MouseEvent event) {
        System.out.println("Profile clicked");
        // TODO: Open Profile page
    }

    private void onSettingsClicked(MouseEvent event) {
        System.out.println("Settings clicked");
        // TODO: Open Settings page
    }

    private void onLogoutClicked(MouseEvent event) {
        Session.logoutAndGoHome(studentDashboardStage);
    }

    private void selectSidebarText(Text newSelectedText) {
        if (selectedText != null && selectedText != newSelectedText) {
            selectedText.setFill(Color.web("#02383E")); // Reset old
        }
        selectedText = newSelectedText;
        selectedText.setFill(Color.web("#20B2AA")); // New selection color
    }

    @SuppressWarnings("unused")
    private void clearSidebarHighlights() {
        if (selectedText != null) {
            selectedText.setFill(Color.web("#02383E"));
            selectedText = null;
        }
    }

    private void onDashboardClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource());
        centerContentPane.getChildren().clear();
        Pane dashboardPanel = StudentDashboardCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }

    private void onViewMyAttendanceClicked(MouseEvent event) {
        System.out.println("View My Attendance clicked!");
        selectSidebarText((Text) event.getSource());
        centerContentPane.getChildren().clear();
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            return;
        }
        Integer studentId = currentUser.getStudentId();
        if (studentId == null) {
            System.err.println("Current user is not a student.");
            return;
        }
        Pane attendancePanel = StudentViewMyAttendanceCenterPanel.createPanel(studentId);
        centerContentPane.getChildren().add(attendancePanel);
    }

    private void onAttendanceStatusClicked(MouseEvent event) {
        System.out.println("Attendance Status clicked!");
        selectSidebarText((Text) event.getSource());
        centerContentPane.getChildren().clear();
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            return;
        }
        Integer studentId = currentUser.getStudentId();
        if (studentId == null) {
            System.err.println("Current user is not a student.");
            return;
        }
        Pane statusPanel = StudentAttendanceStatusCenterPanel.createPanel(studentId);
        centerContentPane.getChildren().add(statusPanel);
    }

    private void onSubmitExcuseClicked(MouseEvent event) {
        System.out.println("Submit Excuse clicked!");
        selectSidebarText((Text) event.getSource());
        centerContentPane.getChildren().clear();
        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            return;
        }
        Integer studentId = currentUser.getStudentId();
        if (studentId == null) {
            System.err.println("Current user is not a student.");
            return;
        }
        Pane excusePanel = StudentSubmitExcuseCenterPanel.createPanel(studentId);
        centerContentPane.getChildren().add(excusePanel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}