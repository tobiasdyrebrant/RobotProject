package tobdyh131;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sun.plugin2.message.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
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


    //TODO
    //HIGHSCORELIST

    //TODO
    //Spara på disk när det körs igen
    public static void main(String[] args) {
        //TODO
        //Kör innan guit här, och skicka med all relevant information som argument Server(arg,args...) till ServerConnectionController och ServerGameController
        //Eventuellt från gui grejen så görs raden nedanför, så att den tråden startars när man klickar på host + all info
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ServerStartupScene.fxml"));
        primaryStage.setTitle("Server startup");
        primaryStage.setScene(new Scene(root, 725, 500));
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
        (new Thread(new ServerConnectionController(s.port))).start();

        kdb_reader = new BufferedReader(new InputStreamReader(System.in));


    }



    //TODO
    // När en client avbryter connectionen, då skall de även försvinna från boarden

    public void run() {
        while(true)
        {
            if(!ServerGameController.GetGameStarted()) {
                try
                {
                    buf = kdb_reader.readLine();

                    if(buf.equals("start")) {
                        //TODO
                        //Ska det vara >= eller bara == ?
                        if(ServerCommunicationController.GetNumberOfPlayers() >= Settings.numberOfPlayersBeforeStart) {
                            ServerCommunicationController.SendToClients("start;" + Settings.height + ";" + Settings.width + ";");
                            (new Thread(new ServerGameController(Settings))).start();
                        }
                        else
                        {
                            LOGGER.info("Not enough players are connected just yet");
                        }
                    }


                }
                catch(IOException e)
                {
                    LOGGER.info("Client run I/O error : " + e.getMessage());
                }

            }

            else {
                while ((msg = queue.poll()) != null) {
                    try {
                        ServerGameController.queue.put(msg);
                    } catch (InterruptedException e) {
                        LOGGER.info("Server run queue.put : " + e.getMessage());
                    }

                }
            }

        }
    }
}
