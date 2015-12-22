package tobdyh131;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tobias on 2015-11-27.
 */
public class ServerCommunicationController implements Runnable{
    private static int numberOfClients = 0;
    private static CopyOnWriteArrayList<ServerCommunicationController> allClients = new CopyOnWriteArrayList<ServerCommunicationController>();
    private static int PlayerTurnIndex = 1; // If 0, computers turn
    private final int clientNumber = ++numberOfClients;

    private String clientUserName;
    private int numberOfSafeTeleportations;
    private int numberOfShortRangeAttacks;

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String inline = "";
    private boolean deadlineForMoveReached = false;

    private static Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    TimerTask task = new TimerTask(){
        public void run(){
            if( inline.equals("") ){
                out.println( "you input nothing. exit..." );
                try
                {
                    Server.queue.put(new ComMessage("wait", clientNumber));
                }
                catch(InterruptedException e)
                {
                    LOGGER.info("Communication queue.put IO:error: " + e.getMessage());
                }

            }
        }
    };

    public ServerCommunicationController(Socket s) throws IOException {
        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        String clientUserName = in.readLine();
        this.clientUserName = clientUserName;

        out.println("You're connected too the server");
        allClients.add(this);

        try {
            Server.queue.put(new ComMessage("spawn", clientNumber));
        }
        catch(InterruptedException e)
        {
            LOGGER.info("ServerCommunicationController: " + e.getMessage());
        }
    }

    //TODO
    //Fixa så att clienter inte kan random tp:a, safe tp:a och göra shortrange attacks hela tiden
    //Gör eventuellt en round counter och då efter några rundor så får alla clienter tillgång till en av varje
    //eller något liknande...

    //TODO
    //Fixa att en klient har 10 sekunder på sig att skicka move
    public void run()
    {
            try
            {
                while(true) {
                    if(ServerGameController.GetGameStarted()) {
                        if (PlayerTurnIndex != 0) {
                            if (allClients.get(PlayerTurnIndex - 1).clientNumber == clientNumber) {
                                try {
                                    //Timer timer = new Timer();
                                    //timer.schedule(task, 5 * 1000);

                                    //TODO
                                    //Börjar med guit så blir att enklare.. Titta på vad du skrev med Lukas på facebook
                                    inline = in.readLine();


                                        if (inline.equals("quit")) {
                                            try {
                                                Server.queue.put(new ComMessage("quit", clientNumber));
                                            } catch (InterruptedException e) {
                                                LOGGER.info("Communication queue.put IO:error: " + e.getMessage());
                                            }
                                            socket.close();
                                            allClients.remove(this);
                                            numberOfClients = allClients.size();
                                            break;
                                        }

                                        if (inline.equals("short range attack") && (numberOfShortRangeAttacks <= 0)) {
                                            out.println("You've used all the short range attacks for this level");
                                        } else if (inline.equals("safe teleport") && (numberOfSafeTeleportations <= 0)) {
                                            out.println("You can't safe teleport anymore, either kill an enemy or survive to the next round");
                                        } else {
                                            Server.queue.put(new ComMessage(inline, clientNumber));
                                        }


                                } catch (InterruptedException e) {
                                    LOGGER.info("Communication queue.put IO:error: " + e.getMessage());
                                }
                            }
                        } else {
                            //ServerTurn
                        }
                    }

                    //System.out.println("Recieved from Client " + clientNumber + ": " + inline);
                }

            }
            catch(IOException e)
            {
                LOGGER.info("Could not read from client/close socket: " + e.getMessage());

            }
    }

    public void PrintBoard() {
        if(PlayerTurnIndex != 0) {
            out.println("ROUND: " + ServerGameController.Round + " LEVEL: " + ServerGameController.Level + " PLAYER WITH ID " + allClients.get(PlayerTurnIndex - 1).clientNumber + "'s TURN" + " SAFE TP'S: "
                        + allClients.get(PlayerTurnIndex - 1).numberOfSafeTeleportations + " SRA'S: " + allClients.get(PlayerTurnIndex - 1).numberOfShortRangeAttacks);
            for (int i = 0; i < ServerGameController.Board[0].length; i++) {
                for (int j = 0; j < ServerGameController.Board[1].length; j++) {
                    out.print("  " + ServerGameController.Board[i][j]);
                }
                out.println("");
                //out.println("  " + ServerGameController.Board[i][0] + "  " + ServerGameController.Board[i][1] + "  " + ServerGameController.Board[i][2] + "  " + ServerGameController.Board[i][3] + "  " + ServerGameController.Board[i][4] + "  " + ServerGameController.Board[i][5] );
            }
        }
    }

