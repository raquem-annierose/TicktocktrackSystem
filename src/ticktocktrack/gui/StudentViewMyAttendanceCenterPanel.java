package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ticktocktrack.database.DatabaseStudentViewMyAttendance;


import java.util.List;


/**
 * Panel for displaying a student's attendance overview with pagination and
 * visually distinct subject cards.
 * <p>
 * Supports cycling through colors for the top bar of each subject card.
 * Displays a limited number of cards per page with navigation controls.
 * </p>
 */
public class StudentViewMyAttendanceCenterPanel {

    /** Number of attendance cards to display per page. */
    private static final int CARDS_PER_PAGE = 9;

    /** The current page index for pagination (zero-based). */
    private static int currentPage = 0;

    /**
     * Array of colors used for the top bars of subject cards,
     * cycling through for visual variety.
     */
    private static final Color[] TOP_BAR_COLORS = new Color[]{
        Color.web("#3e7d7d"), // Teal
        Color.web("#f8e67c"), // Light yellow
        Color.web("#188038"), // Green
        Color.web("#2DCFDF"), // Blue
        Color.web("#6b3fa0"), // Purple
        Color.web("#f4511e"), // Orange
        Color.web("#f5eaff"), // Lavender
        Color.web("#6d4c41")  // Brown
    };

    /**
     * Creates and returns the main panel containing the student's attendance cards.
     * Initializes the first page of attendance records retrieved from the database.
     *
     * @return A Pane containing the attendance overview UI.
     */
    public static Pane createPanel() {
        List<String> classes = DatabaseStudentViewMyAttendance.getStudentClassesWithTeachers();

        Pane root = new Pane();
        root.setPrefSize(1300, 750);
        root.setStyle("-fx-background-color: white;");

        // Title Label
        Label titleLabel = new Label("Classes Enrolled:");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#8B43BC"));
        titleLabel.setPadding(new Insets(20, 0, 10, 20));

        // HBox for horizontal scrolling cards
        HBox hbox = new HBox(40);
        hbox.setPrefHeight(600);
        hbox.setPadding(new Insets(5));
        hbox.setAlignment(Pos.TOP_LEFT);

        for (int i = 0; i < classes.size(); i++) {
            String classInfo = classes.get(i);
            Region card = createClassCard(classInfo, i);

            // Add top margin (15 px)
            HBox.setMargin(card, new Insets(15, 0, 0, 0));

            card.setOnMouseClicked(e -> {
                // Remove existing overlay if any
                root.getChildren().removeIf(node -> "overlayBackground".equals(node.getId()) || "overlayPanel".equals(node.getId()));

                // Create dim background to block interaction and darken background
                Region backgroundDim = new Region();
                backgroundDim.setId("overlayBackground");
                backgroundDim.setPrefSize(root.getPrefWidth(), root.getPrefHeight());
                backgroundDim.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
                backgroundDim.setLayoutX(0);
                backgroundDim.setLayoutY(0);
                backgroundDim.setOnMouseClicked(ev -> {
                    root.getChildren().removeIf(node -> "overlayBackground".equals(node.getId()) || "overlayPanel".equals(node.getId()));
                });

                // Extract course name from classInfo (before first ' | ')
                String courseName = classInfo.split("\\|")[0].trim();

                // Get attendance status for today
                String attendanceStatus = DatabaseStudentViewMyAttendance.getTodayAttendanceStatusForCourse(courseName);

                // Get attendance history for the course
                List<AttendanceStatusPanel.AttendanceRecord> history = DatabaseStudentViewMyAttendance.getAttendanceHistoryForCourse(courseName);

                // Create new overlay panel for clicked class with attendance status and history
                Pane overlayPanel = AttendanceStatusPanel.createStatusPanel(courseName, attendanceStatus, history, () -> {
                    root.getChildren().removeIf(node -> "overlayBackground".equals(node.getId()) || "overlayPanel".equals(node.getId()));
                });

                overlayPanel.setId("overlayPanel");
                overlayPanel.setStyle(overlayPanel.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 15, 0, 0, 0);");

                root.getChildren().addAll(backgroundDim, overlayPanel);
            });




            hbox.getChildren().add(card);
        }

        ScrollPane scrollPane = new ScrollPane(hbox);
        scrollPane.setPrefSize(1100, 600);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: white;");
        scrollPane.setPadding(new Insets(0, 0, 0, 20));

        VBox vbox = new VBox();
        vbox.getChildren().addAll(titleLabel, scrollPane);
        vbox.setLayoutX(0);
        vbox.setLayoutY(30);

        // Shadow image at the top
        String shadowPath = StudentViewMyAttendanceCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        root.getChildren().addAll(shadowView, vbox);

        return root;
    }

