package ticktocktrack.gui;

import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;
import ticktocktrack.database.DatabaseAttendance;
import ticktocktrack.database.DatabaseIndividualReport;
import ticktocktrack.logic.CourseInfo;
import ticktocktrack.logic.Session;
import ticktocktrack.logic.Student;


public class TeacherIndividualReportsCenterPanel {

    public static Pane createPanel(int teacherId) {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setStyle("-fx-background-color: #EEF5F9; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Background shadow 
        String shadowPath = TeacherIndividualReportsCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        // Title
        Text title = new Text("Individual Reports of Students");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 36));
        title.setFill(Color.web("#02383E"));
        title.setLayoutX(50);
        title.setLayoutY(70);
        
     // Student card container VBox
        VBox studentCardContainer = new VBox(10);
        studentCardContainer.setPadding(new Insets(20));
        studentCardContainer.setLayoutX(0); // this matches TranslateTransition targetX
        studentCardContainer.setLayoutY(100); // position below title
        studentCardContainer.setPrefWidth(400);
        studentCardContainer.setPrefHeight(900);
        studentCardContainer.setStyle("-fx-background-color: #EEF5F9;");
        studentCardContainer.setVisible(false);  // initially hidden
       


        // Right panel VBox
        VBox rightPanelVBox = new VBox(15);
        rightPanelVBox.setPadding(new Insets(20));
        rightPanelVBox.setPrefWidth(1100);
        rightPanelVBox.setPrefHeight(650);
        rightPanelVBox.setStyle(
        	    "-fx-background-color: white;" +
        	    "-fx-focus-color: transparent;" +
        	    "-fx-faint-focus-color: transparent;" +
        	    "-fx-border-color: transparent;" +
        	    "-fx-border-width: 0;" +
        	    "-fx-effect: none;"
        	);
        	


        rightPanelVBox.setAlignment(Pos.TOP_LEFT);
        rightPanelVBox.setFillWidth(false);
        rightPanelVBox.setFocusTraversable(false);

        
        // KEY FIX: Let children control width, disable fillWidth
        rightPanelVBox.setFillWidth(false);

        // ScrollPane wrapper
        ScrollPane rightPanelScrollPane = new ScrollPane(rightPanelVBox);
        rightPanelScrollPane.setPrefSize(1100,650);
        rightPanelScrollPane.setFitToWidth(false); // important to keep widths independent
        rightPanelScrollPane.setStyle(
        	    "-fx-background-color: white;" +
        	    "-fx-border-color: transparent;" +
        	    "-fx-border-width: 0;" +
        	    "-fx-focus-color: transparent;" +
        	    "-fx-faint-focus-color: transparent;"
        	);
        	

        // Start layoutX at 0
        rightPanelScrollPane.setLayoutX(0);
        rightPanelScrollPane.setLayoutY(0);
        rightPanelScrollPane.setFocusTraversable(false);

        Text filterTitle = new Text("Class Filters");
        filterTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        filterTitle.setFill(Color.web("#02383E"));
        rightPanelVBox.getChildren().add(filterTitle);

        final String[] SUBJECT_COLORS = {
            "#8B43BC", "#43BC8B", "#BC438B", "#438BBC"
        };

        CourseInfo[] courses = DatabaseAttendance.getCoursesForTeacher(teacherId);
        int colorIndex = 0;
        
        final VBox[] selectedCard = {null};
        final double SMALL_WIDTH = 310;
        final double LARGE_WIDTH = 500;
        final double SMALL_SCALE = 0.80;

