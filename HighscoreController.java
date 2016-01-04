package tobdyh131;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Tobias on 28-Dec-15.
 *
 * A controller for the user GUI when the high score is shown.
 * This is when the game is over, when the user has either been killed or disconnected.
 * Handles and reacts to events on the high score GUI.
 */
public class HighscoreController implements Initializable, ControlledScreen{

    private ScreensController myController;

    ObservableList<HighscoreInfo> highscoreList = FXCollections.observableArrayList();

    @FXML
    private ListView highScore;


    @FXML
    private Button dismiss;

    /**
     * Called when the user presses the "Dismiss" button on the GUI.
     */
    @FXML
    public void handleDismiss()
    {
        myController.loadScreen("clientConnect", "ClientConnectScene.fxml");
        myController.setScreen("clientConnect");
    }

    /**
     * Sets the screenParent for the controller
     * @param screenParent
     */
    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }

    /**
     * Initialize function of the controller which is called absolutely first.
     * Sets the high score list to be shown on the GUI.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(()->{
            highScore.setItems(FXCollections.observableArrayList(highscoreList));

        });
    }

}
