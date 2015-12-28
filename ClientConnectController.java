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
import java.io.*;
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

    //TODO
    //Unikt username, kan eventuellt läggas till typ (1) i slutet av namnet vid connection ifall samma namn hittas.
    @FXML
    public void handleConnectAction(ActionEvent event)
    {
        try
        {
            InetAddress ip = InetAddress.getByName(IP.getText());
            int port = Integer.valueOf(this.port.getText());
            String userName = this.userName.getText();

            Client c = new Client(ip, port, userName);
            Thread t = new Thread(c);
            t.setName("Client");
            t.start();

            WriteToFile();

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
        ReadFromFile();

    }

    public void ReadFromFile()
    {
        String fileName = "C:\\Users\\Tobias\\IdeaProjects\\RobotProject_v3\\src\\tobdyh131\\clientInfo.txt";
        String line = null;

        try {
            FileReader fileReader =
                    new FileReader(fileName);

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                String[] splitLine = line.split(";");
                if(splitLine[0].equals("ip"))
                {
                    IP.setText(splitLine[1]);
                }
                else
                {
                    userName.setText(splitLine[1]);
                }

            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            consoleOutput.appendText(
                    "Unable to open file '" +
                            fileName + "'\n");
        }
        catch(IOException ex) {
            consoleOutput.appendText(
                    "Error reading file '"
                            + fileName + "'\n");
        }
    }

    public void WriteToFile()
    {
        String fileName = "C:\\Users\\Tobias\\IdeaProjects\\RobotProject_v3\\src\\tobdyh131\\clientInfo.txt";
        try {
            FileWriter fileWriter =
                    new FileWriter(fileName);

            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);


            bufferedWriter.write("ip;" + IP.getText() + ";\n");
            bufferedWriter.write("username;" + userName.getText() + ";\n");


            bufferedWriter.close();
        }
        catch(IOException ex) {
            consoleOutput.appendText(
                    "Error writing to file '"
                            + fileName + "'\n");

        }

    }

}