    public int GetClientId()
    {
        return clientNumber;
    }

    public static void PrintBoardForAllClients()
    {
        Iterator<ServerCommunicationController> it = ServerCommunicationController.allClients.iterator();
        while(it.hasNext())
        {
            ServerCommunicationController SCC = it.next();
            if(SCC != null)
            {
                SCC.PrintBoard();
            }
        }
    }

    public static synchronized void NextPlayerTurn()
    {
       PlayerTurnIndex = (PlayerTurnIndex == numberOfClients) ? 0 : ++PlayerTurnIndex;
    }

    public static synchronized int GetNumberOfPlayers()
    {
        return allClients.size();
    }

    public static synchronized  int GetPlayerTurn()
    {
        return PlayerTurnIndex;
    }

    public static synchronized void SetPlayerTurn(int playerTurnIndex) {PlayerTurnIndex = playerTurnIndex;}

    //TODO
    //Gör något annat än att stänga allt
    //Visa gui hos clienten så att den kan connecta igen
    //Samt att denna tråd stängs ner.
    public static synchronized  void KillClient(int ClientId)
    {
        Iterator<ServerCommunicationController> it = ServerCommunicationController.allClients.iterator();
        while(it.hasNext())
        {
            ServerCommunicationController SCC = it.next();
            if(SCC.clientNumber == ClientId)
            {
                try
                {
                    SCC.out.println("You've been killed");
                    SCC.socket.close();
                    SCC.in.close();
                    SCC.out.close();
                    allClients.remove(SCC);
                    numberOfClients = allClients.size();

                }
                catch(IOException e)
                {
                    LOGGER.info("Could not close socket: " + e.getMessage());

                }

            }
        }
    }

    public static synchronized void SetNumberOfShortRangeAttacks(int amount)
    {
        Iterator<ServerCommunicationController> it = ServerCommunicationController.allClients.iterator();
        while(it.hasNext())
        {
            ServerCommunicationController SCC = it.next();
            SCC.numberOfShortRangeAttacks = amount;
        }
    }

    public static synchronized void DecreaseNumberOfShortRangeAttacks(int ClientId)
    {
        Iterator<ServerCommunicationController> it = ServerCommunicationController.allClients.iterator();
        while(it.hasNext())
        {
            ServerCommunicationController SCC = it.next();
            if(SCC.clientNumber == ClientId)
            {
                SCC.numberOfShortRangeAttacks--;
                break;
            }
        }
    }

    public static synchronized  void SetNumberOfSafeTeleportations(int amount)
    {
        Iterator<ServerCommunicationController> it = ServerCommunicationController.allClients.iterator();
        while(it.hasNext())
        {
            ServerCommunicationController SCC = it.next();
            SCC.numberOfSafeTeleportations = amount;
        }
    }

    public static synchronized void IncreaseNumberOfSafeTeleportations(int ClientId, int amount)
    {
        Iterator<ServerCommunicationController> it = ServerCommunicationController.allClients.iterator();
        while(it.hasNext())
        {
            ServerCommunicationController SCC = it.next();
            if(SCC.clientNumber == ClientId)
            {
                SCC.numberOfSafeTeleportations += amount;
                break;
            }
        }
    }

    public static synchronized void DecreaseNumberOfSafeTeleportations(int ClientId)
    {
        Iterator<ServerCommunicationController> it = ServerCommunicationController.allClients.iterator();
        while(it.hasNext())
        {
            ServerCommunicationController SCC = it.next();
            if(SCC.clientNumber == ClientId)
            {
                SCC.numberOfSafeTeleportations--;
                break;
            }
        }
    }

    public static synchronized  void ResetDeadlines()
    {
        Iterator<ServerCommunicationController> it = ServerCommunicationController.allClients.iterator();
        while(it.hasNext())
        {
            ServerCommunicationController SCC = it.next();
            SCC.deadlineForMoveReached = false;
        }
    }



    public static void RespawnPlayers()
    {
        Iterator<ServerCommunicationController> it = ServerCommunicationController.allClients.iterator();
        while(it.hasNext())
        {
            ServerCommunicationController SCC = it.next();
            try {
                Server.queue.put(new ComMessage("spawn", SCC.clientNumber));
            }
            catch(InterruptedException e)
            {
                LOGGER.info("Error while respawning clients: " + e.getMessage());
            }
        }
    }

    public static synchronized CopyOnWriteArrayList<ServerCommunicationController>  GetAllClients()
    {
        return allClients;
    }


}

