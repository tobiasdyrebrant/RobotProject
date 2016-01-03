package tobdyh131;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Tobias on 28-Dec-15.
 *
 * When an admin wants to start a server, the main function of this class is ran.
 */
public class StartServer extends Application{

    /**
     * Main function.
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * This method is called in the main function. It starts the GUI for a
     * server to be created.
     * @param primaryStage Primary stage of the GUI to be shown.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen("serverStartup", "ServerStartupScene.fxml");

        mainContainer.setScreen("serverStartup");

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root, 813, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
