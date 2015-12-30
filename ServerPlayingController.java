package tobdyh131;

import com.sun.jmx.remote.internal.ServerCommunicatorAdmin;
import com.sun.media.jfxmedia.events.PlayerStateEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Tobias on 2015-12-22.
 */
//TODO
//Ifall alla clienter är döda så skall den gå tillbaka till host screenen.
//Fixa scoreboard update grejen...
public class ServerPlayingController implements Initializable, ControlledScreen{

    @FXML
    private ListView scoreBoardView;

    @FXML
    private Text Round;

    @FXML
    private Text Level;

    @FXML
    private Text PlayerTurn;

    @FXML
    private TextArea consoleOutput;


    @FXML
    private GridPane gridPane;


    ScreensController myController;

    public ServerSettings settings;

    public int Board[][];

    public ObservableList<ClientStats> scoreBoard = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridPane.getRowConstraints().removeAll(new RowConstraints());
        gridPane.getColumnConstraints().removeAll(new ColumnConstraints());

        Platform.runLater(()-> {
            (new Thread(new GameEngine(settings, this))).start();
            CreateBoard(settings.height, settings.width);
        });

    }

    @FXML
    public void handleDisconnectSelectedPlayer()
    {
        int indexOfPlayer = scoreBoardView.getSelectionModel().getSelectedIndex();
        if(indexOfPlayer != -1) {
            consoleOutput.appendText("You've disconnected player \"" + scoreBoard.get(indexOfPlayer) + "\"\n");
            scoreBoard.remove(indexOfPlayer);
            CommunicationThread.DisconnectClient(indexOfPlayer);
            UpdateScoreBoard();
        }
    }

    public void RemoveFromScoreBoard(int ClientID)
    {
        int index = 0;
        for (CommunicationThread CT: CommunicationThread.GetAllClients()
             ) {
            if(CT.GetClientId() == ClientID)
            {
                break;
            }
            else
            {
                index++;
            }
        }

        scoreBoard.remove(index);
        UpdateScoreBoard();
    }

    public void CreateBoard(int height, int width ){
        Platform.runLater(()->{
            Board = new int[height][width];
            float rowHeight = (((float)height)/559)*1000;
            float colWidth = (((float)width)/556)*1000;
            for(int i = 0; i < height; i++)
            {
                RowConstraints row = new RowConstraints();
                row.setPercentHeight(rowHeight);
                gridPane.getRowConstraints().addAll(row);
            }

            for(int j = 0; j < width; j++)
            {
                ColumnConstraints c = new ColumnConstraints();
                c.setPercentWidth(colWidth);
                gridPane.getColumnConstraints().addAll(c);
            }
            gridPane.setGridLinesVisible(true);
            gridPane.setVisible(true);




        });
    }

    //TODO
    // fixa så att den visar bilder istället för siffror
    public void UpdateBoard(int[][] Board)
    {
        Platform.runLater(()-> {
            this.Board = Board;

            gridPane.getChildren().clear();

            for (int i = 1; i <= Board[0].length; i++) {
                for (int j = 1; j <= Board[1].length; j++) {
                    Text object = new Text("" + Board[i - 1][j - 1]);
                    gridPane.add(object, j, i);
                }
            }
        });
    }

    public void CreateScoreBoard(CopyOnWriteArrayList<CommunicationThread> allClients)
    {
        Platform.runLater(()->{
        for (CommunicationThread CT: allClients
             ) {
            scoreBoard.add(new ClientStats(CT.clientUserName, CT.GetClientId(), 0));
        }

            UpdateScoreBoard();
        });

    }

    public void UpdateScoreBoard()
    {
        Platform.runLater(()->{
            scoreBoardView.getItems().clear();
            scoreBoardView.setItems(FXCollections.observableArrayList(scoreBoard));
        });
    }

    public void IncreasePointsOfPlayer(String UserName, int Points)
    {

            for (ClientStats player: scoreBoard
                 ) {
                if(UserName.equals(player.clientUserName))
                {
                    player.score += Points;
                }
            }

            UpdateScoreBoard();

    }

    public void SetLevelInformation(int Round, int Level, int PlayerTurn)
    {
        Platform.runLater(()-> {
            this.Round.setText("Round: " + Round);
            this.Level.setText("Level: " + Level);
            this.PlayerTurn.setText("Player number " + PlayerTurn + "'s turn");
        });
    }

    public void WriteToTextArea(String output)
    {
        consoleOutput.appendText(output + "\n");
    }

    public void GoToStartup()
    {
        Platform.runLater(()-> {
            myController.loadScreen("serverStartup", "ServerStartupScene.fxml", true);

            myController.setScreen("serverStartup");
        });
    }

    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }
}
