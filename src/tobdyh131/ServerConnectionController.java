package tobdyh131;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by Tobias on 2015-11-24.
 */
public class ServerConnectionController implements Runnable {
    private static Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
    private static FileHandler fh;

    private ServerSocket s;

    //TODO
    //Titta på youtube exemplet med att ändra scener osv.
    public ServerConnectionController(int PORT) {
        try {
            //LOGGER.setLevel(Level.OFF);
            fh = new FileHandler("C:\\Users\\Tobias\\Documents\\Kurser\\Java för nätverk\\RobotProject\\ServerConnectionController.log");
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            //LOGGER.setUseParentHandlers(false);

            s = new ServerSocket(PORT);
            LOGGER.info("Server-socket: " + s);

        } catch (IOException e) {
            LOGGER.info("ServerConnectionController constructor I/O error: " + e.getMessage());
        }

    }


    public void run() {
        try {
            while (true) {
                if(!ServerGameController.GetGameStarted()) {
                    LOGGER.info("Waiting for connection...");
                    Socket socket = s.accept();

                    try {
                        //TODO
                        //Eventuellt gör något mer än att inte tillåta connection (felmeddelande)
                        if (!ServerGameController.GetGameStarted()) {
                            LOGGER.info("Connection accepted");
                            LOGGER.info("The new socket: " + socket);
                            (new Thread(new ServerCommunicationController(socket))).start();
                            LOGGER.info("New communication thread started.");
                        }
                        else
                        {
                            socket.close();
                        }

                    } catch (IOException e) {
                        socket.close();
                    }
                }

            }

        } catch (IOException e) {
            LOGGER.info("ServerConnectionController run I/O error: " + e.getMessage());
        } finally {
            try {
                s.close();
            } catch (IOException e) {
                LOGGER.info("ServerConnectionController run-finally I/O error: " + e.getMessage());
            }

        }
    }


}
