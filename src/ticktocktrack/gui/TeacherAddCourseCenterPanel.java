package ticktocktrack.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TeacherAddCourseCenterPanel {

    public static Pane createPanel() {
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setLayoutX(0);
        centerPanel.setLayoutY(0);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        String shadowPath = TeacherAddCourseCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);



        centerPanel.getChildren().addAll(shadowView, createUsersTitle, openDialogBtn);

        return centerPanel;
    }

    public static Pane createAddCourseDialog(Pane root) {
        StackPane overlay = new StackPane();
        overlay.setPrefSize(1300, 750);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");

        VBox dialog = new VBox(12);
        dialog.setPrefWidth(350);
        dialog.setMaxWidth(350);
        dialog.setPadding(new Insets(20));
        dialog.setStyle("-fx-background-color: white;");
        dialog.setEffect(new DropShadow(8, Color.gray(0.3)));
        dialog.setMaxHeight(Region.USE_PREF_SIZE);

         // Title
        Text title = new Text("Add Course");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        title.setFill(Color.web("#02383E"));

        // Fields
        TextField courseField = new TextField();
        courseField.setPromptText("Course Name (required)");
        courseField.setPrefWidth(260);
        courseField.setStyle("-fx-background-color: #eeeeee; -fx-border-color: transparent transparent black transparent; -fx-border-width: 1px;");

        TextField sectionField = new TextField();
        sectionField.setPromptText("Section");
        sectionField.setPrefWidth(260);
        sectionField.setStyle("-fx-background-color: #eeeeee; -fx-border-color: transparent transparent black transparent; -fx-border-width: 1px;");

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #0097A7;");
        cancelBtn.setOnAction(e -> root.getChildren().remove(overlay));

        Button createBtn = new Button("Create");
        createBtn.setDisable(true);
        createBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaa;");

        // Enable create button when course name is filled
        courseField.textProperty().addListener((obs, oldText, newText) -> {
            boolean filled = !newText.trim().isEmpty();
            createBtn.setDisable(!filled);
            createBtn.setStyle(filled ?
                "-fx-background-color: transparent; -fx-text-fill: #333;" :
                "-fx-background-color: transparent; -fx-text-fill: #aaa;");
        });

        createBtn.setOnAction(e -> {
            System.out.println("Course: " + courseField.getText());
            System.out.println("Section: " + sectionField.getText());
            root.getChildren().remove(overlay);
        });

        buttonBox.getChildren().addAll(cancelBtn, createBtn);
        dialog.getChildren().addAll(title, courseField, sectionField, buttonBox);
        overlay.getChildren().add(dialog);
        StackPane.setAlignment(dialog, Pos.CENTER);

        return overlay;
    }
}
