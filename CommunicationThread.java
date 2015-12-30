package tobdyh131;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Created by Tobias on 2015-11-27.
 */
public class CommunicationThread implements Runnable{
    private static int numberOfClients = 0;
    private static CopyOnWriteArrayList<CommunicationThread> allClients = new CopyOnWriteArrayList<CommunicationThread>();
    private static int PlayerTurnIndex = 1; // If 0, computers turn
    private int clientNumber = ++numberOfClients;

    public String clientUserName;
    public int clientScore;
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
        this.clientScore = 0;


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

            while(playerAlive) {
                if (GameEngine.GetGameStarted()) {
                    if (PlayerTurnIndex != 0) {
                        try {
                            inline = in.readLine();
                            try {
                                if (inline != null) {
                                    if (inline.equals("quit")) {
                                        Server.queue.put(new ComMessage("quit", this.clientNumber));
                                    /*int index = 0;
                                    for (CommunicationThread c: allClients
                                         ) {
                                        if(c.clientNumber == this.clientNumber)
                                            break;
                                        else
                                            index++;
                                    }

                                    DisconnectClient(index);
                                    break;*/
                                        break;

                                    }

                                    if (inline.equals("short range attack") && (numberOfShortRangeAttacks <= 0)) {
                                        out.println("no sra");
                                    } else if (inline.equals("safe teleport") && (numberOfSafeTeleportations <= 0)) {
                                        out.println("no st");
                                    } else {
                                        Server.queue.put(new ComMessage(inline, clientNumber));
                                    }
                                }
                            } catch (InterruptedException e) {
                                LOGGER.info("Could not put message in the server que: " + e.getMessage());
                            }
                        } catch (IOException e) {
                            LOGGER.info("Could not read from client/close socket: " + e.getMessage());

                        }
                    }
                }

                /*
                else
                {
                    try{
                        inline = in.readLine();

                        try
                        {
                            if(inline != null) {
                                if (inline.equals("quit"))
                                    Server.queue.put(new ComMessage("quit", this.clientNumber));
                            }
                        }
                        catch(InterruptedException e)
                        {
                            LOGGER.info("Could not put message in the server que: " + e.getMessage());
                        }

                    }
                    catch(IOException e)
                    {
                        LOGGER.info("Could not read from client/close socket: " + e.getMessage());
                    }


                }
                */
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

    public void SendMessageOnDeath(String ReasonForDeath)
    {
        ArrayList<HighscoreInfo> highScoreList = LoadHighScore();

        if(highScoreList != null)
        {
            highScoreList.add(new HighscoreInfo(this.clientUserName, this.clientScore));
            if(highScoreList.size() > 10)
            {
                Collections.sort(highScoreList, new Comparator<HighscoreInfo>(){
                    @Override public int compare(HighscoreInfo h1, HighscoreInfo h2)
                    {
                        return h2.score - h1.score;
                    }
                });

                highScoreList.remove(10);
            }
            else
            {
                Collections.sort(highScoreList, new Comparator<HighscoreInfo>(){
                    @Override public int compare(HighscoreInfo h1, HighscoreInfo h2)
                    {
                        return h2.score - h1.score;
                    }
                });
            }
        }

        String highscoreInfoLine = "";
        for (HighscoreInfo hsi: highScoreList
             ) {
            highscoreInfoLine += (hsi.userName + ";" + hsi.score + ";");

        }

        out.println(ReasonForDeath + ";" + highscoreInfoLine);

        SaveHighscore(highscoreInfoLine, highScoreList.size());


    }

    public ArrayList<HighscoreInfo> LoadHighScore()
    {
        ArrayList<HighscoreInfo> highScoreList = new ArrayList<HighscoreInfo>();
        String fileName = "C:\\Users\\Tobias\\IdeaProjects\\RobotProject_v3\\src\\tobdyh131\\highscore.txt";
        String line = null;

        try {
            FileReader fileReader =
                    new FileReader(fileName);

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                String[] splitLine = line.split(";");
                HighscoreInfo hsi = new HighscoreInfo(splitLine[0], Integer.valueOf(splitLine[1]));
                highScoreList.add(hsi);

            }


            bufferedReader.close();

            Collections.sort(highScoreList, new Comparator<HighscoreInfo>(){
                @Override public int compare(HighscoreInfo h1, HighscoreInfo h2)
                {
                    return h2.score - h1.score;
                }
            });

            return highScoreList;


        }
        catch(FileNotFoundException ex) {
            LOGGER.info(
                    "Unable to open file '" +
                            fileName + "'\n");
            return null;
        }
        catch(IOException ex) {
            LOGGER.info(
                    "Error reading file '"
                            + fileName + "'\n");
            return  null;
        }
    }


    public void SaveHighscore(String highscoreInfoLine, int highscoreLength)
    {
        String fileName = "C:\\Users\\Tobias\\IdeaProjects\\RobotProject_v3\\src\\tobdyh131\\highscore.txt";
        try {
            FileWriter fileWriter =
                    new FileWriter(fileName);

            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            String[] splitLine = highscoreInfoLine.split(";");

            for (int i = 0; i < highscoreLength * 2; i += 2) {
                 bufferedWriter.write(splitLine[i] + ";" + splitLine[i+1] + ";" + "\n");
            }


            bufferedWriter.close();
        }
        catch(IOException ex) {
            LOGGER.info(
                    "Error writing to file '"
                            + fileName + "'\n");

        }

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
                    int index = allClients.indexOf(SCC);
                    //if removing client last in list, and it's their turn then its the computers turn
                    if((index == allClients.size() - 1) && (GetPlayerTurn() - 1 == index))
                    {
                        NextPlayerTurn();
                    }
                    else if(GetPlayerTurn() - 1 > index)
                    {
                        DecreasePlayerTurn();
                    }

                    SCC.SendMessageOnDeath("killed");
                    SCC.socket.close();
                    SCC.in.close();
                    SCC.out.close();
                    allClients.remove(SCC);
                    numberOfClients = allClients.size();
                    SCC.playerAlive = false;
                    if(!allClients.isEmpty() && (CommunicationThread.GetPlayerTurn() > 0))
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
                if(GameEngine.GetGameStarted()) {
                    //if removing client last in list, and it's their turn then its the computers turn
                    if ((index == allClients.size() - 1) && (GetPlayerTurn() - 1 == index)) {
                        NextPlayerTurn();
                    } else if (GetPlayerTurn() - 1 > index) {
                        DecreasePlayerTurn();
                    }
                }
                int clientID = allClients.get(index).GetClientId();
                SCC.SendMessageOnDeath("disconnected");
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

    public static synchronized void DecreasePlayerTurn()
    {
        PlayerTurnIndex--;
    }

    public static synchronized void IncreaseScoreOfClient(int ClientID, int scoreIncrement)
    {
        for (CommunicationThread CT: allClients
             ) {
            if(CT.GetClientId() == ClientID)
            {
                CT.clientScore += scoreIncrement;
            }
        }
    }

    public static synchronized  int GetClientIndex(int ClientId)
    {
        int index = 0;
        for (CommunicationThread CT: allClients
             ) {
            if(CT.GetClientId() == ClientId)
            {
                return index;
            }
            else
            {
                index++;
            }
        }
        return index;
    }


}

