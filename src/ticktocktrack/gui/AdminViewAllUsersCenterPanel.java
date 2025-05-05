package ticktocktrack.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class AdminViewAllUsersCenterPanel {

	 public static Pane createPanel() {
	        // Create the center panel
	        Pane centerPanel = new Pane();
	        centerPanel.setPrefSize(1300, 750);
	        centerPanel.setLayoutX(0);
	        centerPanel.setLayoutY(0);
	        centerPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");

	        // Shadow image
	        String shadowPath = AdminViewAllUsersCenterPanel.class.getResource("/resources/SHADOW.png").toExternalForm();
	        ImageView shadowView = new ImageView(new Image(shadowPath));
	        shadowView.setFitWidth(1300);
	        shadowView.setFitHeight(250);
	        shadowView.setLayoutX(0);
	        shadowView.setLayoutY(-115);

	        // Create the "Dashboard" Text
	        Text dashboardTitle = new Text("View All Users");
	        dashboardTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 36));
	        dashboardTitle.setFill(Color.web("#02383E"));
	        dashboardTitle.setLayoutX(50);
	        dashboardTitle.setLayoutY(70);

	        centerPanel.getChildren().addAll(shadowView, dashboardTitle);

	        return centerPanel;
	    }
	}