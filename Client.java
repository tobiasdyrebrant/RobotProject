package tobdyh131;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Tobias on 2015-11-27.
 *
 * This is the Client, its main purpose is to pass messages from the user
 * through the socket where the message is received and handled.
 */
public class Client extends Application implements Runnable{
    private static Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    public BufferedReader in;
    private PrintWriter out;
    private BufferedReader kdb_reader;
    private Socket socket;

    private String buf;

    private boolean Connected = true;


    //TODO
    //Ta bort eventuellt....
    public static void main(String[] args) {
        LOGGER.setLevel(Level.OFF);

        launch(args);
        //(new Thread(new Client(args))).start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen("clientConnect", "ClientConnectScene.fxml");
        //mainContainer.getScreen("clientConnect").
        //mainContainer.loadScreen("clientPlaying", "ClientPlayingScene.fxml");

        mainContainer.setScreen("clientConnect");

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root, 765, 598);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        //Parent root = FXMLLoader.load(getClass().getResource("ClientConnectScene.fxml"));
        //primaryStage.setTitle("Client startup");
        //primaryStage.setScene(new Scene(root, 315, 500));
        //primaryStage.setResizable(false);
        //primaryStage.show();
    }

    public Client()
    {

    }

    /**
     * Constructor that creates the client based on the 3 input arguments.
     * @param ip The ip address used for connecting to the socket
     * @param port The port used for connection to the socket
     * @param UserName The client's username
     */
    public Client(InetAddress ip, int port, String UserName)
    {
        try {
            socket = new Socket(ip, port);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            kdb_reader = new BufferedReader(new InputStreamReader(System.in));

            out.println(UserName);

        }
        catch (IOException e)
        {
            LOGGER.info("Client constructor I/O error : " + e.getMessage());
        }
    }


    /**
     * The function which is continuously executed during runtime.
     * It checks for messages in the queue (which receives messages from user input via the GUI)
     * and then passes it through the socket.
     */
    public void run()
    {
        while(Connected)
        {
            while ((buf = queue.poll()) != null) {

                out.println(buf);

            }

        }

        try{
            in.close();
            out.close();
            socket.close();
            kdb_reader.close();
        }
        catch(IOException e)
        {
            LOGGER.info(e.getMessage());
        }
    }

    /**
     * A method to cancel the run-method before to terminate this thread.
     */
    public void disconnectClient()
    {
        Connected = false;
    }

}
