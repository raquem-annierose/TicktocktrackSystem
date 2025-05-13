package ticktocktrack.gui;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import ticktocktrack.logic.ViewClassList;

import java.util.List;

public class TeacherViewClassListCenterPanel {

    private static Pane centerPanel;
    private static VBox classListPanel;
    private static int currentPage = 0;
    private static final int ROWS_PER_PAGE = 3;
    private static final int COLUMNS_PER_ROW = 3;

    public static Pane createPanel() {
        centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        addShadowImage();
        addTitle();
        addClassListPanel();

        return centerPanel;
    }

    private static void addShadowImage() {
        String shadowPath = TeacherViewClassListCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutY(-115);

        centerPanel.getChildren().add(shadowView);
    }

    private static void addTitle() {
        Text title = new Text("View Class List");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 36));
        title.setFill(Color.web("#02383E"));
        title.setLayoutX(50);
        title.setLayoutY(70);

        centerPanel.getChildren().add(title);
    }

    private static void addClassListPanel() {
        classListPanel = new VBox(20);
        classListPanel.setLayoutX(50);
        classListPanel.setLayoutY(150);

        updateClassListPanel();

        centerPanel.getChildren().add(classListPanel);
    }

    public static void updateClassListPanel() {
        classListPanel.getChildren().clear();

        List<String[]> courses = ViewClassList.getCourses();
        int startIndex = currentPage * (ROWS_PER_PAGE * COLUMNS_PER_ROW);
        int endIndex = Math.min(startIndex + (ROWS_PER_PAGE * COLUMNS_PER_ROW), courses.size());

        HBox row = new HBox(20);
        row.setAlignment(Pos.TOP_LEFT);
        int boxCount = 0;

        for (int i = startIndex; i < endIndex; i++) {
            String[] course = courses.get(i);
            VBox courseBox = createCourseBox(course[0], course[1]);
            row.getChildren().add(courseBox);
            boxCount++;

            if (boxCount == COLUMNS_PER_ROW) {
                classListPanel.getChildren().add(row);
                row = new HBox(20);
                row.setAlignment(Pos.TOP_LEFT);
                boxCount = 0;
            }
        }

        if (boxCount > 0) {
            classListPanel.getChildren().add(row);
        }

        classListPanel.getChildren().add(createNavButtons(courses.size()));
    }

    private static VBox createCourseBox(String courseName, String section) {
        VBox courseBox = new VBox(10);
        courseBox.setStyle("-fx-padding: 15px; -fx-border-color: #ccc; -fx-border-width: 2px; -fx-background-color: #f9f9f9;");
        courseBox.setPrefSize(250, 120);

        Text courseNameText = new Text("Course: " + courseName);
        Text sectionText = new Text("Section: " + section);

        Button editButton = createEditButton(courseName, section);
        Button deleteButton = createDeleteButton(courseName, section);
        Button viewStudentsButton = createViewStudentsButton(courseName);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(editButton, deleteButton, viewStudentsButton);

        courseBox.getChildren().addAll(courseNameText, sectionText, buttonBox);
        return courseBox;
    }

    private static Button createViewStudentsButton(String courseName) {
        Button viewStudentsButton = new Button("View Students");
        viewStudentsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        viewStudentsButton.setOnAction(event -> openStudentList(courseName));
        return viewStudentsButton;
    }

    private static void openStudentList(String courseName) {
        TeacherViewClassStudents.showStudentList(courseName);
    }
    
    public static void updateCenterPanel(Pane newPanel) {
        centerPanel.getChildren().clear();
        centerPanel.getChildren().add(newPanel);
    }

    private static Button createEditButton(String oldCourseName, String oldSection) {
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        editButton.setOnAction(event -> ViewClassList.handleEditCourse(oldCourseName, oldSection));
        return editButton;
    }

    private static Button createDeleteButton(String courseName, String section) {
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> ViewClassList.handleDeleteCourse(courseName, section));
        return deleteButton;
    }

    private static HBox createNavButtons(int totalCourses) {
        HBox navButtons = new HBox(10);
        navButtons.setAlignment(Pos.CENTER);

        if (currentPage > 0) {
            Button backButton = new Button("Back");
            backButton.setOnAction(event -> ViewClassList.goToPreviousPage());
            navButtons.getChildren().add(backButton);
        }

        int totalPages = (int) Math.ceil((double) totalCourses / (ROWS_PER_PAGE * COLUMNS_PER_ROW));
        if (currentPage < totalPages - 1) {
            Button nextButton = new Button("Next");
            nextButton.setOnAction(event -> ViewClassList.goToNextPage());
            navButtons.getChildren().add(nextButton);
        }

        return navButtons;
    }

    // ==== Para ma-access ng logic ====
    public static void incrementPage() {
        currentPage++;
        updateClassListPanel();
    }

    public static void decrementPage() {
        currentPage--;
        updateClassListPanel();
    }
}
