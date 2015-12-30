package tobdyh131;


import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * Created by Tobias on 2015-11-27.
 */
public class Server extends Application implements Runnable {
    private static Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public static BlockingQueue<ComMessage> queue = new LinkedBlockingQueue<ComMessage>();

    private BufferedReader kdb_reader;
    private ComMessage msg;
    private String buf;

    private ServerSettings Settings;

    public Object controller;

    //TODO
    //HIGHSCORELIST
    //Läs från en fil med hela listen, och sen så skicka genom en sträng hela skiten liknande allt annat.

    //TODO
    //Skall en client kunna disconnecta under connect scenen så fixa det.

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen("serverStartup", "ServerStartupScene.fxml");

        mainContainer.setScreen("serverStartup");

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root, 813, 590);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Server()
    {

    }
    // Skapar en servertråd som lyssnar efter connections
    // Skapar en tråd som hanterar logiken
    // Skaper en tråd som kommunicerar
    public Server(ServerSettings s)
    {
        Settings = new ServerSettings();
        Settings = s;
        /*Settings.height = 8;
        Settings.width = 8;
        Settings.numberOfPlayersBeforeStart = 1;
        Settings.numberOfRoundsPerLevel = 10;
        Settings.numberOfRobots = 3;
        Settings.increaseOfRobotsPerLevel = 0;
        Settings.robotsMergeOnCollision = false;
        Settings.numberOfRubbles = 0;
        Settings.changeOfRubblesPerLevel = 0;
        Settings.numberOfSafeTeleportationsAwardedPerKill = 1;
        Settings.numberOfSafeTeleportationsAwardedPerLevel = 1;
        Settings.numberOfShortRangeAttacksAwardedPerLevel = 2;
        Settings.robotsLockToTarget = true;
        //If false, kills one random robot in the area of "2 blocks away".
        Settings.shortRangeAttacksKillsAllAdjacentRobots = true;
        Settings.robotPerceptionRowRange = 4;
        Settings.robotPerceptionColumnRange = 4;*/

        LOGGER.info("Starting Server");

        kdb_reader = new BufferedReader(new InputStreamReader(System.in));


    }


    public void run() {
        while(GameEngine.GetSessionOnGoing())
        {
            if(GameEngine.GetGameStarted()) {
                while ((msg = queue.poll()) != null) {
                    try {
                        GameEngine.queue.put(msg);
                    } catch (InterruptedException e) {
                        LOGGER.info("Server run queue.put : " + e.getMessage());
                    }
                }
            }
            /*
            else
            {
                while((msg = queue.poll()) != null)
                {
                    if(msg.Message.equals("quit"))
                        ((ServerDuringConnectionController)controller).playerDisconnected(msg.ClientId);
                }
            }
            */

        }

        try
        {
            LOGGER.info("Shutting down server");
            GameEngine.ResetStaticVariables();
            kdb_reader.close();
        }
        catch(IOException e)
        {
            LOGGER.info(e.getMessage());
        }
    }

    public ServerSettings GetServerSettings()
    {
        return this.Settings;
    }
}
