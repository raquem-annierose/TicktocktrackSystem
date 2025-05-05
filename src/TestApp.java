import javafx.application.Application;
import javafx.stage.Stage;

public class TestApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Test App");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}