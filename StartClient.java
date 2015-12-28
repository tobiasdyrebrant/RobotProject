package tobdyh131;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;

/**
 * Created by Tobias on 28-Dec-15.
 */
public class StartClient extends Application {

    public static void main(String[] args) {

        launch(args);
        //(new Thread(new Client(args))).start();
    }

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
