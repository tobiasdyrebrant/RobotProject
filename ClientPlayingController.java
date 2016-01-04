package tobdyh131;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tobias on 2015-12-22.
 *
 * A controller for the users GUI while playing.
 * This class reacts and handles events on the users side while the game is running.
 */
public class ClientPlayingController implements Initializable, ControlledScreen{

    ScreensController myController;

    Client myClient;

    public int clientId;

    public int playerTurn;

    public int Board[][];

    public Timer timer;

    boolean DoingTurn = false;

    private float rowHeight;
    private float colWidth;
    private int height;
    private int width;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button moveUp ;

    @FXML
    private Button moveUpLeft;

    @FXML
    private Button moveUpRight;

    @FXML
    private Button moveDown;

    @FXML
    private Button moveDownLeft;

    @FXML
    private Button moveDownRight;

    @FXML
    private Button moveLeft;

    @FXML
    private Button moveRight;

    @FXML
    private TextArea consoleOutput;

    @FXML
    private Text Round;

    @FXML
    private Text Level;

    @FXML
    private Text PlayerTurn;

    @FXML
    private Text ClientId;


    /**
     * Its called when a key is pressed, and then calls the proper
     * method depending on what key is pressed.
     * @param e A key event
     */
    @FXML
    public void handleKeyPress(KeyEvent e)
    {

        KeyCode kc = e.getCode();
        switch(kc) {
            case NUMPAD1:
                handleMoveDownLeft();
                break;
            case NUMPAD2:
                handleMoveDown();
                break;
            case NUMPAD3:
                handleMoveDownRight();
                break;
            case NUMPAD4:
                handleMoveLeft();
                break;
            case NUMPAD5:
                handleWait();
                break;
            case NUMPAD6:
                handleMoveRight();
                break;
            case NUMPAD7:
                handleMoveUpLeft();
                break;
            case NUMPAD8:
                handleMoveUp();
                break;
            case NUMPAD9:
                handleMoveUpRight();
                break;
            case S:
                handleShortRangeAttack();
                break;
            case R:
                handleRandomTeleport();
                break;
            case T:
                handleSafeTeleport();
                break;
        }
    }

    /**
     * Called when the user wants to move right.
     */
    @FXML
    public void handleMoveRight()
    {

        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("move right");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                WriteToTextArea("Not your turn!");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to move up right (Diagonal)
     */
    @FXML
    public void handleMoveUpRight()
    {
        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("move up right");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                WriteToTextArea("Not your turn!");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to move down right (Diagonal)
     */
    @FXML
    public void handleMoveDownRight()
    {
        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("move down right");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                WriteToTextArea("Not your turn!");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to move left.
     */
    @FXML
    public void handleMoveLeft()
    {
        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("move left");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                WriteToTextArea("Not your turn!");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to move up left (Diagonal)
     */
    @FXML
    public void handleMoveUpLeft()
    {
        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("move up left");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                WriteToTextArea("Not your turn!");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to move down left (Diagonal).
     */
    @FXML
    public void handleMoveDownLeft()
    {
        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("move down left");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                WriteToTextArea("Not your turn!");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to move down.
     */
    @FXML
    public void handleMoveDown()
    {
        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("move down");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                WriteToTextArea("Not your turn!");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to move up.
     */
    @FXML
    public void handleMoveUp()
    {
        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("move up");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                WriteToTextArea("Not your turn!");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to perform a random teleport.
     */
    @FXML
    public void handleRandomTeleport()
    {
        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("random teleport");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                WriteToTextArea("Not your turn!");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to perform a safe teleport.
     */
    @FXML
    public void handleSafeTeleport()
    {
        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("safe teleport");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                WriteToTextArea("Not your turn!");
            }

        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to perform a short range attack.
     */
    @FXML
    public void handleShortRangeAttack()
    {
        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("short range attack");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                consoleOutput.appendText("Not your turn!");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to wait or when the time for moving
     * is over.
     */
    @FXML
    public void handleWait()
    {
        try
        {
            if(playerTurn == clientId ) {
                myClient.queue.put("wait");
                timer.cancel();
                timer.purge();
                DoingTurn = false;
            }
            else
            {
                consoleOutput.appendText("Not your turn!");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Called when the user wants to disconnect from the game.
     */
    @FXML
    public void handleDisconnect()
    {
        try
        {
            myClient.queue.put("quit");

        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This is called when the game is over for the user, whether it got killed or disconnected.
     * It switches scene to the High Score Scene which shows the current high score list.
     * @param highscoreList A list of the users whom scored the ten highest points.
     */
    @FXML
    public void showHighScoreScene(ObservableList<HighscoreInfo> highscoreList)
    {
       Platform.runLater(()->{
           myClient.disconnectClient();

           if(timer != null) {
               timer.cancel();
               timer.purge();
           }


           myController.loadScreen("highscoreScene", "HighscoreScene.fxml", highscoreList);
           myController.setScreen("highscoreScene");

       });
    }

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
            ServerListener t = new ServerListener(myClient.in, this);
            t.setName("Serverlistener");
            t.start();
        });

        consoleOutput.appendText("Welcome!\n\nWaiting for all players\nto connect!\n");

    }

    /**
     * Creates the board based on the two parameters.
     * @param height Height of the board
     * @param width Width of the board
     */
    public void CreateBoard(int height, int width ){
        Platform.runLater(()->{
            Board = new int[height][width];

            this.height = height;
            this.width = width;

            rowHeight = (((float)height)/559)*1000;
            colWidth = (((float)width)/556)*1000;
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
     * Updates the board for the user when a change is made.
     * @param Board The updated version of the board.
     */
    public void UpdateBoard(int[][] Board)
    {
        Platform.runLater(()-> {
            this.Board = Board;

            gridPane.getChildren().clear();

            gridPane.setGridLinesVisible(true);

            for (int i = 1; i <= Board.length; i++) {
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
     * Sets the level information for the user at the current time.
     * And if it's the users turn, starts a timer for the user to make a move.
     * @param Round The current round
     * @param Level The current level
     * @param PlayerTurn Which player who's turn it is to move
     */
    public void SetLevelInformation(int Round, int Level, int PlayerTurn)
    {
        Platform.runLater(()-> {
            this.Round.setText("Round: " + Round);
            this.Level.setText("Level: " + Level);
            this.PlayerTurn.setText("Player number " + PlayerTurn + "'s turn");
            this.ClientId.setText("Your own id: " +  clientId);
            playerTurn = PlayerTurn;

            if((playerTurn == clientId) && (!DoingTurn))
            {
                DoingTurn = true;
                timer = new Timer();


                TimerTask task = new TimerTask(){
                    @Override
                    public void run()
                    {
                        Platform.runLater(()-> {
                            try
                            {
                                WriteToTextArea("You did not move fast enough \n");
                                DoingTurn = false;
                                myClient.queue.put("wait");
                                timer.cancel();
                                timer.purge();
                            }
                            catch(InterruptedException e)
                            {
                                System.out.println("Communication queue.put IO:error: " + e.getMessage());
                            }
                        });
                    }
                };

                timer.schedule(task, 10 * 1000);
            }
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

    /**
     * Writes a message to the text area so the user can see.
     * @param msg The message to write.
     */
    public void WriteToTextArea(String msg)
    {
        consoleOutput.appendText(msg + "\n");
    }
}
