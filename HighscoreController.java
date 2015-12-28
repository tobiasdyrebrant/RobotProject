package tobdyh131;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javax.naming.ldap.Control;
import javax.swing.text.html.ListView;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Tobias on 28-Dec-15.
 */
public class HighscoreController implements Initializable, ControlledScreen{

    ScreensController myController;

    @FXML
    public ListView highScore;

    @FXML
    public Button dismiss;

    @FXML
    public void handleDismiss(ActionEvent event)
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

    }
}
