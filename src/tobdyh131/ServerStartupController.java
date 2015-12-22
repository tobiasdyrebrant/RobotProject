package tobdyh131;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerStartupController {

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



    @FXML
    public void handleHostAction(ActionEvent event)
    {
        ServerSettings Settings = new ServerSettings();
        Settings.height = Integer.valueOf(height.getText());
        Settings.width = Integer.valueOf(width.getText());
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

        (new Thread(new Server(Settings))).start();

        Stage stage = (Stage) hostButton.getScene().getWindow();
        stage.close();



    }






}
