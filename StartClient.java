package tobdyh131;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Tobias on 28-Dec-15.
 *
 * When a client wants to start, the main function of this class is ran.
 */
public class StartClient extends Application {

    /**
     * Main function.
     * @param args
     */
    public static void main(String[] args) {

        launch(args);
    }

    public StartClient()
    {

    }


    /**
     * This method is called in the main function. It starts the GUI for a
     * client to be created and connected to a server.
     * @param primaryStage Primary stage of the GUI to be shown.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen("clientConnect", "ClientConnectScene.fxml");

        mainContainer.setScreen("clientConnect");

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root, 765, 598);
        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
