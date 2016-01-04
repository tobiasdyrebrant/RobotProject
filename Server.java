package tobdyh131;


import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * Created by Tobias on 2015-11-27.
 *
 * This is the Server. Its main purpose is to receive messages from the communication threads,
 * and pass the further to the game engine which then handles these messages.
 */
public class Server implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public static final BlockingQueue<ComMessage> queue = new LinkedBlockingQueue<>();

    private final BufferedReader kdb_reader;

    private ServerSettings Settings;

    public Object controller;

    //TODO
    //Skall en client kunna disconnecta under connect scenen så fixa det.

    /**
     * The constructor that creates the server based on the settings.
     * @param s Settings of the game.
     */
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

    /**
     * The function which is continuously executed during runtime.
     * If the session and game has started, it checks for messages in the queue (which receives messages from the communication threads)
     * and then passes it to the game engine.
     */
    public void run() {
        while(GameEngine.GetSessionOnGoing())
        {

            if(GameEngine.GetGameStarted()) {
                ComMessage msg;
                while ((msg = queue.poll()) != null) {
                    try {
                        GameEngine.queue.put(msg);
                    } catch (InterruptedException e) {
                        LOGGER.info("Server run queue.put : " + e.getMessage());
                    }
                }
            }


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
