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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Tobias on 2015-12-22.
 *
 * A controller for the server/admin GUI while playing.
 * This class reacts and handles events on the server/admin side while the game is running.
 */
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

    /**
     * Initialize function of the controller which is called absolutely first.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridPane.getRowConstraints().removeAll(new RowConstraints());
        gridPane.getColumnConstraints().removeAll(new ColumnConstraints());

        Platform.runLater(()-> {
            (new Thread(new GameEngine(settings, this))).start();
            CreateBoard(settings.height, settings.width);
        });

    }

    /**
     * If the admin wants to disconnect a selected client during gameplay,
     * this method is called.
     */
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

    /**
     * If a client is disconnect/killed this method removes it
     * from the score board shown for the admin.
     * @param ClientID Id of the client.
     */
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

    /**
     * Creates the board based on the arguments.
     * @param height Height of the board.
     * @param width Width of the board.
     */
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

    /**
     * Updates the board to the current version.
     * @param Board Updated version of the board.
     */
    public void UpdateBoard(int[][] Board)
    {
        Platform.runLater(()-> {
            this.Board = Board;

            gridPane.getChildren().clear();

            for (int i = 1; i <= Board[0].length; i++) {
                for (int j = 1; j <= Board[1].length; j++) {



                    if(Board[i - 1][j - 1] < -1) {
                        gridPane.add(LoadPicture("Robot.png"), j, i);
                    }
                    else if(Board[i - 1][j - 1] == -1)
                    {
                        gridPane.add(LoadPicture("Obstacle.jpg"), j, i);
                    }
                    else if(Board[i - 1][j - 1] > 0)
                    {
                        gridPane.add(LoadPlayerImage(Board[i - 1][j - 1]), j, i);
                    }
                    else if(Board[i - 1][j - 1]  == 0)
                    {
                        Text empty = new Text("");
                        gridPane.add(empty, j, i);
                    }

                }
            }
        });
    }

    /**
     * Loads the correct picture of the player to display on the board
     * @param clientID The users id so the right picture is chosen
     * @return An Image View which is used on the board.
     */
    public ImageView LoadPlayerImage(int clientID)
    {
        try
        {
            File file = new File(getClass().getResource("player" + clientID + ".png").toURI());
            Image image = new Image("file:" + file.toString());
            ImageView pic = new ImageView();
            pic.setImage(image);
            pic.setFitHeight((gridPane.getPrefHeight() / gridPane.getRowConstraints().size()) / 1.1);
            pic.setFitWidth((gridPane.getPrefWidth() / gridPane.getColumnConstraints().size()) / 1.1);
            return pic;
        }
        catch(URISyntaxException e)
        {
            System.out.println(e);
        }

        return null;
    }

    /**
     * Loads a picture of either a robot or a obstacle depending on the argument.
     * @param fileName The name of the file to be loaded
     * @return An Image View which is used on the board.
     */
    public ImageView LoadPicture(String fileName)
    {
        try
        {
            File file = new File(getClass().getResource(fileName).toURI());
            Image image = new Image("file:" + file.toString());
            ImageView pic = new ImageView();
            pic.setImage(image);
            pic.setFitHeight((gridPane.getPrefHeight() / gridPane.getRowConstraints().size()) / 1.1);
            pic.setFitWidth((gridPane.getPrefWidth() / gridPane.getColumnConstraints().size()) / 1.1);
            return pic;
        }
        catch(URISyntaxException e)
        {
            System.out.println(e);
        }

        return null;
    }


    /**
     * Creates a score board based on all the clients.
     * @param allClients A list of all the clients connected.
     */
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

    /**
     * Updates the score board when a client has increased their score.
     */
    public void UpdateScoreBoard()
    {
        Platform.runLater(()->{
            scoreBoardView.getItems().clear();
            scoreBoardView.setItems(FXCollections.observableArrayList(scoreBoard));
        });
    }

    /**
     * Increase score of a client.
     * @param UserName Username of the client.
     * @param Points The amount of points to increase a clients score by.
     */
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

    /**
     * Sets the level information for the admin at the current time.
     * @param Round The current round
     * @param Level The current level
     * @param PlayerTurn Which player who's turn it is to move
     */
    public void SetLevelInformation(int Round, int Level, int PlayerTurn) {
        Platform.runLater(() -> {
            this.Round.setText("Round: " + Round);
            this.Level.setText("Level: " + Level);
            this.PlayerTurn.setText("Player number " + PlayerTurn + "'s turn");
        });
    }

    /**
     * Writes a message to the text area so the admin can see.
     * @param output The message to write.
     */
    public void WriteToTextArea(String output)
    {
        consoleOutput.appendText(output + "\n");
    }

    /**
     * If the game is over, the server goes back to the start up phase by changing scene
     * and this method is called.
     */
    public void GoToStartup()
    {
        Platform.runLater(()-> {
            myController.loadScreen("serverStartup", "ServerStartupScene.fxml", true);

            myController.setScreen("serverStartup");
        });
    }

    /**
     * Sets the screen parent for the controller.
     * @param screenParent
     */
    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }
}
