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
 */
public class Client extends Application implements Runnable{
    private static Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    public BufferedReader in;
    private PrintWriter out;
    private BufferedReader kdb_reader;

    private String buf;

    private boolean Connected = true;


    //TODO
    //Gör som för servern..
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

    //TODO
    //Spara på disk
    public Client(InetAddress ip, int port, String UserName)
    {
        try {
            Socket socket = new Socket(ip, port);

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


    public void run()
    {
        while(Connected)
        {

            while ((buf = queue.poll()) != null) {

                if(buf.equals("quit"))
                {
                    Connected = false;
                }

                out.println(buf);

            }




        }
    }

}
