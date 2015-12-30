package tobdyh131;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javax.naming.ldap.Control;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 * Created by Tobias on 28-Dec-15.
 */
public class HighscoreController implements Initializable, ControlledScreen{

    ScreensController myController;

    ObservableList<HighscoreInfo> highscoreList = FXCollections.observableArrayList();

    @FXML
    private ListView highScore;


    @FXML
    private Button dismiss;

    @FXML
    public void handleDismiss()
    {
        myController.loadScreen("clientConnect", "ClientConnectScene.fxml");
        myController.setScreen("clientConnect");
    }

    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(()->{
            highScore.setItems(FXCollections.observableArrayList(highscoreList));

        });
    }

}
