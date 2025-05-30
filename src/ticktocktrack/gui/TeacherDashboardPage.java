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
 * JavaFX Application class representing the Teacher Dashboard UI.
 * Manages the main window, including notifications, user icon, and center content pane.
 */
public class TeacherDashboardPage extends Application {

    /** The primary stage (window) for the teacher dashboard. */
    private Stage teacherDashboardStage;

    /** The pane that holds the main center content of the dashboard. */
    private Pane centerContentPane;

    /** Pane that displays notifications specific to the teacher. */
    private TeacherNotificationPane notificationPane;

    /** ImageView for displaying the user's profile or icon image. */
    private ImageView userIcon;

    /**
     * Starts the Teacher Dashboard application.
     * Stores the primary stage reference and applies the application icon.
     * Checks if a user is logged in; if not, terminates the application.
     *
     * @param primaryStage the main stage (window) provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
    	 // Store the reference to the primaryStage
        this.teacherDashboardStage = primaryStage;
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
        notificationPane.getNotificationIconWrapper().setLayoutX(990);
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
        Text teacherText = new Text(currentUser.getUsername());
        teacherText.setFont(Font.font("Poppins Medium", 20));
        teacherText.setFill(Color.web("#02383E"));
        teacherText.setLayoutX(1080);
        teacherText.setLayoutY(65);
    
        
     // --- User Icon Setup ---
        String userIconPath = getClass().getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm();
        Image profileImage = UserIconUpdate.getCurrentUserProfileImage();
        Image userImage = profileImage != null ? profileImage : new Image(userIconPath);

        userIcon = new ImageView(userImage);
        userIcon.setFitWidth(67);
        userIcon.setFitHeight(67);
        userIcon.setLayoutX(1190);
        userIcon.setLayoutY(20);
        userIcon.setCursor(Cursor.HAND);

        // Apply circular clipping to the user icon
        Circle clip = new Circle(33.5, 33.5, 33.5);
        userIcon.setClip(clip);

        // --- User Icon Popup Handling ---
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

            for (Label label : new Label[]{profileLabel, logoutLabel}) {
                label.setStyle(normalStyle);
                label.setOnMouseEntered(e -> label.setStyle(hoverStyle));
                label.setOnMouseExited(e -> label.setStyle(normalStyle));
            }

            profileLabel.setOnMouseClicked(this::onProfileClicked);
            logoutLabel.setOnMouseClicked(this::onLogoutClicked);

            box.getChildren().addAll(profileLabel, logoutLabel);
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
        line1.setFitWidth(195);
        line1.setFitHeight(2);
        line1.setLayoutX(30);
        line1.setLayoutY(90);
     
     // Calendar Icon
        String calendarIconPath = getClass().getResource("/resources/Teacher_Dashboard/Teacher_calendar_icon.png").toExternalForm();
        ImageView calendarIcon = new ImageView(new Image(calendarIconPath));
        calendarIcon.setFitWidth(33); // Set width for the calendar icon
        calendarIcon.setFitHeight(33); // Set height for the calendar icon
        calendarIcon.setLayoutX(46); // x position
        calendarIcon.setLayoutY(130); // y position (adjust as needed)

       
     // Mark Attendance Text
        Text markAttendanceText = new Text("Mark Attendance");
        markAttendanceText.setFont(Font.font("Poppins", 15)); // Set font size to 18 and Poppins font
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
        line2.setFitWidth(195);  // width of line 2
        line2.setFitHeight(0);   // height of line 2 (0 for a thin line)
        line2.setLayoutX(30); // x position
        line2.setLayoutY(205); // y position
        
     // View Class List icon image
        String viewAllPath = getClass().getResource("/resources/Teacher_Dashboard/Teacher_view_all_icon.png").toExternalForm();
        ImageView viewAllIcon = new ImageView(new Image(viewAllPath));
        viewAllIcon.setFitWidth(35);
        viewAllIcon.setFitHeight(35);
        viewAllIcon.setLayoutX(46); // x position
        viewAllIcon.setLayoutY(240); // y position

     // View Class List Text
        Text viewClassListText = new Text("View Class List");
        viewClassListText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 15)); // Set font size to 18 and Poppins font (medium weight)
        viewClassListText.setFill(Color.web("#02383E")); // Set text color
        viewClassListText.setLayoutX(90); // x position
        viewClassListText.setLayoutY(265); // y position (adjust as needed)
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
        addCourseIcon.setFitWidth(31); // Set width
        addCourseIcon.setFitHeight(31); // Set height
        addCourseIcon.setLayoutX(47); // x position (adjust if needed)
        addCourseIcon.setLayoutY(320); // y position (adjust if needed)

        
     // Create "Add Course" Text
        Text addCourseText = new Text("Register Class");
        addCourseText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 15)); // Set font size and weight
        addCourseText.setFill(Color.web("#02383E")); // Set initial text color
        addCourseText.setLayoutX(90); // x position
        addCourseText.setLayoutY(339); // y position (adjust if needed)
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
            onRegisterClassClicked(e); // Handle the click (define this method)
        });
        
     // Line 3 image
        String line3Path = getClass().getResource("/resources/Line3.png").toExternalForm();
        ImageView line3 = new ImageView(new Image(line3Path));
        line3.setFitWidth(195);  // width of line 3
        line3.setFitHeight(0);   // height of line 3 (0 for a thin line)
        line3.setLayoutX(30);    // x position (adjust if needed)
        line3.setLayoutY(387);   // y position (adjust for spacing between sections)
        
     // Attendance icon image
        String attendanceIconPath = getClass().getResource("/resources/Teacher_Dashboard/Teacher_summary_icon.png").toExternalForm();
        ImageView attendanceIcon = new ImageView(new Image(attendanceIconPath));
        attendanceIcon.setFitWidth(35);   // width
        attendanceIcon.setFitHeight(35);  // height
        attendanceIcon.setLayoutX(46);    // x position
        attendanceIcon.setLayoutY(429);   // y position

     // Add "Attendance Summary" text
        Text attendanceSummaryText = new Text("Attendance Summary");
        attendanceSummaryText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 15)); // Set font size to 18 and Poppins font (medium weight)
        attendanceSummaryText.setFill(Color.web("#02383E")); // Set text color
        attendanceSummaryText.setLayoutX(93); // x position
        attendanceSummaryText.setLayoutY(440); // y position
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
        individualReportsIcon.setFitWidth(35);   // width
        individualReportsIcon.setFitHeight(35);  // height
        individualReportsIcon.setLayoutX(46);    // x position
        individualReportsIcon.setLayoutY(511);   // y position
        
     // Add "Individual Reports" text
        Text individualReportsText = new Text("Individual Reports");
        individualReportsText.setFont(Font.font("Poppins", FontWeight.MEDIUM, 15)); // Set font size to 18 and Poppins font (medium weight)
        individualReportsText.setFill(Color.web("#02383E")); // Set text color
        individualReportsText.setLayoutX(90); // x position
        individualReportsText.setLayoutY(534); // y position
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
        
       
        // Add all elements to topPanel
        topPanel.getChildren().addAll(logoView,  teacherText);
        
        sidePanel.getChildren().addAll( dashboardIcon, line1, calendarIcon, markAttendanceText, viewAllIcon, viewClassListText, line3, addCourseIcon, addCourseText,  line2, attendanceIcon, attendanceSummaryText, individualReportsIcon, individualReportsText);
        

     // Ensure the notificationPane icon is added last to overlayPane
        overlayPane.getChildren().addAll(topPanel, sidePanel, centerContentPane, userIcon, notificationPane.getNotificationIconWrapper());

    


        
        dashboardText.setOnMouseClicked(this::onDashboardClicked);
        
        
        
       
    }
    
    /**
     * Loads and sets the current user's profile image into the userIcon ImageView.
     * If the current user has no profile image, sets a default admin user icon.
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
     * Handles the profile icon click event.
     * Opens the UserProfile window and provides a callback to reload the user icon
     * when the user profile is updated.
     *
     * @param event The mouse event triggered by clicking the profile icon
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
     * Handles the logout action when the logout button or icon is clicked.
     * Logs out the current session and navigates back to the home page.
     *
     * @param event The mouse event triggered by clicking the logout control
     */
    private void onLogoutClicked(MouseEvent event) {
        Session.logoutAndGoHome(teacherDashboardStage);
    }

