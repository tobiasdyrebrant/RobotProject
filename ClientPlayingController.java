package tobdyh131;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tobias on 2015-12-22.
 */
public class ClientPlayingController implements Initializable, ControlledScreen{

    ScreensController myController;

    Client myClient;

    public int clientId;

    public int playerTurn;

    public int Board[][];

    boolean DoingTurn = false;

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


    //TODO
    //Fixa så att man skicka enum istället
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
            System.out.println(e);
        }
    }

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
            System.out.println(e);
        }
    }

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
            System.out.println(e);
        }
    }

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
            System.out.println(e);
        }
    }

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
            System.out.println(e);
        }
    }

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
            System.out.println(e);
        }
    }

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
            System.out.println(e);
        }
    }

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
            System.out.println(e);
        }
    }

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
            System.out.println(e);
        }
    }

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
            System.out.println(e);
        }
    }

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
            System.out.println(e);
        }
    }

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
            System.out.println(e);
        }
    }

    //TODO
    //Byt scen? Highscore?
    @FXML
    public void handleDisconnect()
    {
        try
        {
            myClient.queue.put("quit");

        }
        catch(InterruptedException e)
        {
            System.out.println(e);
        }
    }


    @FXML
    public void showHighScoreScene(ObservableList<HighscoreInfo> highscoreList)
    {
       Platform.runLater(()->{
           myClient.disconnectClient();

           if(timer != null) {
               timer.cancel();
               timer.purge();
           }

           //TODO
           //Fixa highscore här

           myController.loadScreen("highscoreScene", "HighscoreScene.fxml", highscoreList);
           myController.setScreen("highscoreScene");

       });
    }


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

    public Timer timer;


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

    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }

    public void WriteToTextArea(String msg)
    {
        consoleOutput.appendText(msg + "\n");
    }
}
