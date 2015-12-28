package tobdyh131;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.TimerTask;

/**
 * Created by Tobias on 2015-11-27.
 */
public class CommunicationThread implements Runnable{
    private static int numberOfClients = 0;
    private static CopyOnWriteArrayList<CommunicationThread> allClients = new CopyOnWriteArrayList<CommunicationThread>();
    private static int PlayerTurnIndex = 1; // If 0, computers turn
    private int clientNumber = ++numberOfClients;

    public String clientUserName;
    private int numberOfSafeTeleportations;
    private int numberOfShortRangeAttacks;

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String inline = "";
    private boolean deadlineForMoveReached = false;

    public boolean playerAlive = true;

    private static Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());



    public CommunicationThread(Socket s) throws IOException {
        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        this.clientUserName = in.readLine();


        out.println("id;" + clientNumber + ";");
        out.println("You're connected too the server");


        allClients.add(this);

        /*try {
            Server.queue.put(new ComMessage("spawn", clientNumber));
        }
        catch(InterruptedException e)
        {
            LOGGER.info("CommunicationThread: " + e.getMessage());
        }*/
    }

    public void run()
    {
            try
            {
                while(playerAlive) {
                    if(GameEngine.GetGameStarted()) {
                        if (PlayerTurnIndex != 0) {
                            if (allClients.get(PlayerTurnIndex - 1).clientNumber == clientNumber) {
                                try {
                                    inline = in.readLine();


                                        if (inline.equals("quit")) {
                                            //Server.queue.put(new ComMessage("quit", this.clientNumber));
                                            int index = 0;
                                            for (CommunicationThread c: allClients
                                                 ) {
                                                if(c.clientNumber == this.clientNumber)
                                                    break;
                                                else
                                                    index++;
                                            }

                                            DisconnectClient(index);
                                            break;

                                        }

                                        if (inline.equals("short range attack") && (numberOfShortRangeAttacks <= 0)) {
                                            out.println("no sra");
                                        } else if (inline.equals("safe teleport") && (numberOfSafeTeleportations <= 0)) {
                                            out.println("no st");
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

                }

            }
            catch(IOException e)
            {
                LOGGER.info("Could not read from client/close socket: " + e.getMessage());

            }
    }

    public void PrintBoard() {
        if(PlayerTurnIndex != 0) {
            out.println("ROUND: " + GameEngine.Round + " LEVEL: " + GameEngine.Level + " PLAYER WITH ID " + allClients.get(PlayerTurnIndex - 1).clientNumber + "'s TURN" + " SAFE TP'S: "
                        + allClients.get(PlayerTurnIndex - 1).numberOfSafeTeleportations + " SRA'S: " + allClients.get(PlayerTurnIndex - 1).numberOfShortRangeAttacks);
            for (int i = 0; i < GameEngine.Board[0].length; i++) {
                for (int j = 0; j < GameEngine.Board[1].length; j++) {
                    out.print("  " + GameEngine.Board[i][j]);
                }
                out.println("");
                //out.println("  " + GameEngine.Board[i][0] + "  " + GameEngine.Board[i][1] + "  " + GameEngine.Board[i][2] + "  " + GameEngine.Board[i][3] + "  " + GameEngine.Board[i][4] + "  " + GameEngine.Board[i][5] );
            }
        }
    }

    public int GetClientId()
    {
        return clientNumber;
    }

    public static void PrintBoardForAllClients()
    {
        Iterator<CommunicationThread> it = CommunicationThread.allClients.iterator();
        while(it.hasNext())
        {
            CommunicationThread SCC = it.next();
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

    public static synchronized void SendToClients(String msg)
    {
        for (CommunicationThread SCC : CommunicationThread.GetAllClients()) {
            SCC.out.println(msg);
        }
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

    public static synchronized  void KillClient(int ClientId)
    {
        Iterator<CommunicationThread> it = CommunicationThread.allClients.iterator();
        while(it.hasNext())
        {
            CommunicationThread SCC = it.next();
            if(SCC.clientNumber == ClientId)
            {
                try
                {
                    SCC.out.println("killed");
                    SCC.socket.close();
                    SCC.in.close();
                    SCC.out.close();
                    allClients.remove(SCC);
                    numberOfClients = allClients.size();
                    SCC.playerAlive = false;
                    if(!allClients.isEmpty())
                        CommunicationThread.SendToClients("levelinfo;" + GameEngine.Round + ";" + GameEngine.Level + ";" + CommunicationThread.GetAllClients().get(CommunicationThread.GetPlayerTurn() - 1).GetClientId() + ";");
                    break;
                }
                catch(IOException e)
                {
                    LOGGER.info("Could not close socket: " + e.getMessage());

                }

            }
        }
    }

    public static synchronized  void DisconnectClient(int index)
    {

            CommunicationThread SCC = allClients.get(index);
            try
            {
                int clientID = allClients.get(index).GetClientId();
                SCC.out.println("disconnected");
                SCC.socket.close();
                SCC.in.close();
                SCC.out.close();
                allClients.remove(SCC);
                numberOfClients = allClients.size();
                SCC.playerAlive = false;
                if(GameEngine.GetGameStarted())
                {
                    GameEngine.RemoveDisconnectedPlayerFromBoard(clientID);
                }


            }
            catch(IOException e)
            {
                LOGGER.info("Could not close socket: " + e.getMessage());

            }

    }

    public static synchronized void SetNumberOfShortRangeAttacks(int amount)
    {
        Iterator<CommunicationThread> it = CommunicationThread.allClients.iterator();
        while(it.hasNext())
        {
            CommunicationThread SCC = it.next();
            SCC.numberOfShortRangeAttacks = amount;
        }
    }

    public static synchronized void DecreaseNumberOfShortRangeAttacks(int ClientId)
    {
        Iterator<CommunicationThread> it = CommunicationThread.allClients.iterator();
        while(it.hasNext())
        {
            CommunicationThread SCC = it.next();
            if(SCC.clientNumber == ClientId)
            {
                SCC.numberOfShortRangeAttacks--;
                break;
            }
        }
    }

    public static synchronized  void SetNumberOfSafeTeleportations(int amount)
    {
        Iterator<CommunicationThread> it = CommunicationThread.allClients.iterator();
        while(it.hasNext())
        {
            CommunicationThread SCC = it.next();
            SCC.numberOfSafeTeleportations = amount;
        }
    }

    public static synchronized void IncreaseNumberOfSafeTeleportations(int ClientId, int amount)
    {
        Iterator<CommunicationThread> it = CommunicationThread.allClients.iterator();
        while(it.hasNext())
        {
            CommunicationThread SCC = it.next();
            if(SCC.clientNumber == ClientId)
            {
                SCC.numberOfSafeTeleportations += amount;
                break;
            }
        }
    }

    public static synchronized void DecreaseNumberOfSafeTeleportations(int ClientId)
    {
        Iterator<CommunicationThread> it = CommunicationThread.allClients.iterator();
        while(it.hasNext())
        {
            CommunicationThread SCC = it.next();
            if(SCC.clientNumber == ClientId)
            {
                SCC.numberOfSafeTeleportations--;
                break;
            }
        }
    }

    public static synchronized  void ResetDeadlines()
    {
        Iterator<CommunicationThread> it = CommunicationThread.allClients.iterator();
        while(it.hasNext())
        {
            CommunicationThread SCC = it.next();
            SCC.deadlineForMoveReached = false;
        }
    }

    public static synchronized  void SendUpdatedBoardToClients(int[][] Board)
    {
        String boardInfo = "board;";

        for (int i = 0; i < Board[0].length; i++) {
            for (int j = 0; j < Board[1].length; j++) {
                if(Board[i][j] == -1)
                {
                    boardInfo += "rubble;"+ i + ";" + j + ";";
                }
                else if(Board[i][j] < -1)
                {
                    boardInfo += "robot;"+ Board[i][j] + ";" + i + ";" + j + ";";
                }
                else if(Board[i][j] > 0)
                {
                    boardInfo += "client;"+ Board[i][j] + ";" + i + ";" + j + ";";
                }
            }
        }

        for (CommunicationThread SCC: allClients) {
            SCC.out.println(boardInfo);
        }
    }


    public static void RespawnPlayers()
    {
        Iterator<CommunicationThread> it = CommunicationThread.allClients.iterator();
        while(it.hasNext())
        {
            CommunicationThread SCC = it.next();
            try {
                Server.queue.put(new ComMessage("spawn", SCC.clientNumber));
            }
            catch(InterruptedException e)
            {
                LOGGER.info("Error while respawning clients: " + e.getMessage());
            }
        }
    }

    public static synchronized CopyOnWriteArrayList<CommunicationThread>  GetAllClients()
    {
        return allClients;
    }

    public static synchronized String GetClientUserName(int ClientID)
    {
        for (CommunicationThread CT: allClients
             ) {

            if(CT.clientNumber == ClientID)
            {
                return CT.clientUserName;
            }

        }

        return "";
    }

    public static synchronized void ResetClientNumbers()
    {
        int newClientNumber = 0;
        Iterator<CommunicationThread> it = CommunicationThread.allClients.iterator();
        while(it.hasNext())
        {
            CommunicationThread SCC = it.next();
            SCC.clientNumber = ++newClientNumber;
            SCC.out.println("id;" + SCC.clientNumber + ";");

        }
    }


}