    /** 
     * Stores the currently selected sidebar Text control to manage selection highlights.
     */
    private Text selectedText; // Track the currently selected sidebar Text
    
    /**
     * Updates the sidebar to visually mark the newly selected Text element and
     * reset the previous selection's style.
     *
     * @param newSelectedText The Text element to be selected in the sidebar
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
     * Clears any sidebar text selection highlighting by resetting the color and
     * clearing the selectedText reference.
     */
    @SuppressWarnings("unused")
	private void clearSidebarHighlights() {
        if (selectedText != null) {
            selectedText.setFill(Color.web("#02383E")); // Reset the color of the previously selected text
            selectedText = null;
        }
    }

    /**
     * Handles the event when the Dashboard sidebar item is clicked.
     * Updates sidebar selection highlighting and loads the dashboard panel
     * into the center content pane.
     *
     * @param event The mouse event triggered by clicking the dashboard text
     */
    private void onDashboardClicked(MouseEvent event) {
        System.out.println("Dashboard clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected
        centerContentPane.getChildren().clear();
        // No need to pass currentUser explicitly
        Pane dashboardPanel = TeacherDashboardCenterPanel.createPanel();
        centerContentPane.getChildren().add(dashboardPanel);
    }

    /**
     * Handles the event when the Mark Attendance sidebar item is clicked.
     * Updates sidebar selection highlighting and loads the mark attendance panel
     * into the center content pane.
     *
     * @param event The mouse event triggered by clicking the mark attendance text
     */
    private void onMarkAttendanceClicked(MouseEvent event) {
        System.out.println("Mark Attendance clicked!");
        selectSidebarText((Text) event.getSource()); // If you want sidebar highlighting like in view class list

        centerContentPane.getChildren().clear();

        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            return;
        }

        Integer teacherId = currentUser.getTeacherId();
        if (teacherId == null) {
            System.err.println("Current user is not a teacher.");
            return;
        }

        // Assuming your createPanel method has a signature like createPanel(int teacherId, String optionalParam)
        // If the actual method expects course or section instead, adjust accordingly.
        Pane markAttendancePanel = TeacherMarkAttendanceCenterPanel.createPanel(null, null, teacherId);
        centerContentPane.getChildren().add(markAttendancePanel);
    }