    /**
     * Creates a visual card representing a class and its teacher's information.
     * <p>
     * The card includes attendance details for the class and is styled with
     * a color based on its position on the current page to visually differentiate it.
     * </p>
     *
     * @param classInfoWithTeacher the combined class name and teacher information to display
     * @param indexOnPage the zero-based index of the card on the current page,
     *                    used for selecting the card's color styling
     * @return a Region node representing the styled class attendance card
     */
    private static Region createClassCard(String classInfoWithTeacher, int indexOnPage) {
        String[] parts = classInfoWithTeacher.split("\\|");
        String courseName = parts[0].trim();
        String teacherName = (parts.length > 1) ? parts[1].trim() : "";
        String profilePath = (parts.length > 2) ? parts[2].trim() : "";

        VBox card = new VBox();
        card.setPrefSize(280, 140);
        card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 12px; " +
                "-fx-border-radius: 12px; " +
                "-fx-border-color: #ddd; " +
                "-fx-border-width: 1;"
        );
        card.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.12)));
        card.setCursor(Cursor.HAND);

        // Colored Top Bar
        Color barColor = TOP_BAR_COLORS[indexOnPage % TOP_BAR_COLORS.length];
        String barColorHex = toHexString(barColor);

        Region topBar = new Region();
        topBar.setPrefHeight(40);
        topBar.setMaxWidth(Double.MAX_VALUE);
        topBar.setStyle("-fx-background-color: " + barColorHex + "; -fx-background-radius: 12 12 0 0;");

        // Main content with adjusted spacing and padding
        VBox content = new VBox(10);
        content.setPadding(new Insets(15, 20, 15, 20));
        content.setAlignment(Pos.TOP_LEFT);

        Label classLabel = new Label(courseName);
        classLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        classLabel.setTextFill(Color.web("#202124"));

        // Load profile image or default icon
        Image userImage;
        try {
            if (!profilePath.isEmpty()) {
                // Assuming profilePath is a valid URL or a file path accessible by your app
                userImage = new Image(profilePath, 70, 70, true, true);
                if (userImage.isError()) {
                    throw new IllegalArgumentException("Image loading error");
                }
            } else {
                throw new IllegalArgumentException("Empty profile path");
            }
        } catch (Exception e) {
            // fallback to default icon if loading fails
            userImage = new Image(StudentViewMyAttendanceCenterPanel.class.getResource("/resources/Admin_Dashboard/Admin_user_icon.png").toExternalForm(), 70, 70, true, true);
        }

        ImageView userIcon = new ImageView(userImage);
        userIcon.setFitWidth(70);
        userIcon.setFitHeight(70);
        userIcon.setPreserveRatio(true);
        userIcon.setSmooth(true);
        
        Circle clip = new Circle(35, 35, 35);  // centerX=35, centerY=35, radius=35 (half of 70)
        userIcon.setClip(clip);
        
        // Container VBox to center icon and teacher label vertically stacked
        VBox teacherBox = new VBox(4); // spacing between icon and label
        teacherBox.setAlignment(Pos.CENTER);
        Label teacherLabel = new Label(teacherName);
        teacherLabel.setFont(Font.font("System", 14));
        teacherLabel.setTextFill(Color.web("#5f6368"));
        teacherBox.getChildren().addAll(userIcon, teacherLabel);

        content.getChildren().addAll(classLabel, teacherBox);

        card.getChildren().addAll(topBar, content);

        // Hover effects unchanged
        card.setOnMouseEntered(e -> {
            card.setEffect(new DropShadow(12, Color.rgb(26, 115, 232, 0.3)));
            card.setScaleX(1.03);
            card.setScaleY(1.03);
        });

        card.setOnMouseExited(e -> {
            card.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.12)));
            card.setScaleX(1);
            card.setScaleY(1);
        });

        card.setOnMouseClicked(e -> System.out.println("Selected class: " + courseName));

        return card;
    }

    /**
     * Converts a JavaFX Color object to its hexadecimal string representation.
     *
     * @param color the Color object to convert
     * @return the hex string representation of the color in the format "#RRGGBB"
     */
    private static String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }
}
