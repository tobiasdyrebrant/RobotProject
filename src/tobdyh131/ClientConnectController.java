package tobdyh131;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

/**
 * Created by Tobias on 2015-12-22.
 */
public class ClientConnectController implements Initializable, ControlledScreen{

    ScreensController myController;

    @FXML
    private TextField IP;

    @FXML
    private TextField port;

    @FXML
    private TextField userName;

    @FXML
    private TextArea consoleOutput;

    @FXML
    private Button connectButton;

    @FXML
    private ImageView iV;

    @FXML
    public void handleConnectAction(ActionEvent event)
    {
        try
        {
            InetAddress ip = InetAddress.getByName(IP.getText());
            int port = Integer.valueOf(this.port.getText());
            String userName = this.userName.getText();

            Client c = new Client(ip, port, userName);
            (new Thread(c)).start();

            myController.loadScreen("clientPlaying", "ClientPlayingScene.fxml", c);
            myController.setScreen("clientPlaying");

        }
        catch(UnknownHostException e)
        {
            consoleOutput.appendText("Could not connect to any server\n");
        }

    }

    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}