        for (CourseInfo course : courses) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(20));

            // Set width to LARGE_WIDTH (500) for all cards
            card.setPrefWidth(LARGE_WIDTH);
            card.setMinWidth(LARGE_WIDTH);
            card.setMaxWidth(LARGE_WIDTH);

            // **All start at scaleX = 1.0 (full width)**
            card.setScaleX(1.0);

            String borderColor = SUBJECT_COLORS[colorIndex % SUBJECT_COLORS.length];

            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 1.5;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;"
            );
            


            Label courseName = new Label(course.getCourseName());
            courseName.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
            courseName.setTextFill(Color.web("#02383E"));

            Label section = new Label("Section: " + course.getSection());
            section.setFont(Font.font("Poppins", 12));
            section.setTextFill(Color.web("#555555"));

            Label program = new Label("Program: " + course.getProgram());
            program.setFont(Font.font("Poppins", 12));
            program.setTextFill(Color.web("#555555"));

            card.getChildren().addAll(courseName, section, program);

            DropShadow glow = new DropShadow(10, Color.web(borderColor));
            glow.setSpread(0.15);

            final VBox currentCard = card;
            final String currentColor = borderColor;

            card.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                if (selectedCard[0] != currentCard) {
                    currentCard.setStyle(
                        "-fx-background-color: " + currentColor + ";" +
                        "-fx-border-color: " + currentColor + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;"
                    );
                    courseName.setTextFill(Color.WHITE);
                    section.setTextFill(Color.WHITE);
                    program.setTextFill(Color.WHITE);
                }
                currentCard.setCursor(javafx.scene.Cursor.HAND);
            });

            card.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                if (selectedCard[0] != currentCard) {
                    currentCard.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-border-color: " + currentColor + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;"
                    );
                    courseName.setTextFill(Color.web("#02383E"));
                    section.setTextFill(Color.web("#555555"));
                    program.setTextFill(Color.web("#555555"));
                }
                currentCard.setCursor(javafx.scene.Cursor.DEFAULT);
            });

            card.setOnMouseClicked(e -> {
                System.out.println("Selected course: " + course.getCourseName());

                rightPanelVBox.setStyle("-fx-background-color: #EEF5F9;");

                // Reset all cards animation and styling
                for (javafx.scene.Node node : rightPanelVBox.getChildren()) {
                    if (node instanceof VBox && node != filterTitle) {
                        VBox c = (VBox) node;
                        boolean isSelected = (c == card);
                        double targetScale = isSelected ? 1.0 : SMALL_SCALE;

                        Timeline animation = new Timeline(
                            new KeyFrame(Duration.ZERO,
                                new KeyValue(c.scaleXProperty(), c.getScaleX())
                            ),
                            new KeyFrame(Duration.millis(150),
                                new KeyValue(c.scaleXProperty(), targetScale)
                            )
                        );
                        animation.play();

                        if (isSelected) {
                            c.setStyle(
                                "-fx-background-color: " + borderColor + ";" +
                                "-fx-border-color: " + borderColor + ";" +
                                "-fx-border-width: 1.5;" +
                                "-fx-background-radius: 5;" +
                                "-fx-border-radius: 5;"
                            );
                            // Update labels to white
                            for (javafx.scene.Node label : c.getChildren()) {
                                if (label instanceof Label) {
                                    ((Label) label).setTextFill(Color.WHITE);
                                }
                            }
                        } else {
                            int idx = rightPanelVBox.getChildren().indexOf(c) - 1; // -1 for filterTitle offset
                            String otherColor = SUBJECT_COLORS[idx % SUBJECT_COLORS.length];
                            c.setStyle(
                                "-fx-background-color: white;" +
                                "-fx-border-color: " + otherColor + ";" +
                                "-fx-border-width: 1.5;" +
                                "-fx-background-radius: 5;" +
                                "-fx-border-radius: 5;"
                            );
                            // Reset labels to default colors
                            for (javafx.scene.Node label : c.getChildren()) {
                                if (label instanceof Label) {
                                    Label lbl = (Label) label;
                                    if (lbl.getText().startsWith("Section") || lbl.getText().startsWith("Program")) {
                                        lbl.setTextFill(Color.web("#555555"));
                                    } else {
                                        lbl.setTextFill(Color.web("#02383E"));
                                    }
                                }
                            }
                        }
                    }
                }

                selectedCard[0] = card;

                TranslateTransition slideRight = new TranslateTransition(Duration.millis(300), rightPanelScrollPane);
                slideRight.setToX(700);
                slideRight.play();

                // Clear previous student cards BEFORE loading new ones
                studentCardContainer.getChildren().clear();

                List<Student> students = DatabaseIndividualReport.getStudentsForCurrentTeacherClass(
                	    course.getCourseName(),
                	    course.getSection(),
                	    course.getProgram()
                	);


                // (Use getBasicStudentsForTeacher or whichever method returns fresh student list for the teacher)

                for (Student s : students) {
                    VBox studentCard = new VBox(5);
                    studentCard.setPadding(new Insets(10));
                    studentCard.setStyle(
                        "-fx-background-color: #F9F9F9;" +
                        "-fx-border-color: #AAAAAA;" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 4;" +
                        "-fx-border-radius: 4;"
                    );
                    Label nameLabel = new Label(s.getLastName() + ", " + s.getFirstName() + " " + s.getMiddleName());
                    nameLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 13));
                    nameLabel.setTextFill(Color.web("#02383E"));

                    Label yearLabel = new Label("Year Level: " + s.getYearLevel());
                    yearLabel.setFont(Font.font("Poppins", 12));
                    yearLabel.setTextFill(Color.web("#555555"));

                    studentCard.getChildren().addAll(nameLabel, yearLabel);
                    
                    Student selectedStudent = s;  // capture for lambda
                   
                    studentCard.setOnMouseClicked(evt -> {
                        Pane overlay = CardIndividualReport.createStudentDetailOverlay(selectedStudent);
                        centerPanel.getChildren().add(overlay);  // This will overlay on top of everything inside the panel
                    });


                    studentCardContainer.getChildren().add(studentCard);
                }

                studentCardContainer.setVisible(true);
            });



            rightPanelVBox.getChildren().add(card);
            colorIndex++;
            card.setFocusTraversable(false);
        }
        
        studentCardContainer.setVisible(true);
        
        
        centerPanel.getChildren().addAll(title,studentCardContainer, rightPanelScrollPane, shadowView);
        return centerPanel;
    }
}
