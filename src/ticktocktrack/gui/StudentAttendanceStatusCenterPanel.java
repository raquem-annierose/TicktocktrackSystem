package ticktocktrack.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import ticktocktrack.database.DatabaseStatusAttendance;
import ticktocktrack.logic.AttendanceStats;

import java.util.List;

public class StudentAttendanceStatusCenterPanel {

    private static final String SUBJECT_ICON = StudentAttendanceStatusCenterPanel.class
            .getResource("/resources/Subject_icon.png").toExternalForm();

    private static final double BASE_HUE = 210.0;
    private static final double HUE_RANGE = 15.0;
    private static final double SATURATION = 0.3;
    private static final double BRIGHTNESS = 0.9;

    private static HBox selectedCard = null;

    private static VBox cardsContainer;
    private static List<String> allSubjects;
    private static int currentPage = 0;
    private static final int CARDS_PER_PAGE = 3;

    public static Pane createPanel(int studentId) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1700, 350);
        root.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        root.setTop(buildHeader());

        allSubjects = DatabaseStatusAttendance.getEnrolledStudentSubjects(studentId);

        VBox verticalLayout = new VBox(10);
        verticalLayout.setPadding(new Insets(10));
        verticalLayout.setAlignment(Pos.TOP_CENTER);

        cardsContainer = new VBox(15);
        cardsContainer.setAlignment(Pos.TOP_CENTER);

        updateCards(studentId);

        HBox paginationControls = buildPaginationControls(studentId);

        verticalLayout.getChildren().addAll(paginationControls, cardsContainer);

        StackPane content = new StackPane(verticalLayout);
        content.setPadding(new Insets(15, 15, 15, 15));
        root.setCenter(content);
        return root;
    }

    private static Pane buildHeader() {
        Pane headerPane = new Pane();
        headerPane.setPrefHeight(70);

        ImageView shadow = new ImageView(new Image(StudentAttendanceStatusCenterPanel.class
                .getResource("/resources/SHADOW.png").toExternalForm()));
        shadow.setFitWidth(1700);
        shadow.setFitHeight(120);
        shadow.setLayoutY(-60);

        Text title = new Text("Attendance Status");
        title.setFont(Font.font("Poppins", FontWeight.MEDIUM, 22));
        title.setFill(Color.web("#02383E"));
        title.relocate(90, 25);

        headerPane.getChildren().addAll(shadow, title);
        return headerPane;
    }

    private static HBox buildPaginationControls(int studentId) {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);

        javafx.scene.control.Button prevButton = new javafx.scene.control.Button("Previous");
        javafx.scene.control.Button nextButton = new javafx.scene.control.Button("Next");

        prevButton.setPrefHeight(35);
        nextButton.setPrefHeight(35);
        prevButton.setPrefWidth(90);
        nextButton.setPrefWidth(90);
        prevButton.setStyle("-fx-font-size: 14px;");
        nextButton.setStyle("-fx-font-size: 14px;");

        prevButton.setOnAction(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateCards(studentId);
            }
        });

        nextButton.setOnAction(e -> {
            if ((currentPage + 1) * CARDS_PER_PAGE < allSubjects.size()) {
                currentPage++;
                updateCards(studentId);
            }
        });

        controls.getChildren().addAll(prevButton, nextButton);
        return controls;
    }

    private static void updateCards(int studentId) {
        cardsContainer.getChildren().clear();

        int start = currentPage * CARDS_PER_PAGE;
        int end = Math.min(start + CARDS_PER_PAGE, allSubjects.size());
        List<String> subjectsToShow = allSubjects.subList(start, end);

        for (String subject : subjectsToShow) {
            HBox card = buildSubjectCard(subject, studentId);
            cardsContainer.getChildren().add(card);
        }

        if (!cardsContainer.getChildren().isEmpty()) {
            setCardExpanded((HBox) cardsContainer.getChildren().get(0));
        }
    }

    private static HBox buildSubjectCard(String subject, int studentId) {
        final double COLLAPSED_WIDTH = 800;  // made smaller to fit horizontally
        final double COLLAPSED_HEIGHT = 140; // taller cards
        final double EXPANDED_HEIGHT = 270;  // slightly taller expanded

        AttendanceStats stats = DatabaseStatusAttendance.getAttendanceStats(studentId, subject);

        ImageView icon = new ImageView(new Image(SUBJECT_ICON));
        icon.setFitWidth(50);
        icon.setFitHeight(50);

        Text subjectName = new Text(subject);
        subjectName.setFont(Font.font("Poppins", FontWeight.MEDIUM, 18));
        subjectName.setFill(Color.web("#02383E"));

        VBox summary = new VBox(6);
        summary.setAlignment(Pos.CENTER_LEFT);

        String professorName = DatabaseStatusAttendance.getProfessorNameBySubject(subject);
        Text professor = new Text("Professor: " + professorName);
        Text summaryLabel = new Text("Attendance Summary:");
        Text present = new Text("✔ Present: " + stats.present);
        Text absent = new Text("✖ Absent: " + stats.absent);
        Text late = new Text("🕓 Late: " + stats.late);
        Text excused = new Text("📝 Excused: " + stats.excused);

        for (Text t : List.of(professor, summaryLabel, present, absent, late, excused)) {
            t.setFont(Font.font("Poppins", 13));
            t.setFill(Color.web("#555555"));
        }

        Text status = new Text();
        status.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        if (stats.absent == 0) {
            status.setText("Attendance Status: Good");
            status.setFill(Color.GREEN);
        } else if (stats.absent <= 2) {
            status.setText("Attendance Status: Warning");
            status.setFill(Color.ORANGE);
        } else {
            status.setText("Attendance Status: Critical");
            status.setFill(Color.RED);
        }

        summary.getChildren().addAll(professor, summaryLabel, present, absent, late, excused, status);
        summary.setVisible(false);
        summary.setManaged(false);
        summary.setId("details-box");

        VBox centerContent = new VBox(10, subjectName, summary);
        centerContent.setAlignment(Pos.CENTER_LEFT);

        PieChart pie = new PieChart();
        pie.setLegendVisible(false);
        pie.setLabelsVisible(false);
        pie.setStartAngle(90);
        pie.setPrefSize(90, 90);
        pie.getData().addAll(
                new PieChart.Data("Present", stats.present),
                new PieChart.Data("Absent", stats.absent),
                new PieChart.Data("Late", stats.late),
                new PieChart.Data("Excused", stats.excused)
        );
        pie.setVisible(false);
        pie.setManaged(false);
        pie.setId("pie-chart");

        HBox card = new HBox(20, icon, centerContent, pie);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefSize(COLLAPSED_WIDTH, COLLAPSED_HEIGHT);
        card.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

        animateBorderColor(card);

        card.setOnMouseClicked(e -> {
            card.setPrefHeight(EXPANDED_HEIGHT);
            setCardExpanded(card);
        });

        return card;
    }

    private static void setCardExpanded(HBox card) {
        if (selectedCard != null && selectedCard != card) {
            selectedCard.setPrefHeight(140);
            VBox oldContent = (VBox) selectedCard.getChildren().get(1);
            Node oldDetails = oldContent.lookup("#details-box");
            if (oldDetails != null) {
                oldDetails.setVisible(false);
                oldDetails.setManaged(false);
            }
            Node oldPie = selectedCard.lookup("#pie-chart");
            if (oldPie != null) {
                oldPie.setVisible(false);
                oldPie.setManaged(false);
            }
        }

        selectedCard = card;
        VBox newContent = (VBox) selectedCard.getChildren().get(1);
        Node newDetails = newContent.lookup("#details-box");
        if (newDetails != null) {
            newDetails.setVisible(true);
            newDetails.setManaged(true);
        }
        Node newPie = selectedCard.lookup("#pie-chart");
        if (newPie != null) {
            newPie.setVisible(true);
            newPie.setManaged(true);
        }
    }

    private static void animateBorderColor(HBox card) {
        final double[] hue = {BASE_HUE - HUE_RANGE};
        final boolean[] increasing = {true};

        Timeline colorAnimation = new Timeline(
                new KeyFrame(Duration.millis(70), event -> {
                    if (increasing[0]) {
                        hue[0] += 1;
                        if (hue[0] >= BASE_HUE + HUE_RANGE) increasing[0] = false;
                    } else {
                        hue[0] -= 1;
                        if (hue[0] <= BASE_HUE - HUE_RANGE) increasing[0] = true;
                    }

                    Color animatedColor = Color.hsb(hue[0], SATURATION, BRIGHTNESS);
                    String rgb = String.format("#%02X%02X%02X",
                            (int) (animatedColor.getRed() * 255),
                            (int) (animatedColor.getGreen() * 255),
                            (int) (animatedColor.getBlue() * 255));

                    card.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: " + rgb + ";" +
                            (card == selectedCard ? ("-fx-background-color: " + rgb + ";") : "-fx-background-color: white;"));
                })
        );
        colorAnimation.setCycleCount(Animation.INDEFINITE);
        colorAnimation.play();
    }
}
