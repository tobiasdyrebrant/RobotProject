package tobdyh131;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Tobias on 28-Dec-15.
 */
public class StartServer extends Application{

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen("serverStartup", "ServerStartupScene.fxml");

        mainContainer.setScreen("serverStartup");

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root, 813, 590);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
