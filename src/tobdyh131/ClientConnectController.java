package tobdyh131;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

/**
 * Created by Tobias on 2015-12-22.
 */
public class ClientConnectController {

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
    public void handleConnectAction(ActionEvent event)
    {
        try
        {
            InetAddress ip = InetAddress.getByName(IP.getText());
            int port = Integer.valueOf(this.port.getText());
            String userName = this.userName.getText();
            (new Thread(new Client(ip, port, userName))).start();
            Stage stage = (Stage) connectButton.getScene().getWindow();
            stage.close();
        }
        catch(UnknownHostException e)
        {
            consoleOutput.appendText("Could not connect \n");
        }

    }

}