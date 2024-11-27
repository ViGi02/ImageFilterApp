/**
 * Entry point for the JavaFX Image Filter application.
 * 
 * @author Unathi Vayeke
 */
import imageFilterApp.ImageFilterApp;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ImageFilterApp app = new ImageFilterApp();
        BorderPane root = app.createRoot(primaryStage);
        Scene scene = new Scene(root, 800, 600);
        scene.setFill(Color.LIGHTGRAY);
        scene.getStylesheets().add("file:docs/style.css");
        
        primaryStage.setTitle("ViGi's Image Filter App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main method to launch the JavaFX application.
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