    /**
     * Handles the event when the "View Class List" sidebar item is clicked.
     * Updates sidebar selection highlighting and loads the class list panel
     * for the logged-in teacher into the center content pane.
     *
     * @param event The mouse event triggered by clicking the "View Class List" text
     */
    private void onViewClassListClicked(MouseEvent event) {
        System.out.println("View Class List clicked!");
        selectSidebarText((Text) event.getSource());
        centerContentPane.getChildren().clear();

        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            return;
        }

        Integer teacherId = currentUser.getTeacherId();
        if (teacherId == null) {
            System.err.println("Current user is not a teacher.");
            return;
        }

        Pane dashboardPanel = TeacherViewClassListCenterPanel.createPanel(teacherId);
        centerContentPane.getChildren().add(dashboardPanel);
    }

    /**
     * Handles the event when the "Register Class" sidebar item is clicked.
     * Updates sidebar selection highlighting and prepares the center content pane
     * for loading the class registration panel.
     *
     * @param event The mouse event triggered by clicking the "Register Class" text
     */
    private void onRegisterClassClicked(MouseEvent event) {
        System.out.println("Register Class clicked!");
        selectSidebarText((Text) event.getSource());

        centerContentPane.getChildren().clear();

        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            // Optionally show an alert or redirect to login screen
            return;
        }

        Integer teacherId = currentUser.getTeacherId();
        if (teacherId == null) {
            System.err.println("Current user is not a teacher.");
            // Optionally handle error or show alert
            return;
        }

        Pane addCourseDialog = TeacherRegisterClassCenterPanel.createAddCourseDialog(centerContentPane, teacherId);
        centerContentPane.getChildren().add(addCourseDialog);
    }
    
    /**
     * Handles the event when the "Attendance Summary" sidebar item is clicked.
     * Highlights the selected sidebar text and loads the attendance summary panel
     * for the logged-in teacher into the center content pane.
     *
     * @param event The mouse event triggered by clicking the "Attendance Summary" text
     */
    private void onAttendanceSummaryClicked(MouseEvent event) {
        System.out.println("Attendance Summary clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected

        centerContentPane.getChildren().clear();

        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            // Optionally show an alert or redirect to login screen
            return;
        }

        Integer teacherId = currentUser.getTeacherId();
        if (teacherId == null) {
            System.err.println("Current user is not a teacher.");
            // Optionally handle error or show alert
            return;
        }

        Pane summaryPanel = TeacherAttendanceSummaryCenterPanel.createPanel(teacherId);
        centerContentPane.getChildren().add(summaryPanel);
    }

    /**
     * Handles the event when the "Individual Reports" sidebar item is clicked.
     * Highlights the selected sidebar text and prepares the center content pane
     * for loading individual report panels.
     *
     * @param event The mouse event triggered by clicking the "Individual Reports" text
     */
    private void onIndividualReportsClicked(MouseEvent event) {
        System.out.println("Individual Reports clicked!");
        selectSidebarText((Text) event.getSource()); // Set the clicked text as selected

        centerContentPane.getChildren().clear();

        UsersModel currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            // Optionally show an alert or redirect to login screen
            return;
        }

        Integer teacherId = currentUser.getTeacherId();
        if (teacherId == null) {
            System.err.println("Current user is not a teacher.");
            // Optionally handle error or show alert
            return;
        }

        Pane individualReportPanel = TeacherIndividualReportsCenterPanel.createPanel(teacherId);
        centerContentPane.getChildren().add(individualReportPanel);
    }


    /**
     * The main method serves as the entry point for the application.
     * It launches the JavaFX application by calling the {@code launch} method.
     *
     * @param args The command-line arguments passed to the program
     */
    public static void main(String[] args) {
        launch(args);
    }
}