package ticktocktrack.gui;

import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;

public class StudentViewMyAttendanceCenterPanel {

    public static Pane createPanel() {
        // Create the center panel
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setLayoutX(0);
        centerPanel.setLayoutY(0);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Shadow image
        String shadowPath = StudentViewMyAttendanceCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        // Create a centered rectangle
        Rectangle centeredRect = new Rectangle();
        centeredRect.setWidth(840);
        centeredRect.setHeight(511);
        centeredRect.setFill(Paint.valueOf("#FFFFFF"));
        centeredRect.setStroke(Paint.valueOf("#CBCBCB"));
        centeredRect.setStrokeWidth(2);
        centeredRect.setLayoutX((1300 - 1075) / 2.0); // center horizontally
        centeredRect.setLayoutY((750 - 630) / 2.0);   // center vertically

        // Drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(4);
        dropShadow.setRadius(10);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        centeredRect.setEffect(dropShadow);

        // Add all nodes to the panel
        centerPanel.getChildren().addAll(shadowView, centeredRect);

        return centerPanel;
    }
}
