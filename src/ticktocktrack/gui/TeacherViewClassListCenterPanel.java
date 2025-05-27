package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import ticktocktrack.database.DatabaseViewClassList;
import ticktocktrack.logic.CourseInfo;
import ticktocktrack.logic.ViewClassList;

import java.util.List;

/**
 * A panel that displays the list of classes associated with a teacher. 
 * Uses a vertical scrollbar to navigate through all classes.
 */
public class TeacherViewClassListCenterPanel {

    /** The central pane that contains the class list panel. */
    private static Pane centerPanel;

    /** The VBox container that holds rows of class cards. */
    private static VBox classListPanel;

    /** The ID of the teacher whose classes are displayed. */
    private static int teacherId;

    /** ScrollPane for vertical scrolling */
    private static ScrollPane scrollPane;

    /**
     * Creates the entire panel for viewing a teacher's class list.
     *
     * @param teacherId the ID of the teacher whose classes will be displayed.
     * @return a {@link Pane} containing the class list interface.
     */
    public static Pane createPanel(int teacherId) {
        TeacherViewClassListCenterPanel.teacherId = teacherId;
        centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        addShadowImage();
        addTitle();
        addClassListPanel();

        return centerPanel;
    }
    
    /**
     * Adds a shadow image to the center panel.
     * This enhances the visual design of the panel with a shadow effect.
     */
    private static void addShadowImage() {
        String shadowPath = TeacherViewClassListCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutY(-115);

        centerPanel.getChildren().add(shadowView);
    }

