package tobdyh131;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Tobias on 2015-12-22.
 *
 * A controller for the admin on startup.
 * This class reacts and handles events on the server/admin side while the game is in the startup phase.
 */
public class ServerStartupController implements Initializable, ControlledScreen{
    ScreensController myController;

    @FXML
    private Button hostButton;

    @FXML
    private TextField height;

    @FXML
    private TextField width;

    @FXML
    private TextField numberOfPlayersBeforeStart;

    @FXML
    private TextField numberOfRoundsPerLevel;

    @FXML
    private TextField numberOfRobots;

    @FXML
    private TextField increaseOfRobotsPerLevel;

    @FXML
    private TextField numberOfRubbles;

    @FXML
    private TextField changeOfRubblesPerLevel;

    @FXML
    private TextField numberOfSafeTeleportationsAwardedPerLevel;

    @FXML
    private TextField numberOfSafeTeleportationsAwardedPerKill;

    @FXML
    private TextField numberOfShortRangeAttacksAwardedPerLevel;

    @FXML
    private TextField robotPerceptionRowRange;

    @FXML
    private TextField robotPerceptionColumnRange;

    @FXML
    private TextField port;

    @FXML
    private TextArea consoleOutput;

    @FXML
    private CheckBox robotsMergeOnCollision;

    @FXML
    private CheckBox shortRangeAttacksKillsAllAdjacentRobots;

    @FXML
    private CheckBox robotsLockToTarget;


    /**
     * When the event is fired, the method takes the information from all the text fields and creates
     * server settings with that information. Then creates a server thread from those settings, and starts that thread.
     * @param event Event when the button "Host on port" is pressed.
     */
    @FXML
    public void handleHostAction(ActionEvent event)
    {
        ServerSettings Settings = new ServerSettings();

        try {
            //Maximum height 15, minimum 5
            Settings.height = (Integer.valueOf(height.getText()) > 15) ? 15 : Integer.valueOf(height.getText());
            if(Integer.valueOf(height.getText()) > 15)
            {
                Settings.height = 15;
            }
            else if(Integer.valueOf(height.getText()) < 5)
            {
                Settings.height = 5;
            }
            else
            {
                Settings.height = Integer.valueOf(height.getText());
            }


            //Maximum width 15, minimum 5
            if(Integer.valueOf(width.getText()) > 15)
            {
                Settings.width = 15;
            }
            else if(Integer.valueOf(width.getText()) < 5)
            {
                Settings.width = 5;
            }
            else
            {
                Settings.width = Integer.valueOf(width.getText());
            }

            Settings.numberOfPlayersBeforeStart = Integer.valueOf(numberOfPlayersBeforeStart.getText());
            Settings.numberOfRoundsPerLevel = Integer.valueOf(numberOfRoundsPerLevel.getText());
            Settings.numberOfRobots = Integer.valueOf(numberOfRobots.getText());
            Settings.increaseOfRobotsPerLevel = Integer.valueOf(increaseOfRobotsPerLevel.getText());
            Settings.robotsMergeOnCollision = robotsMergeOnCollision.isSelected();
            Settings.numberOfRubbles = Integer.valueOf(numberOfRubbles.getText());
            Settings.changeOfRubblesPerLevel = Integer.valueOf(changeOfRubblesPerLevel.getText());
            Settings.numberOfSafeTeleportationsAwardedPerKill = Integer.valueOf(numberOfSafeTeleportationsAwardedPerKill.getText());
            Settings.numberOfSafeTeleportationsAwardedPerLevel = Integer.valueOf(numberOfSafeTeleportationsAwardedPerLevel.getText());
            Settings.numberOfShortRangeAttacksAwardedPerLevel = Integer.valueOf(numberOfShortRangeAttacksAwardedPerLevel.getText());
            Settings.robotsLockToTarget = robotsLockToTarget.isSelected();
            //If false, kills one random robot in the area of "2 blocks away".
            Settings.shortRangeAttacksKillsAllAdjacentRobots = shortRangeAttacksKillsAllAdjacentRobots.isSelected();
            Settings.robotPerceptionRowRange = Integer.valueOf(robotPerceptionRowRange.getText());
            Settings.robotPerceptionColumnRange = Integer.valueOf(robotPerceptionColumnRange.getText());
            Settings.port = Integer.valueOf(port.getText());

            Server s = new Server(Settings);
            Thread t = new Thread(s);
            t.setName("Server");
            t.start();
            WriteToFile(Settings);

            myController.loadScreen("serverDuringConnection", "ServerConnectionScene.fxml", s);
            myController.setScreen("serverDuringConnection");
        }
        catch(NumberFormatException e)
        {
            consoleOutput.appendText("You've set a string where it should \n be an integer! \n");
        }

    }

