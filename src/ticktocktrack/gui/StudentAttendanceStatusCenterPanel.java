package ticktocktrack.gui;

import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class StudentAttendanceStatusCenterPanel {

    public static Pane createPanel() {
        // Create the center panel
        Pane centerPanel = new Pane();
        centerPanel.setPrefSize(1300, 750);
        centerPanel.setLayoutX(0);
        centerPanel.setLayoutY(0);
        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Shadow image
        String shadowPath = StudentAttendanceStatusCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
        ImageView shadowView = new ImageView(new Image(shadowPath));
        shadowView.setFitWidth(1300);
        shadowView.setFitHeight(250);
        shadowView.setLayoutX(0);
        shadowView.setLayoutY(-115);

        // Create the "Create Users" Text
        Text createUsersTitle = new Text("Attendance Status");
        createUsersTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 36));
        createUsersTitle.setFill(Color.web("#02383E"));
        createUsersTitle.setLayoutX(50);
        createUsersTitle.setLayoutY(70);

        centerPanel.getChildren().addAll(shadowView, createUsersTitle);

        return centerPanel;
    }
}