    /**
     * Adds a title to the center panel.
     * The title is styled and positioned to align with the panel's design.
     */
    private static void addTitle() {
        Text title = new Text("View Class List");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 36));
        title.setFill(Color.web("#02383E"));
        title.setLayoutX(50);
        title.setLayoutY(70);

        centerPanel.getChildren().add(title);
    }

    /**
     * Adds the class list panel to the center panel.
     * Initializes the {@link VBox} container for displaying the class list and
     * updates its content based on the teacher's ID.
     */
    private static void addClassListPanel() {
        classListPanel = new VBox(20); // 20px spacing between rows
        classListPanel.setPadding(new Insets(10, 0, 120, 10)); // Top, Right, Bottom, Left

        scrollPane = new ScrollPane(classListPanel);
        scrollPane.setLayoutX(50);
        scrollPane.setLayoutY(130);
        scrollPane.setPrefSize(1200, 600); // Adjust size as needed
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: WHITE; -fx-border-color: WHITE;");
        updateClassListPanel(teacherId);

        centerPanel.getChildren().add(scrollPane);
    }

    /**
     * Updates the class list panel with all the classes for the teacher,
     * displaying all courses in rows and columns inside a scrollable panel.
     *
     * @param teacherId the ID of the teacher whose classes are displayed
     */
    public static void updateClassListPanel(int teacherId) {
        classListPanel.getChildren().clear();

        List<CourseInfo> courses = DatabaseViewClassList.getClassesByTeacher(teacherId);

        final int COLUMNS_PER_ROW = 3;

        HBox row = new HBox(20);
        row.setAlignment(Pos.TOP_LEFT);
        int boxCount = 0;

        for (CourseInfo course : courses) {
            VBox courseBox = createCourseBox(course.courseName, course.section, course.program);
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
    }

    /**
     * Creates a visual representation of a course in the form of a VBox.
     * The box contains details about the course such as its name, section, and program.
     * 
     * @param courseName the name of the course
     * @param section the section associated with the course
     * @param program the program to which the course belongs
     * @return a VBox containing the course's details, styled for display
     */
    private static VBox createCourseBox(String courseName, String section, String program) {
        VBox courseBox = new VBox(10);
        courseBox.setStyle( "-fx-padding: 15px;" +
                "-fx-border-color: #8B43BC;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;" +       
                "-fx-background-color: #F6F6F6;" +
                "-fx-background-radius: 12px;"     
            );

        courseBox.setPrefSize(250, 120);

        Text courseNameText = new Text("Course: " + courseName);
        Text sectionText = new Text("Section: " + section);
        Text programText = new Text("Program: " + ViewClassList.mapProgramToShortName(program));

        Button editButton = createEditButton(courseName, section);
        Button deleteButton = createDeleteButton(courseName, section);
        Button viewStudentsButton = createViewStudentsButton(courseName, section, program, teacherId);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(editButton, deleteButton, viewStudentsButton);

        courseBox.getChildren().addAll(courseNameText, sectionText, programText, buttonBox);
        return courseBox;
    }

    /**
     * Creates a button that allows the teacher to view the list of students
     * enrolled in a specific course. The button triggers the display of
     * detailed student information for the given course, section, and program.
     * 
     * @param courseName the name of the course
     * @param section the section associated with the course
     * @param program the program to which the course belongs
     * @param teacherId the ID of the teacher viewing the student list
     * @return a Button configured to display the list of students when clicked
     */
    private static Button createViewStudentsButton(String courseName, String section, String program, int teacherId) {
        Button viewStudentsButton = new Button("View Students");
        viewStudentsButton.setStyle("-fx-background-color: #FFFFFF;" +  
                "-fx-text-fill: #009DDA;" +           
                "-fx-border-color: #009DDA;" +      
                "-fx-border-width: 1px;" +          
                "-fx-border-radius: 3px;" +         
                "-fx-background-radius: 3px;"       
            );

        viewStudentsButton.setOnAction(event -> openStudentList(courseName, section, program, teacherId));
        return viewStudentsButton;
    }

    /**
     * Opens a panel displaying the list of students for a specific course and section.
     * Initializes a new TeacherViewClassStudents panel and updates the center panel
     * to show the student list.
     *
     * @param courseName the name of the course
     * @param section the section associated with the course
     * @param program the program to which the course belongs
     * @param teacherId the ID of the teacher accessing the student list
     */
    private static void openStudentList(String courseName, String section, String program, int teacherId) {
        TeacherViewClassStudents panel = new TeacherViewClassStudents(courseName, section, program, teacherId);
        updateCenterPanel(panel.getView());
    }

    /**
     * Updates the main center panel with a new pane, replacing any existing content.
     *
     * @param newPanel the new pane to display in the center panel
     */
    public static void updateCenterPanel(Pane newPanel) {
        centerPanel.getChildren().clear();
        centerPanel.getChildren().add(newPanel);
    }

    /**
     * Creates a button to allow editing of the course and section details.
     * The button triggers an interface or dialog for modifying the course name and section.
     *
     * @param oldCourseName the current name of the course to be edited
     * @param oldSection the current section of the course to be edited
     * @return a Button configured to initiate the edit functionality
     */
    private static Button createEditButton(String oldCourseName, String oldSection) {
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #FFFFFF;" +  
                "-fx-text-fill: #01B80A;" +           
                "-fx-border-color: #01B80A;" +      
                "-fx-border-width: 1px;" +          
                "-fx-border-radius: 3px;" +         
                "-fx-background-radius: 3px;"       
            );

        editButton.setOnAction(event -> ViewClassList.handleEditCourse(oldCourseName, oldSection, teacherId));
        return editButton;
    }

    /**
     * Creates a delete button for removing a specific course and its associated section.
     * The button triggers a delete action when clicked, calling the handleDeleteCourse
     * method in the ViewClassList class.
     *
     * @param courseName the name of the course to be deleted
     * @param section the section of the course to be deleted
     * @return a Button styled and configured to delete the specified course
     */
    private static Button createDeleteButton(String courseName, String section) {
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #FFFFFF;" +  
                "-fx-text-fill: #DD5F61;" +           
                "-fx-border-color: #DD5F61;" +      
                "-fx-border-width: 1px;" +          
                "-fx-border-radius: 3px;" +         
                "-fx-background-radius: 3px;"
              );
        deleteButton.setOnAction(event -> ViewClassList.handleDeleteCourse(courseName, section, teacherId));
        return deleteButton;
    }
}