    /**
     * Sets the screen parent for the controller.
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

    }

    /**
     * If it's a consecutive round being started, then this method is called.
     * This method reads from a specific file and then sets all the settings
     * based on the information from that file.
     */
    public void ReadSettingsFromFile() {
        try {


            File file = new File(getClass().getResource("serverInfo.txt").toURI());

            //"C:\\Users\\Tobias\\IdeaProjects\\RobotProject_v3\\src\\tobdyh131\\serverInfo.txt";
            String line = null;

            try {
                FileReader fileReader =
                        new FileReader(file);

                BufferedReader bufferedReader =
                        new BufferedReader(fileReader);

                while ((line = bufferedReader.readLine()) != null) {
                    String[] splitLine = line.split(";");

                    switch (splitLine[0]) {
                        case "height":
                            height.setText(splitLine[1]);
                            break;
                        case "width":
                            width.setText(splitLine[1]);
                            break;
                        case "numberOfPlayersBeforeStart":
                            numberOfPlayersBeforeStart.setText(splitLine[1]);
                            break;
                        case "numberOfRoundsPerLevel":
                            numberOfRoundsPerLevel.setText(splitLine[1]);
                            break;
                        case "numberOfRobots":
                            numberOfRobots.setText(splitLine[1]);
                            break;
                        case "increaseOfRobotsPerLevel":
                            increaseOfRobotsPerLevel.setText(splitLine[1]);
                            break;
                        case "robotsMergeOnCollision":
                            if (robotsMergeOnCollision.isSelected() && splitLine[1].equals("false"))
                                robotsMergeOnCollision.fire();
                            else if (!robotsMergeOnCollision.isSelected() && splitLine[1].equals("true"))
                                robotsMergeOnCollision.fire();
                            break;
                        case "numberOfRubbles":
                            numberOfRubbles.setText(splitLine[1]);
                            break;
                        case "changeOfRubblesPerLevel":
                            changeOfRubblesPerLevel.setText(splitLine[1]);
                            break;
                        case "numberOfSafeTeleportationsAwardedPerKill":
                            numberOfSafeTeleportationsAwardedPerKill.setText(splitLine[1]);
                            break;
                        case "numberOfSafeTeleportationsAwardedPerLevel":
                            numberOfSafeTeleportationsAwardedPerLevel.setText(splitLine[1]);
                            break;
                        case "numberOfShortRangeAttacksAwardedPerLevel":
                            numberOfShortRangeAttacksAwardedPerLevel.setText(splitLine[1]);
                            break;
                        case "robotsLockToTarget":
                            if (robotsLockToTarget.isSelected() && splitLine[1].equals("false"))
                                robotsLockToTarget.fire();
                            else if (!robotsLockToTarget.isSelected() && splitLine[1].equals("true"))
                                robotsLockToTarget.fire();
                            break;

                        case "shortRangeAttacksKillsAllAdjacentRobots":
                            if (shortRangeAttacksKillsAllAdjacentRobots.isSelected() && splitLine[1].equals("false"))
                                shortRangeAttacksKillsAllAdjacentRobots.fire();
                            else if (!shortRangeAttacksKillsAllAdjacentRobots.isSelected() && splitLine[1].equals("true"))
                                shortRangeAttacksKillsAllAdjacentRobots.fire();
                            break;
                        case "robotPerceptionRowRange":
                            robotPerceptionRowRange.setText(splitLine[1]);
                            break;
                        case "robotPerceptionColumnRange":
                            robotPerceptionColumnRange.setText(splitLine[1]);
                            break;
                        case "port":
                            port.setText(splitLine[1]);
                            break;
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
            consoleOutput.appendText(e.getMessage() + "\n");
        }
    }

    /**
     * Before a game is started, the current settings is save to file so it can be used
     * on consecutive rounds.
     * @param s Settings of the server.
     */
    public void WriteToFile(ServerSettings s) {
        try {
            File file = new File(getClass().getResource("serverInfo.txt").toURI());
            //"C:\\Users\\Tobias\\IdeaProjects\\RobotProject_v3\\src\\tobdyh131\\serverInfo.txt";
            try {
                FileWriter fileWriter =
                        new FileWriter(file);

                BufferedWriter bufferedWriter =
                        new BufferedWriter(fileWriter);


                bufferedWriter.write("height;" + s.height + ";\n");
                bufferedWriter.write("width;" + s.width + ";\n");
                bufferedWriter.write("numberOfPlayersBeforeStart;" + s.numberOfPlayersBeforeStart + ";\n");
                bufferedWriter.write("numberOfRoundsPerLevel;" + s.numberOfRoundsPerLevel + ";\n");
                bufferedWriter.write("numberOfRobots;" + s.numberOfRobots + ";\n");
                bufferedWriter.write("increaseOfRobotsPerLevel;" + s.increaseOfRobotsPerLevel + ";\n");
                bufferedWriter.write("robotsMergeOnCollision;" + s.robotsMergeOnCollision + ";\n");
                bufferedWriter.write("numberOfRubbles;" + s.numberOfRubbles + ";\n");
                bufferedWriter.write("changeOfRubblesPerLevel;" + s.changeOfRubblesPerLevel + ";\n");
                bufferedWriter.write("numberOfSafeTeleportationsAwardedPerKill;" + s.numberOfSafeTeleportationsAwardedPerKill + ";\n");
                bufferedWriter.write("numberOfSafeTeleportationsAwardedPerLevel;" + s.numberOfSafeTeleportationsAwardedPerLevel + ";\n");
                bufferedWriter.write("numberOfShortRangeAttacksAwardedPerLevel;" + s.numberOfShortRangeAttacksAwardedPerLevel + ";\n");
                bufferedWriter.write("robotsLockToTarget;" + s.robotsLockToTarget + ";\n");
                bufferedWriter.write("shortRangeAttacksKillsAllAdjacentRobots;" + s.shortRangeAttacksKillsAllAdjacentRobots + ";\n");
                bufferedWriter.write("robotPerceptionRowRange;" + s.robotPerceptionRowRange + ";\n");
                bufferedWriter.write("robotPerceptionColumnRange;" + s.robotPerceptionColumnRange + ";\n");
                bufferedWriter.write("port;" + s.port + ";\n");


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
