package tobdyh131;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Tobias on 2015-11-27.
 */
public class Client extends Application implements Runnable{
    private static final int PORT = 1111;
    private static Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader kdb_reader;

    private String buf;

    //TODO
    //Gör som för servern..
    public static void main(String[] args) {
        LOGGER.setLevel(Level.OFF);

        launch(args);
        //(new Thread(new Client(args))).start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ServerPlayingScene.fxml"));
        primaryStage.setTitle("Client startup");
        primaryStage.setScene(new Scene(root, 315, 500));
        //primaryStage.setResizable(false);
        primaryStage.show();
    }

    public Client()
    {

    }

    //TODO
    //Spara på disk
    public Client(InetAddress ip, int port, String UserName)
    {
        try {
            Socket socket = new Socket(ip, PORT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            kdb_reader = new BufferedReader(new InputStreamReader(System.in));

            out.println(UserName);

            ServerListener t = new ServerListener(in);

            t.start();
        }
        catch (IOException e)
        {
            LOGGER.info("Client constructor I/O error : " + e.getMessage());
        }
    }


    public void run()
    {
        while(true)
        {
            try
            {
                buf = kdb_reader.readLine();
                LOGGER.info("User input: " + buf);
                LOGGER.info("To Server: " + buf);
                out.println(buf);

                if(buf.equals("quit"))
                {
                    break;
                }
            }
            catch(IOException e)
            {
                LOGGER.info("Client run I/O error : " + e.getMessage());
            }

        }
    }

}
