package tobdyh131;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Tobias on 2015-12-22.
 */
//TODO
//Använd Platform.Runlater sedan när saker och ting ändras i boarden
public class ClientPlayingController implements Initializable, ControlledScreen{

    ScreensController myController;

    Client myClient;

    public int Board[][];

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




    //TODO
    //Fixa så att man skicka enum istället
    @FXML
    public void handleMoveRight()
    {

        try
        {
            myClient.queue.put("move right");
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
            myClient.queue.put("move up right");
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
            myClient.queue.put("move down right");
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
            myClient.queue.put("move left");
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
            myClient.queue.put("move up left");
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
            myClient.queue.put("move down left");
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
            myClient.queue.put("move down");
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
            myClient.queue.put("move up");
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
            myClient.queue.put("random teleport");
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
            myClient.queue.put("safe teleport");

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
            myClient.queue.put("short range attack");
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
            myClient.queue.put("wait");
        }
        catch(InterruptedException e)
        {
            System.out.println(e);
        }
    }

    @FXML
    public void handleDisconnect()
    {
        try
        {
            myClient.queue.put("quit");
            Stage s = (Stage)moveUp.getScene().getWindow();
            s.close();


        }
        catch(InterruptedException e)
        {
            System.out.println(e);
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridPane.getRowConstraints().removeAll(new RowConstraints());
        gridPane.getColumnConstraints().removeAll(new ColumnConstraints());


        Platform.runLater(()-> {
            ServerListener t = new ServerListener(myClient.in, this);
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
            //Text h = new Text("waza");
            //gridPane.add(h, 1, 1);

            gridPane.getChildren().clear();

            for (int i = 1; i <= Board[0].length; i++) {
                for (int j = 1; j <= Board[1].length; j++) {
                    Text object = new Text("" + Board[i - 1][j - 1]);
                    gridPane.add(object, j, i);
                }
            }
        });
    }

    public void SetLevelInformation(int Round, int Level, int PlayerTurn)
    {
        this.Round.setText("Round: " + Round);
        this.Level.setText("Level: " + Level);
        this.PlayerTurn.setText("Player number " + PlayerTurn + "'s turn");
    }

    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }

    public void WriteToTextArea(String msg)
    {
        consoleOutput.appendText(msg);
    }
}
