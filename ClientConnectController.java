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
import java.net.*;
import java.util.ResourceBundle;

/**
 * Created by Tobias on 2015-12-22.
 *
 * A controller for the clients connection GUI that the user first will see when starting.
 * This class reacts and handles events on the connection GUI.
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

    /**
     * When the client presses on the "Connect" button, this method is called.
     * It checks what the relevant text field values and creates a new client thread
     * based on those, and then starts the thread.
     * @param event A button-click event
     */
    @FXML
    public void handleConnectAction(ActionEvent event)
    {
        try
        {

            InetAddress ip = InetAddress.getByName(IP.getText());

            int port = Integer.valueOf(this.port.getText());


            String userName = this.userName.getText();

            Client c = new Client(ip, port, userName);
            if(!c.SocketError) {
                Thread t = new Thread(c);
                t.setName("Client");
                t.start();

                WriteToFile();

                myController.loadScreen("clientPlaying", "ClientPlayingScene.fxml", c);
                myController.setScreen("clientPlaying");
            }
            else
            {
                consoleOutput.appendText("Could not connect to any server! \n");
            }







        }
        catch(UnknownHostException e)
        {
            consoleOutput.appendText("Wrong syntax on ip\n");
        }
        catch(NumberFormatException e)
        {
            consoleOutput.appendText("Wrong syntax on port\n");
        }

    }

    /**
     * Checks whether or not it's possible to connect to the server.
     * @param ip The ip address used for connecting to the socket
     * @param port The port used for connection to the socket
     * @return True or false whether you can connect or not.
     */
    public static boolean hostAvailabilityCheck(InetAddress ip, int port) {
        try (Socket s = new Socket(ip, port)) {
            return true;
        } catch (IOException ex) {
        /* ignore */
        }
        return false;
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
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ReadFromFile();
    }

    /**
     * Reads from a file with what information the user used the last connection.
     * Then sets the text fields to that information.
     */
    public void ReadFromFile()
    {
        try {
            File file = new File(getClass().getResource("clientInfo.txt").toURI());
                //"C:\\Users\\Tobias\\IdeaProjects\\RobotProject_v3\\src\\tobdyh131\\clientInfo.txt";
            String line = null;

            try {
                FileReader fileReader =
                        new FileReader(file);

                BufferedReader bufferedReader =
                        new BufferedReader(fileReader);

                while ((line = bufferedReader.readLine()) != null) {
                    String[] splitLine = line.split(";");
                    if (splitLine[0].equals("ip")) {
                        IP.setText(splitLine[1]);
                    } else {
                        userName.setText(splitLine[1]);
                    }

                }

                bufferedReader.close();
            } catch (FileNotFoundException ex) {
                consoleOutput.appendText(
                        "Unable to open file '" +
                                file.toString() + "'\n");
            } catch (IOException ex) {
                consoleOutput.appendText(
                        "Error reading file '"
                                + file.toString() + "'\n");
            }
        }
        catch(URISyntaxException e)
        {
            consoleOutput.appendText("URI syntax error: " + e.getMessage() + "\n");
        }
    }

    /**
     * Before closing, the information used for connection is saved to a file
     * using this method.
     */
    public void WriteToFile() {
        try {
            File file = new File(getClass().getResource("clientInfo.txt").toURI());

            //"C:\\Users\\Tobias\\IdeaProjects\\RobotProject_v3\\src\\tobdyh131\\clientInfo.txt";
            try {
                FileWriter fileWriter =
                        new FileWriter(file);

                BufferedWriter bufferedWriter =
                        new BufferedWriter(fileWriter);


                bufferedWriter.write("ip;" + IP.getText() + ";\n");
                bufferedWriter.write("username;" + userName.getText() + ";\n");


                bufferedWriter.close();
            } catch (IOException ex) {
                consoleOutput.appendText(
                        "Error writing to file '"
                                + file.toString() + "'\n");

            }

        }
        catch(URISyntaxException e)
        {
            consoleOutput.appendText(e.getMessage() + "\n");
        }
    }

}