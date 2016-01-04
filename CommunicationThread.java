package tobdyh131;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Created by Tobias on 2015-11-27.
 *
 * The class which takes input from the user through the socket.
 * Works as a bridge between the server and client.
 * Receives from the user, and passes messages to the server.
 */
class CommunicationThread implements Runnable{
    private static int numberOfClients = 0;
    private static final CopyOnWriteArrayList<CommunicationThread> allClients = new CopyOnWriteArrayList<>();
    private static int PlayerTurnIndex = 1; // If 0, computers turn
    private int clientNumber = ++numberOfClients;

    public String clientUserName;
    private int clientScore;
    private int numberOfSafeTeleportations;
    private int numberOfShortRangeAttacks;

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private boolean deadlineForMoveReached = false;

    private boolean playerAlive = true;

    private static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());


    /**
     * The constructor that creates the communication thread and
     * enables communication between the server and the client through a given socket.
     * @param s The socket
     * @throws IOException Exception thrown when handling sockets
     */
    public CommunicationThread(Socket s) throws IOException {
        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        String username = in.readLine();
        //this.clientUserName = in.readLine();
        this.clientScore = 0;

        boolean userNameExists = false;
        for (CommunicationThread CT: allClients
             ) {
            if(CT.clientUserName.equals(username))
            {
                AssignNewUsername(username);
                userNameExists = true;
                break;
            }
        }

        if(!userNameExists)
            clientUserName = username;



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

    /**
     * Assigns a new username to the client if it's alread taken
     * @param userName Username that was already taken
     */
    private void AssignNewUsername(String userName)
    {
        String changedUsername;
        boolean notTaken = false;
        int numberOfRepetitions = 1;

        while(!notTaken)
        {
            changedUsername = userName + "(" + numberOfRepetitions + ")";
            if(!UsernameTaken(changedUsername))
            {
                notTaken = true;
                clientUserName = changedUsername;
            }
            numberOfRepetitions++;
        }
    }

    /**
     * Checks if the username is already taken
     * @param username The username to check
     * @return
     */
    private boolean UsernameTaken(String username)
    {
        boolean userNameExists = false;
        for (CommunicationThread CT: allClients
                ) {
            if(CT.clientUserName.equals(username))
            {
                AssignNewUsername(username);
                userNameExists = true;
                break;
            }
        }

        return userNameExists;
    }

    /**
     * The function which is continuously executed during runtime.
     * If the player is still alive, the game has started and its not the
     * computers turn, this method waits for user input and then passes
     * the information received further to the server which handles matter.
     */
    public void run()
    {

            while(playerAlive) {
                if (GameEngine.GetGameStarted()) {
                    if (PlayerTurnIndex != 0) {
                        try {
                            String inline = in.readLine();
                            try {
                                if (inline != null) {
                                    if (inline.equals("quit")) {
                                        Server.queue.put(new ComMessage("quit", this.clientNumber));
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

    /**
     * A method that was used before the GUI was implemented.
     * Prints the board in the console window for a user.
     */
    private void PrintBoard() {
        if(PlayerTurnIndex != 0) {
            out.println("ROUND: " + GameEngine.Round + " LEVEL: " + GameEngine.Level + " PLAYER WITH ID " + allClients.get(PlayerTurnIndex - 1).clientNumber + "'s TURN" + " SAFE TP'S: "
                        + allClients.get(PlayerTurnIndex - 1).numberOfSafeTeleportations + " SRA'S: " + allClients.get(PlayerTurnIndex - 1).numberOfShortRangeAttacks);
            for (int i = 0; i < GameEngine.Board.length; i++) {
                for (int j = 0; j < GameEngine.Board[1].length; j++) {
                    out.print("  " + GameEngine.Board[i][j]);
                }
                out.println("");
            }
        }
    }

    /**
     *
     * @return The clients id set from start
     */
    public int GetClientId()
    {
        return clientNumber;
    }

    /**
     * Sends a message to the user that the game has ended for their part.
     * Passes the current high score list which is handled and displayed on the "client side".
     * @param ReasonForDeath The reason why the client has died, either killed by a robot or disconnected
     */
    private void SendMessageOnDeath(String ReasonForDeath)
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

    /**
     * Loads the current high score list from a specific file.
     * @return The high score list
     */
    private ArrayList<HighscoreInfo> LoadHighScore()
    {
        ArrayList<HighscoreInfo> highScoreList = new ArrayList<>();

        try {
            File file = new File(getClass().getResource("highscore.txt").toURI());
            //"C:\\Users\\Tobias\\IdeaProjects\\RobotProject_v3\\src\\tobdyh131\\highscore.txt";
            String line;

            try {
                FileReader fileReader =
                        new FileReader(file);

                BufferedReader bufferedReader =
                        new BufferedReader(fileReader);

                while ((line = bufferedReader.readLine()) != null) {
                    String[] splitLine = line.split(";");
                    HighscoreInfo hsi = new HighscoreInfo(splitLine[0], Integer.valueOf(splitLine[1]));
                    highScoreList.add(hsi);

                }


                bufferedReader.close();

                Collections.sort(highScoreList, new Comparator<HighscoreInfo>() {
                    @Override
                    public int compare(HighscoreInfo h1, HighscoreInfo h2) {
                        return h2.score - h1.score;
                    }
                });

                return highScoreList;


            } catch (FileNotFoundException ex) {
                LOGGER.info(
                        "Unable to open file '" +
                                file.toString() + "'\n");
                return null;
            } catch (IOException ex) {
                LOGGER.info(
                        "Error reading file '"
                                + file.toString() + "'\n");
                return null;
            }
        }
        catch(URISyntaxException e)
        {
            LOGGER.info(e.getMessage());
            return null;
        }
    }


    /**
     * Saves the current hich score list to a specific file.
     * @param highscoreInfoLine The line, which contain all information about the high score list, which is saved to the file.
     * @param highscoreLength The length of the high score list (How many users is in the list)
     */
    private void SaveHighscore(String highscoreInfoLine, int highscoreLength)
    {

        try {
            File file = new File(getClass().getResource("highscore.txt").toURI());
            //"C:\\Users\\Tobias\\IdeaProjects\\RobotProject_v3\\src\\tobdyh131\\highscore.txt";
            try {
                FileWriter fileWriter =
                        new FileWriter(file);

                BufferedWriter bufferedWriter =
                        new BufferedWriter(fileWriter);

                String[] splitLine = highscoreInfoLine.split(";");

                for (int i = 0; i < highscoreLength * 2; i += 2) {
                    bufferedWriter.write(splitLine[i] + ";" + splitLine[i + 1] + ";" + "\n");
                }


                bufferedWriter.close();
            } catch (IOException ex) {
                LOGGER.info(
                        "Error writing to file '"
                                + file.toString() + "'\n");

            }
        }
        catch(URISyntaxException e)
        {
            LOGGER.info(
                    "URI Syntax error: " + e.getMessage());
        }

    }

    /**
     * A method that was used before the GUI was implemented.
     * Prints the board in the console window for all users.
     */
    public static void PrintBoardForAllClients()
    {
        for (CommunicationThread SCC : CommunicationThread.allClients) {
            if (SCC != null) {
                SCC.PrintBoard();
            }
        }
    }

    /**
     * Sends a message to a specific client
     * @param ClientId Id of the Client
     * @param msg Message to be sent
     */
    public static void SendMessageToClient(int ClientId, String msg)
    {
        CommunicationThread.GetAllClients().stream().filter(SCC -> SCC.GetClientId() == ClientId).forEach(SCC -> {
            SCC.out.println(msg);
        });
    }


    /**
     * Makes it the next players turns
     */
    public static synchronized void NextPlayerTurn()
    {
       PlayerTurnIndex = (PlayerTurnIndex == numberOfClients) ? 0 : ++PlayerTurnIndex;
    }

    /**
     * A method that is used by the server to send a message to all the clients.
     * @param msg Message to be sent
     */
    public static synchronized void SendToClients(String msg)
    {
        for (CommunicationThread SCC : CommunicationThread.GetAllClients()) {
            SCC.out.println(msg);
        }
    }

    /**
     *
     * @return The number of players connected to the game.
     */
    public static synchronized int GetNumberOfPlayers()
    {
        return allClients.size();
    }

    /**
     *
     * @return Which players turn it is.
     */
    public static synchronized  int GetPlayerTurn()
    {
        return PlayerTurnIndex;
    }

    /**
     * Sets which players turn it should be.
     * @param playerTurnIndex Which players turn it should be.
     */
    public static synchronized void SetPlayerTurn(int playerTurnIndex) {PlayerTurnIndex = playerTurnIndex;}

    /**
     * Kills a client
     * - Sends message about dying to the client
     * - Closes the socket, in, and out
     * - Removes the client from the list
     * - Sets the player status to dead
     * - Send information to the remaining clients
     * @param ClientId Id of the client to be killed.
     */
    public static synchronized  void KillClient(int ClientId)
    {
        for (CommunicationThread SCC : CommunicationThread.allClients) {
            if (SCC.clientNumber == ClientId) {
                try {
                    int index = allClients.indexOf(SCC);
                    //if removing client last in list, and it's their turn then its the computers turn
                    if ((index == allClients.size() - 1) && (GetPlayerTurn() - 1 == index)) {
                        NextPlayerTurn();
                    } else if (GetPlayerTurn() - 1 > index) {
                        DecreasePlayerTurn();
                    }

                    SCC.SendMessageOnDeath("killed");
                    SCC.socket.close();
                    SCC.in.close();
                    SCC.out.close();
                    allClients.remove(SCC);
                    numberOfClients = allClients.size();
                    SCC.playerAlive = false;
                    if (!allClients.isEmpty() && (CommunicationThread.GetPlayerTurn() > 0))
                        CommunicationThread.SendToClients("levelinfo;" + GameEngine.Round + ";" + GameEngine.Level + ";" + CommunicationThread.GetAllClients().get(CommunicationThread.GetPlayerTurn() - 1).GetClientId() + ";");
                    break;
                } catch (IOException e) {
                    LOGGER.info("Could not close socket: " + e.getMessage());

                }

            }
        }
    }

    /**
     * Disconnects a client
     * - Sends message about disconnecting to the client
     * - Closes the socket, in, and out
     * - Removes the client from the list
     * - Sets the player status to dead
     * - Send information to the remaining clients
     * - Removes the client from the board
     * @param index Index of the client to be disconnected
     */
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

    /**
     * Sets the number of short range attacks for all the clients.
     * @param amount Number of short range attacks.
     */
    public static synchronized void SetNumberOfShortRangeAttacks(int amount)
    {
        for (CommunicationThread SCC : CommunicationThread.allClients) {
            SCC.numberOfShortRangeAttacks = amount;
        }
    }

    /**
     * Decreases the amount of short range attacks for a certain client by one.
     * @param ClientId Id of the client who's short range attacks is to be decreased.
     */
    public static synchronized void DecreaseNumberOfShortRangeAttacks(int ClientId)
    {
        for (CommunicationThread SCC : CommunicationThread.allClients) {
            if (SCC.clientNumber == ClientId) {
                SCC.numberOfShortRangeAttacks--;
                break;
            }
        }
    }

    /**
     * Sets the number of safe teleportations for all the clients.
     * @param amount Number of safe teleportations.
     */
    public static synchronized  void SetNumberOfSafeTeleportations(int amount)
    {
        for (CommunicationThread SCC : CommunicationThread.allClients) {
            SCC.numberOfSafeTeleportations = amount;
        }
    }

    /**
     * Increase the amount of safe teleportations for a certain client by one.
     * @param ClientId Id of the client who's safe teleportations is to be increased.
     * @param amount The amount to increase the safe teleportations.
     */
    public static synchronized void IncreaseNumberOfSafeTeleportations(int ClientId, int amount)
    {
        for (CommunicationThread SCC : CommunicationThread.allClients) {
            if (SCC.clientNumber == ClientId) {
                SCC.numberOfSafeTeleportations += amount;
                break;
            }
        }
    }

    /**
     * Decrease the amount of safe teleportations for a certain client by one.
     * @param ClientId Id of the client who's safe teleportations is to be decreased.
     */
    public static synchronized void DecreaseNumberOfSafeTeleportations(int ClientId)
    {
        for (CommunicationThread SCC : CommunicationThread.allClients) {
            if (SCC.clientNumber == ClientId) {
                SCC.numberOfSafeTeleportations--;
                break;
            }
        }
    }

    /**
     * Sends an updated version of the board to all clients.
     * @param Board An updated version of the board.
     */
    public static synchronized  void SendUpdatedBoardToClients(int[][] Board)
    {
        String boardInfo = "board;";

        for (int i = 0; i < Board.length; i++) {
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

    /**
     * Respawns the all the alive players after a level has been completed.
     */
    public static void RespawnPlayers()
    {
        for (CommunicationThread SCC : CommunicationThread.allClients) {
            try {
                Server.queue.put(new ComMessage("spawn", SCC.clientNumber));
            } catch (InterruptedException e) {
                LOGGER.info("Error while respawning clients: " + e.getMessage());
            }
        }
    }

    /**
     *
     * @return The list of all clients
     */
    public static synchronized CopyOnWriteArrayList<CommunicationThread>  GetAllClients()
    {
        return allClients;
    }

    /**
     *
     * @param ClientID Id of the client
     * @return The user name of the client
     */
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

    /**
     * Resets the current client numbers.
     * This is used when a client disconnects during the connection phase.
     * Then all the numbers/id for the clients are reset.
     */
    public static synchronized void ResetClientNumbers()
    {
        int newClientNumber = 0;
        for (CommunicationThread SCC : CommunicationThread.allClients) {
            SCC.clientNumber = ++newClientNumber;
            SCC.out.println("id;" + SCC.clientNumber + ";");

        }
    }

    /**
     * Sets the player turn index one step back.
     */
    private static synchronized void DecreasePlayerTurn()
    {
        PlayerTurnIndex--;
    }

    /**
     * Increases the score of a certain client.
     * @param ClientID Id of the client.
     * @param scoreIncrement Amount of points to be added to the clients score.
     */
    public static synchronized void IncreaseScoreOfClient(int ClientID, int scoreIncrement)
    {
        allClients.stream().filter(CT -> CT.GetClientId() == ClientID).forEach(CT -> {
            CT.clientScore += scoreIncrement;
        });
    }

    /**
     *
     * @param ClientId Id of the client.
     * @return The index of the client in the list which has the given id.
     */
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

