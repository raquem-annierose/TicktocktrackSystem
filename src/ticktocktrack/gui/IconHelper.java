package ticktocktrack.gui;

import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Utility class to manage and apply the application icon to JavaFX stages.
 */
public class IconHelper {

    /**
     * The application icon image loaded from the resources folder.
     * This icon is shared and reused for all stages.
     */
    private static final Image icon = new Image(IconHelper.class.getResourceAsStream("/resources/SystemIcon.png"));

    /**
     * Applies the application icon to the specified JavaFX stage.
     *
     * @param stage the JavaFX Stage to set the icon for
     */
    public static void applyIcon(Stage stage) {
        stage.getIcons().add(icon);
    }
}


