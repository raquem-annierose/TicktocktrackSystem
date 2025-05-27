package ticktocktrack.gui;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class IconHelper {
    private static Image icon = new Image(IconHelper.class.getResourceAsStream("/resources/SystemIcon.png"));

    public static void applyIcon(Stage stage) {
        stage.getIcons().add(icon);
    }
}
