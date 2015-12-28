package tobdyh131;

import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by Tobias on 2015-11-24.
 */
public class ServerConnectionThread implements Runnable {
    private static Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
    private static FileHandler fh;

    private ServerSocket s;

    private Object controller;

    private boolean waitingForConnections = true;

    public ServerConnectionThread(int PORT, Object controller) {
        try {
            //LOGGER.setLevel(Level.OFF);
            fh = new FileHandler("C:\\Users\\Tobias\\Documents\\Kurser\\Java för nätverk\\RobotProject\\ServerConnectionThread.log");
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            //LOGGER.setUseParentHandlers(false);

            this.controller = controller;

            s = new ServerSocket(PORT);
            LOGGER.info("Server-socket: " + s);

        } catch (IOException e) {
            LOGGER.info("ServerConnectionThread constructor I/O error: " + e.getMessage());
        }

    }


    public void run() {
        try {
            while (waitingForConnections) {
                if(!GameEngine.GetGameStarted()) {
                    LOGGER.info("Waiting for connection... \n");
                    try {
                        Socket socket = s.accept();
                        try {
                            //TODO
                            //Eventuellt gör något mer än att inte tillåta connection (felmeddelande)
                            if (!GameEngine.GetGameStarted()) {
                                LOGGER.info("Connection accepted");
                                LOGGER.info("The new socket: " + socket);

                                CommunicationThread SCC = new CommunicationThread(socket);
                                Thread t = new Thread(SCC);
                                t.setName("Communiation Thread");
                                t.start();

                                Platform.runLater(()-> {
                                    ((ServerDuringConnectionController) controller).listOfPlayers.add(SCC.clientUserName);
                                });


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
                    catch(SocketException e)
                    {
                        LOGGER.info(e.getMessage());
                    }


                }
                else
                {
                    waitingForConnections = false;
                    s.close();
                }

            }

        } catch (IOException e) {
            LOGGER.info("ServerConnectionThread run I/O error: " + e.getMessage());
        }
    }

    public void CloseConnectionThread()
    {
        waitingForConnections = false;

        try
        {
            s.close();
        }
        catch(IOException e){
            LOGGER.info(e.getMessage());
        }

    }


}

/*finally {
            try {
                s.close();
            } catch (IOException e) {
                LOGGER.info("ServerConnectionThread run-finally I/O error: " + e.getMessage());
            }

        }*/