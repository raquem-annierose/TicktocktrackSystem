package ticktocktrack.gui;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.util.Duration;

import ticktocktrack.database.DatabaseStatusAttendance;
import ticktocktrack.logic.AttendanceStats;

import java.util.List;

/**
 * A UI component that displays the attendance status for a student.
 * It shows subject-specific attendance cards and allows selection highlighting.
 */
public class StudentAttendanceStatusCenterPanel {

    /**
     * The icon image URL used for subjects.
     */
    private static final String SUBJECT_ICON = StudentAttendanceStatusCenterPanel.class
            .getResource("/resources/Subject_icon.png").toExternalForm();

    /**
     * The currently selected attendance card (highlighted).
     */
    private static HBox selectedCard = null;

    /**
     * Container holding all subject attendance cards.
     */
    private static VBox cardsContainer;

    /**
     * List of all subjects for the student.
     */
    private static List<String> allSubjects;

    /**
     * Creates and returns a pane containing the attendance status panel for the given student.
     * The panel shows subject cards with attendance information and allows interaction.
     *
     * @param studentId the unique identifier of the student whose attendance status is displayed
     * @return a Pane representing the student attendance status UI
     */
    public static Pane createPanel(int studentId) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1100, 630);
        root.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #E0E4E8; -fx-border-width: 1.5px; -fx-background-radius: 12; -fx-border-radius: 12;");

        root.setTop(buildHeader());

        allSubjects = DatabaseStatusAttendance.getEnrolledStudentSubjects(studentId);

        cardsContainer = new VBox(25);
        cardsContainer.setPadding(new Insets(20, 0, 20, 0));  
        cardsContainer.setAlignment(Pos.TOP_LEFT);

        updateCards(studentId);

        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background:transparent; -fx-background-color:transparent;");

        StackPane content = new StackPane(scrollPane);
        content.setPadding(new Insets(20, 20, 20, 20)); // or even (15, 15, 15, 5) for tighter left spacing

        root.setCenter(content);

        return root;
    }

    /**
     * Builds the header section of the attendance status panel.
     * This header includes a background with shadow effect and a title.
     *
     * @return a Pane containing the styled header UI components
     */
    private static Pane buildHeader() {
        HBox headerBox = new HBox();
        headerBox.setPrefHeight(80);
        headerBox.setPadding(new Insets(10, 30, 10, 30));
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setStyle("-fx-background-color: #02383E; -fx-background-radius: 12 12 0 0;");

        ImageView shadow = new ImageView(new Image(StudentAttendanceStatusCenterPanel.class
                .getResource("/resources/SHADOW.png").toExternalForm()));
        shadow.setFitWidth(1300);
        shadow.setFitHeight(100);
        shadow.setOpacity(0.15);
        shadow.setTranslateY(-40);
        shadow.setMouseTransparent(true);

        Text title = new Text("Attendance Status");
        title.setFont(Font.font("Poppins", FontWeight.SEMI_BOLD, 28));
        title.setFill(Color.WHITE);
        title.setEffect(new javafx.scene.effect.DropShadow(2, Color.color(0, 0, 0, 0.25)));

        headerBox.getChildren().add(title);

        StackPane headerPane = new StackPane(shadow, headerBox);
        return headerPane;
    }

    /**
     * Updates the attendance subject cards displayed in the panel
     * by clearing existing cards and loading new data for the specified student.
     *
     * @param studentId the ID of the student whose attendance cards are to be updated
     */
    private static void updateCards(int studentId) {
        cardsContainer.getChildren().clear();

        for (String subject : allSubjects) {
            HBox card = buildSubjectCard(subject, studentId);
            HBox wrapper = new HBox(card);
            wrapper.setAlignment(Pos.TOP_LEFT);
            wrapper.setPrefWidth(900);
            wrapper.setPadding(new Insets(0, 0, 0, 70)); // 👈 Add this line to shift the card right
            cardsContainer.getChildren().add(wrapper);

        }

        // Automatically expand the first card if it exists
        if (!cardsContainer.getChildren().isEmpty() && cardsContainer.getChildren().get(0) instanceof HBox firstWrapper) {
            if (!firstWrapper.getChildren().isEmpty() && firstWrapper.getChildren().get(0) instanceof HBox firstCard) {
                setCardExpanded(firstCard);
                selectedCard = firstCard;
            } else {
                System.out.println("First child of firstWrapper is not an HBox or is missing.");
            }
        } else {
            System.out.println("cardsContainer is empty or first child is not an HBox.");
        }
    }

    /**
     * Builds a collapsible subject card UI component displaying attendance statistics
     * for the given student and subject. The card expands and collapses on click to
     * show or hide detailed information.
     *
     * @param subject the name of the subject to display
     * @param studentId the ID of the student whose attendance stats will be shown
     * @return an HBox representing the subject card with attendance details
     */
    private static HBox buildSubjectCard(String subject, int studentId) {
        final double COLLAPSED_HEIGHT = 150;
        final double EXPANDED_HEIGHT = 280;

        AttendanceStats stats = DatabaseStatusAttendance.getAttendanceStats(studentId, subject);

        ImageView icon = new ImageView(new Image(SUBJECT_ICON));
        icon.setFitWidth(48);
        icon.setFitHeight(48);
        icon.setSmooth(true);

        Text subjectName = new Text(subject);
        subjectName.setFont(Font.font("Poppins", FontWeight.SEMI_BOLD, 20));
        subjectName.setFill(Color.web("#024053"));

        VBox summary = new VBox(8);
        summary.setAlignment(Pos.TOP_LEFT);

        String professorName = DatabaseStatusAttendance.getProfessorNameBySubject(subject);
        Text professor = new Text("Professor: " + professorName);
        Text summaryLabel = new Text("Attendance Summary");
        Text present = new Text("✔ Present: " + stats.present);
        Text absent = new Text("✖ Absent: " + stats.absent);
        Text late = new Text("🕓 Late: " + stats.late);
        Text excused = new Text("📝 Excused: " + stats.excused);

        for (Text t : List.of(professor, summaryLabel, present, absent, late, excused)) {
            t.setFont(Font.font("Poppins", 14));
            t.setFill(Color.web("#5A6A72"));
        }
        summaryLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 15));
        summaryLabel.setFill(Color.web("#236A6B"));

        Text status = new Text();
        status.setFont(Font.font("Poppins", FontWeight.BOLD, 15));
        if (stats.absent <= 2) {
            status.setText("Attendance Status: Good");
            status.setFill(Color.web("#2C6E49")); // dark green
        } else if (stats.absent == 3) {
            status.setText("Attendance Status: Warning");
            status.setFill(Color.web("#D9780E")); // amber
        } else {
            status.setText("Attendance Status: Critical");
            status.setFill(Color.web("#B43425")); // red
        }

        summary.getChildren().addAll(professor, summaryLabel, present, absent, late, excused, status);
        summary.setVisible(false);
        summary.setManaged(false);
        summary.setId("details-box");

        VBox centerContent = new VBox(10, subjectName, summary);
        centerContent.setAlignment(Pos.TOP_LEFT);

        PieChart pie = new PieChart();
        pie.setLegendVisible(false);
        pie.setLabelsVisible(false);
        pie.setStartAngle(90);
        pie.setPrefSize(100, 100);
        pie.getData().addAll(
                new PieChart.Data("Present", stats.present),
                new PieChart.Data("Absent", stats.absent),
                new PieChart.Data("Late", stats.late),
                new PieChart.Data("Excused", stats.excused)
        );

        for (PieChart.Data data : pie.getData()) {
            String color;
            switch (data.getName()) {
                case "Present": color = "#4A9CCB"; break;
                case "Absent": color = "#D9534F"; break;
                case "Late": color = "#F0AD4E"; break;
                case "Excused": color = "#5BC0DE"; break;
                default: color = "#888888"; break;
            }
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        }

        VBox rightContent = new VBox(pie);
        rightContent.setAlignment(Pos.CENTER_RIGHT);
        rightContent.setMinWidth(120);

        HBox card = new HBox(25, icon, centerContent, rightContent);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefHeight(COLLAPSED_HEIGHT);
        card.setMaxWidth(880);
        card.setMinWidth(860);
        card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-width: 1.5;
            -fx-border-color: #D9E2E7;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6,0,0,3);
            """);

        card.setCursor(javafx.scene.Cursor.HAND);

        card.setOnMouseClicked(e -> {
            if (selectedCard == card) {
                setCardCollapsed(card);
                selectedCard = null;
            } else {
                if (selectedCard != null) {
                    setCardCollapsed(selectedCard);
                }
                setCardExpanded(card);
                selectedCard = card;
            }
        });

        card.setUserData(summary);

        return card;
    }

    /**
     * Expands the given subject card by making its detailed summary visible and
     * animating the card's height and fade-in effect.
     *
     * @param card the HBox representing the subject card to expand
     */
    private static void setCardExpanded(HBox card) {
        VBox summary = (VBox) card.getUserData();
        summary.setVisible(true);
        summary.setManaged(true);

        Timeline expandTimeline = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(card.prefHeightProperty(), 280, Interpolator.EASE_BOTH))
        );
        expandTimeline.play();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), summary);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    /**
     * Collapses the given subject card by hiding its detailed summary and
     * animating the card's height and fade-out effect.
     *
     * @param card the HBox representing the subject card to collapse
     */
    private static void setCardCollapsed(HBox card) {
        VBox summary = (VBox) card.getUserData();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), summary);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(ev -> {
            summary.setVisible(false);
            summary.setManaged(false);
        });
        fadeOut.play();

        Timeline collapseTimeline = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(card.prefHeightProperty(), 150, Interpolator.EASE_BOTH))
        );
        collapseTimeline.play();
    }
}
