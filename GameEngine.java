package tobdyh131;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Tobias on 2015-11-27.
 *
 * This is the class that handles all the logic behind the game.
 * Controls everything that happens on the board, if a player or computer moves,
 * if a level or round is over etc.
 */
class GameEngine implements Runnable {
    public static final BlockingQueue<ComMessage> queue = new LinkedBlockingQueue<>();
    public static int Board[][];
    public static int Round;
    public static int Level;
    private static boolean gameStarted = false;
    private static boolean SessionOngoing = true;

    private ArrayList<Robot> robotList;

    private final Object controller;



    // 0 = Empty space
    // 0< = Clienter
    // -1 = Rubble
    // -1> = Robots

    private ServerSettings Settings;

    /**
     * The constructor that "creates the game".
     * @param s Settings of the game.
     * @param controller The controller of the GUI which is displayed for the server/admin during playing.
     */
    public GameEngine(ServerSettings s, Object controller)
    {
        this.controller = controller;
        CreateLevel(s);
        gameStarted = true;
        Level = 1;
        Round = 1;
        CommunicationThread.SetPlayerTurn(1);
        ((ServerPlayingController) controller).CreateScoreBoard(CommunicationThread.GetAllClients());

    }

    /**
     * Creates a new level.
     * @param s Settings of the current level.
     */
    private void CreateLevel(ServerSettings s)
    {
        Settings = s;
        Board = new int[s.width][s.height];
        PlaceRubbleOnMap(s.numberOfRubbles);
        CreateAndPlaceRobots(s.numberOfRobots);
        CommunicationThread.RespawnPlayers();
        Round = 1;

        if(Settings.robotsLockToTarget) {
            AssignRobotsToClients();
        }
        CommunicationThread.SetNumberOfShortRangeAttacks(Settings.numberOfShortRangeAttacksAwardedPerLevel);
        CommunicationThread.SetNumberOfSafeTeleportations(Settings.numberOfSafeTeleportationsAwardedPerLevel);

        CommunicationThread.SendUpdatedBoardToClients(Board);
        if(CommunicationThread.GetPlayerTurn() > 0 && (CommunicationThread.GetAllClients().size() > 0)) {
            CommunicationThread.SendToClients("levelinfo;" + Round + ";" + Level + ";" + CommunicationThread.GetAllClients().get(CommunicationThread.GetPlayerTurn() - 1).GetClientId() + ";");
            ((ServerPlayingController) controller).SetLevelInformation(Round, Level,CommunicationThread.GetAllClients().get(CommunicationThread.GetPlayerTurn() - 1).GetClientId());
            ((ServerPlayingController) controller).UpdateBoard(Board);
        }


    }


    /**
     * The function which is continuously executed during runtime.
     * If the game is started, first checks if there's players alive, if not then it terminates.
     * But if there's players alive it keeps checking the queue for incoming messages, and if a message
     * has arrived it does the correct action based on what information it has received.
     */
    public void run()
    {

            while (gameStarted) {
                if(CommunicationThread.GetAllClients().isEmpty())
                {
                    ((ServerPlayingController) controller).GoToStartup();
                    SessionOngoing = false;
                    gameStarted = false;
                    break;
                }
                ComMessage msg;
                while ((msg = queue.poll()) != null) {
                    if (msg.Message.equals("spawn")) {
                        int position[] = RandomPosition();
                        Board[position[0]][position[1]] = msg.ClientId;
                    } else if (msg.Message.equals("quit")) {
                        ((ServerPlayingController) controller).RemoveFromScoreBoard(msg.ClientId);
                        DisconnectClient(msg.ClientId);
                        ((ServerPlayingController) controller).WriteToTextArea("Client " + msg.ClientId + " disconnected");
                    } else if (IsMoveCommand(msg.Message)) {
                        if (CanMoveToPosition(msg.Message, msg.ClientId)) {
                            Move(msg.ClientId, msg.Message);
                            CommunicationThread.NextPlayerTurn();
                        } else {
                            CommunicationThread.SendMessageToClient(msg.ClientId,"wrong move");
                        }


                    } else if (msg.Message.equals("random teleport")) {
                        RandomTeleport(msg.ClientId);
                        ((ServerPlayingController) controller).WriteToTextArea("Client " + msg.ClientId + " did a random teleport");
                        CommunicationThread.NextPlayerTurn();
                    } else if (msg.Message.equals("safe teleport")) {
                        SafeTeleport(msg.ClientId);
                        ((ServerPlayingController) controller).WriteToTextArea("Client " + msg.ClientId + " did a safe teleport");
                        CommunicationThread.DecreaseNumberOfSafeTeleportations(msg.ClientId);
                        CommunicationThread.NextPlayerTurn();
                    } else if (msg.Message.equals("short range attack")) {
                        ShortRangeAttack(msg.ClientId);
                        ((ServerPlayingController) controller).WriteToTextArea("Client " + msg.ClientId + " did a short range attack");
                        CommunicationThread.DecreaseNumberOfShortRangeAttacks(msg.ClientId);
                        CommunicationThread.NextPlayerTurn();
                    } else if (msg.Message.equals("wait")) {
                        ((ServerPlayingController) controller).WriteToTextArea("Client " + msg.ClientId + " waited");
                        CommunicationThread.NextPlayerTurn();
                    } else {
                        System.out.println("Wrong input");
                    }

                    //CommunicationThread.PrintBoardForAllClients();
                    if(CommunicationThread.GetPlayerTurn() > 0 && (CommunicationThread.GetAllClients().size() > 0)) {
                        CommunicationThread.SendToClients("levelinfo;" + Round + ";" + Level + ";" + CommunicationThread.GetAllClients().get(CommunicationThread.GetPlayerTurn() - 1).GetClientId() + ";");
                        ((ServerPlayingController) controller).UpdateBoard(Board);
                        ((ServerPlayingController) controller).SetLevelInformation(Round, Level,CommunicationThread.GetAllClients().get(CommunicationThread.GetPlayerTurn() - 1).GetClientId());
                    }

                    CommunicationThread.SendUpdatedBoardToClients(Board);
                }

                if (CommunicationThread.GetPlayerTurn() == 0) {
                    if (!robotList.isEmpty()) {
                        if (!Settings.robotsLockToTarget) {
                            MoveRobotsToClosestClient();
                        } else {
                            MoveRobotsToTarget();
                        }
                    }

                    CommunicationThread.NextPlayerTurn();
                    Round++;
                    //CommunicationThread.PrintBoardForAllClients();
                    CommunicationThread.SendUpdatedBoardToClients(Board);
                    if(CommunicationThread.GetPlayerTurn() > 0 && (CommunicationThread.GetAllClients().size() > 0)) {
                        CommunicationThread.SendToClients("levelinfo;" + Round + ";" + Level + ";" + CommunicationThread.GetAllClients().get(CommunicationThread.GetPlayerTurn() - 1).GetClientId() + ";");
                        ((ServerPlayingController) controller).SetLevelInformation(Round, Level,CommunicationThread.GetAllClients().get(CommunicationThread.GetPlayerTurn() - 1).GetClientId());
                        ((ServerPlayingController) controller).UpdateBoard(Board);
                    }


                }

                if (Round > Settings.numberOfRoundsPerLevel) {
                    NextLevel();
                }

            }

    }

    /**
     * This method moves a client based on the arguments.
     * @param ClientId Id of the client to be moved.
     * @param msg The "move message", that is, what move is to be performed.
     */
    private void Move(int ClientId, String msg)
    {
        int[] currentPosition = GetClientOrRobotPosition(ClientId);
        Board[currentPosition[0]][currentPosition[1]] = 0;

        switch(msg){
            case "move right":  Board[currentPosition[0]][currentPosition[1] + 1] = ClientId;
                                ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientId + " moved right");
                                break;
            case "move left":   Board[currentPosition[0]][currentPosition[1] - 1] = ClientId;
                                ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientId + " moved left");
                                break;
            case "move up":     Board[currentPosition[0] - 1][currentPosition[1]] = ClientId;
                                ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientId + " moved up");
                                break;
            case "move down":   Board[currentPosition[0] + 1][currentPosition[1]] = ClientId;
                                ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientId + " moved down");
                                break;
            case "move up right":   Board[currentPosition[0] - 1][currentPosition[1] + 1] = ClientId;
                                    ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientId + " moved up right");
                                    break;
            case "move down right": Board[currentPosition[0] + 1][currentPosition[1] + 1] = ClientId;
                                    ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientId + " down up");
                                    break;
            case "move up left":    Board[currentPosition[0] - 1][currentPosition[1] - 1] = ClientId;
                                    ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientId + " moved up left");
                                    break;
            case "move down left":  Board[currentPosition[0] + 1][currentPosition[1] - 1] = ClientId;
                                    ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientId + " moved down left");
                                    break;
        }
    }

    /**
     * Moves all the robots to their current closest client.
     */
    private void MoveRobotsToClosestClient()
    {

        ArrayList<int[]> clientPositions = new ArrayList<>();

        for (int i = 0; i < Board.length; i++) {
            for (int j = 0; j < Board[1].length; j++) {
                if(Board[i][j] > 0)
                {
                    int[] clientPos = new int[2];
                    clientPos[0] = i;
                    clientPos[1] = j;
                    clientPositions.add(clientPos);
                }
            }
        }

        if(!clientPositions.isEmpty()) {
            for (Robot aRobotList : robotList) {
                int closestClient = -1;

                //Find closest client
                for (int j = 0; j < clientPositions.size(); j++) {
                    int rowDifference;
                    if (aRobotList.position[0] > clientPositions.get(j)[0])
                        rowDifference = aRobotList.position[0] - clientPositions.get(j)[0];
                    else
                        rowDifference = clientPositions.get(j)[0] - aRobotList.position[0];

                    int columnDifference;
                    if (aRobotList.position[1] > clientPositions.get(j)[1])
                        columnDifference = aRobotList.position[1] - clientPositions.get(j)[1];
                    else
                        columnDifference = clientPositions.get(j)[1] - aRobotList.position[1];

                    //Compares to the current closest clients
                    if (closestClient != -1) {
                        int currentClosestClientRowDifference;
                        if (aRobotList.position[0] > clientPositions.get(closestClient)[0])
                            currentClosestClientRowDifference = aRobotList.position[0] - clientPositions.get(closestClient)[0];
                        else
                            currentClosestClientRowDifference = clientPositions.get(j)[0] - robotList.get(closestClient).position[0];

                        int currentClosestClientColumnDifference;
                        if (aRobotList.position[1] > clientPositions.get(closestClient)[1])
                            currentClosestClientColumnDifference = aRobotList.position[1] - clientPositions.get(closestClient)[1];
                        else
                            currentClosestClientColumnDifference = clientPositions.get(closestClient)[1] - aRobotList.position[1];
                        if ((rowDifference <= Settings.robotPerceptionRowRange) && (columnDifference <= Settings.robotPerceptionColumnRange)) {
                            if ((rowDifference + columnDifference) < (currentClosestClientRowDifference + currentClosestClientColumnDifference)) {
                                closestClient = j;
                            }
                        }
                    } else {
                        if ((rowDifference <= Settings.robotPerceptionRowRange) && (columnDifference <= Settings.robotPerceptionColumnRange)) {
                            closestClient = j;
                        }
                    }


                }


                //If the robot has found any close clients
                if (closestClient != -1) {
                    //Move towards closest client
                    Board[aRobotList.position[0]][aRobotList.position[1]] = 0;
                    if (aRobotList.position[0] > clientPositions.get(closestClient)[0]) {
                        aRobotList.position[0] -= 1;
                    } else if (aRobotList.position[0] < clientPositions.get(closestClient)[0]) {
                        aRobotList.position[0] += 1;
                    }

                    if (aRobotList.position[1] > clientPositions.get(closestClient)[1]) {
                        aRobotList.position[1] -= 1;
                    } else if (aRobotList.position[1] < clientPositions.get(closestClient)[1]) {
                        aRobotList.position[1] += 1;
                    }
                    aRobotList.hasMoved = true;
                } else {
                    aRobotList.hasMoved = false;
                }


            }

            CheckAndUpdateWhereRobotsMoved();

        }
    }

    /**
     * Moves all robots to their given target client.
     */
    private void MoveRobotsToTarget()
    {
        for(Robot r : robotList) {
            Board[r.position[0]][r.position[1]] = 0;
            if (r.position[0] > GetClientOrRobotPosition(r.lockedToClientID)[0]) {
                r.position[0] -= 1;
            } else if (r.position[0] < GetClientOrRobotPosition(r.lockedToClientID)[0]) {
                r.position[0] += 1;
            }

            if (r.position[1] > GetClientOrRobotPosition(r.lockedToClientID)[1]) {
                r.position[1] -= 1;
            } else if (r.position[1] < GetClientOrRobotPosition(r.lockedToClientID)[1]) {
                r.position[1] += 1;
            }
            r.hasMoved = true;
        }


        CheckAndUpdateWhereRobotsMoved();

    }

    /**
     * Checks where all the robots has moved, and does the correct actions based
     * on what's on their new position on the board. If it's a client on their new position,
     * they kill that client, if it's a piece of rubble the robot dies, if there's another robot
     * on that position they either merge or become a rubble (depending on the server settings), or if
     * it's empty then nothing happens.
     */
    private void CheckAndUpdateWhereRobotsMoved()
    {
        boolean LastChecked = false;

        for(Robot r : robotList)
        {
            r.hasBeenChecked = false;
        }

        while(!LastChecked)
        {
            if(!robotList.isEmpty()) {
                for (Robot r : robotList) {
                    if (!r.hasBeenChecked) {
                        if (r.hasMoved) {
                            if (Board[r.position[0]][r.position[1]] > 0) {
                                CommunicationThread.KillClient(Board[r.position[0]][r.position[1]]);

                                int clientId = Board[r.position[0]][r.position[1]];

                                Board[r.position[0]][r.position[1]] = r.ID;
                                r.hasBeenChecked = true;
                                r.hasMoved = false;

                                if(Settings.robotsLockToTarget)
                                {
                                    //Reset so robots who followed the dead client is now not following anyone
                                    robotList.stream().filter(robot -> robot.lockedToClientID == clientId).forEach(robot -> {
                                        robot.lockedToClientID = -1;
                                    });

                                    //Set so that the mentioned robots follow the one with the least locks on
                                    for(Robot robot : robotList)
                                    {
                                        robot.lockedToClientID = GetClientIdWithLeastLocks();
                                    }
                                }

                                break;
                            } else if (Board[r.position[0]][r.position[1]] == -1) {
                                KillRobot(r.ID);
                                r.hasBeenChecked = true;
                                r.hasMoved = false;

                                break;
                            } else if (Board[r.position[0]][r.position[1]] < -1) {
                                if (Settings.robotsMergeOnCollision) {
                                    KillRobot(Board[r.position[0]][r.position[1]]);
                                    Board[r.position[0]][r.position[1]] = r.ID;
                                    r.hasBeenChecked = true;
                                    r.hasMoved = false;

                                    break;

                                } else {
                                    KillRobot(Board[r.position[0]][r.position[1]]);
                                    Board[r.position[0]][r.position[1]] = -1;
                                    KillRobot(r.ID);
                                    r.hasBeenChecked = true;
                                    r.hasMoved = false;
                                    break;
                                }
                            } else {
                                Board[r.position[0]][r.position[1]] = r.ID;
                                r.hasBeenChecked = true;
                                r.hasMoved = false;
                            }

                            r.hasMoved = false;
                        }
                    }
                    if (robotList.indexOf(r) == (robotList.size() - 1)) {
                        LastChecked = true;
                    }
                }
            }

            else
            {
                LastChecked = true;
            }

        }
    }

    /**
     * Assign all the robots to clients to chase.
     */
    private void AssignRobotsToClients()
    {
        List<CommunicationThread> allClients = CommunicationThread.GetAllClients();
        int index = 0;


        for(Robot r : robotList)
        {
            r.lockedToClientID = allClients.get(index).GetClientId();
            if(index == allClients.size() - 1)
            {
                index = 0;
            }
            else
            {
                index++;
            }
        }


    }

    /**
     *
     * @return The id of the client whom got the least number of robots chasing it.
     */
    private int GetClientIdWithLeastLocks()
    {
        List<ClientToLocksEntry> CTLE = new ArrayList<>();
        List<CommunicationThread> allClients = CommunicationThread.GetAllClients();

        if(!allClients.isEmpty()) {
            for (Robot r : robotList) {
                if (r.lockedToClientID != -1) {
                    if (CTLE.isEmpty()) {
                        CTLE.add(new ClientToLocksEntry(r.lockedToClientID, 1));
                    } else {
                        boolean newEntry = true;
                        for (ClientToLocksEntry c : CTLE) {
                            if (c.ClientId == r.lockedToClientID) {
                                CTLE.get(CTLE.indexOf(c)).NumberOfLocks++;
                                newEntry = false;
                                break;
                            }
                        }

                        if (newEntry) {
                            CTLE.add(new ClientToLocksEntry(r.lockedToClientID, 1));
                        }
                    }
                }
            }

            Collections.sort(CTLE);
            return CTLE.get(0).ClientId;
        }

        return -1;

    }

    /**
     * Disconnects a certain client.
     * @param ClientId Id of the client to disconnect.
     */
    private void DisconnectClient(int ClientId)
    {
        int[] position = GetClientOrRobotPosition(ClientId);
        Board[position[0]][position[1]] = 0;

        for (int i = 0; i < CommunicationThread.GetAllClients().size(); i++) {
            if(CommunicationThread.GetAllClients().get(i).GetClientId() == ClientId)
            {
                CommunicationThread.DisconnectClient(i);
                break;
            }
        }

    }

    /**
     * Performs a random teleport of a specific client.
     * @param ClientId Id of the client.
     */
    private void RandomTeleport(int ClientId)
    {
        int currentPosition[] = GetClientOrRobotPosition(ClientId);
        Board[currentPosition[0]][currentPosition[1]] = 0;
        int nextPosition[] = RandomPosition();

        while(currentPosition == nextPosition) {
            nextPosition = RandomPosition();
        }

        Board[nextPosition[0]][nextPosition[1]] = ClientId;
    }

    /**
     * Performs a safe teleport of a specific client.
     * @param ClientId Id of the client.
     */
    private void SafeTeleport(int ClientId)
    {
        int currentPosition[] = GetClientOrRobotPosition(ClientId);
        Board[currentPosition[0]][currentPosition[1]] = 0;
        int nextPosition[] = RandomPosition();

        while(!(IsSafe(nextPosition) && (currentPosition != nextPosition)))
        {
            nextPosition = RandomPosition();
        }

        Board[nextPosition[0]][nextPosition[1]] = ClientId;
    }

    /**
     * Performs a short range attack around the client.
     * Depending on the settings this attack will vary in how it's performed.
     * @param ClientId Id of the client.
     */
    private void ShortRangeAttack(int ClientId)
    {
        int numberOfRobotsKilled = 0;
        if(Settings.shortRangeAttacksKillsAllAdjacentRobots) {

            int[] clientPosition = GetClientOrRobotPosition(ClientId);

            for(int row = clientPosition[0] - 1; row <= clientPosition[0] + 1; row++)
            {
                for(int column = clientPosition[1] - 1; column <= clientPosition[1] + 1; column++ )
                {
                    int[] surrounding = new int[2];
                    surrounding[0] = row;
                    surrounding[1] = column;

                    if(!OutOfBounds(surrounding)) {
                        if (Board[row][column] < -1) {
                            KillRobot(Board[row][column]);
                            Board[row][column] = 0;
                            numberOfRobotsKilled++;
                        }
                    }
                }
            }
        }

        else
        {
            List<Integer> robotIdsAroundClient = new ArrayList<>();

            int[] clientPosition = GetClientOrRobotPosition(ClientId);

            for(int row = clientPosition[0] - 2; row <= clientPosition[0] + 2; row++)
            {
                for(int column = clientPosition[1] - 2; column <= clientPosition[1] + 2; column++ )
                {
                    int[] surrounding = new int[2];
                    surrounding[0] = row;
                    surrounding[1] = column;

                    if(!OutOfBounds(surrounding)) {
                        if (Board[row][column] < -1) {
                            robotIdsAroundClient.add(Board[row][column]);
                        }
                    }
                }
            }

            if(!robotIdsAroundClient.isEmpty()) {
                int rand = ThreadLocalRandom.current().nextInt(0, robotIdsAroundClient.size());

                int[] pos = GetClientOrRobotPosition(robotIdsAroundClient.get(rand));
                KillRobot(robotIdsAroundClient.get(rand));
                Board[pos[0]][pos[1]] = 0;
                numberOfRobotsKilled++;
            }

        }

        CommunicationThread.IncreaseNumberOfSafeTeleportations(ClientId, numberOfRobotsKilled);

        ((ServerPlayingController) controller).IncreasePointsOfPlayer(CommunicationThread.GetClientUserName(ClientId), numberOfRobotsKilled);
        CommunicationThread.IncreaseScoreOfClient(ClientId, numberOfRobotsKilled);




    }

    /**
     * Generates a random position on the board
     * @return int array
     */
    private int[] RandomPosition() {
        boolean taken = true;
        int[] position = new int[2];
        while(taken) {
            int rowLength = Board.length;
            int columnLength = Board[1].length;
            Random rn = new Random();
            int row = rn.nextInt(rowLength);
            int column = rn.nextInt(columnLength);


            position[0] = row;
            position[1] = column;

            if(Board[row][column] == 0)
            {
                taken = false;
            }
        }

        return position;
    }

    /**
     *
     * @param ClientId Id of the client.
     * @return The position of the client.
     */
    private int[] GetClientOrRobotPosition(int ClientId)
    {
        for (int i = 0; i < Board.length; i++) {
            for (int j = 0; j < Board[1].length; j++) {
                if (Board[i][j] == ClientId) {
                    int[] Position = new int[2];
                    Position[0] = i;
                    Position[1] = j;
                    return Position;
                }
            }
        }

        return new int[2];
    }

    /**
     * Places rubble piles on the board.
     * @param numberOfPiles The number of piles to be placed on the map.
     */
    private void PlaceRubbleOnMap(int numberOfPiles)
    {
        for(int i = 0; i < numberOfPiles; i++)
        {
            int[] position = RandomPosition();
            Board[position[0]][position[1]] = -1;
        }
    }

    /**
     * Creates and places robots on the board.
     * @param numberOfRobots Number of robots to be created.
     */
    private void CreateAndPlaceRobots(int numberOfRobots)
    {
        robotList = new ArrayList<>();

        for (int i = -2; i > -numberOfRobots - 2; i--)
        {
            robotList.add(new Robot(i));
        }

        for(int i = 0; i < numberOfRobots; i++)
        {
            int[] position = RandomPosition();
            Board[position[0]][position[1]] = robotList.get(i).ID;
            robotList.get(i).position = position;
        }
    }

    /**
     * Kills a robot.
     * @param RobotId Id of the robot.
     */
    private void KillRobot(int RobotId)
    {
        for (Robot r : robotList) {
            if (r.ID == RobotId) {
                robotList.remove(r);
                break;
            }
        }
    }

    /**
     * The game goes to the next level.
     */
    private void NextLevel()
    {
        Settings.numberOfRobots += Settings.increaseOfRobotsPerLevel;
        Settings.numberOfRubbles += Settings.changeOfRubblesPerLevel;
        Level++;
        CreateLevel(Settings);


        for (CommunicationThread CT: CommunicationThread.GetAllClients()
             ) {

            ((ServerPlayingController) controller).IncreasePointsOfPlayer(CT.clientUserName, 5);
            CommunicationThread.IncreaseScoreOfClient(CT.GetClientId(), 5);
        }

    }

    /**
     * Checks whether or not a robot can reach a position
     * when their moving next.
     * @param position The position to be checked.
     * @return True if the position is safe, false if not.
     */
    private boolean IsSafe(int[] position)
    {
        for(int row = position[0] - 1; row <= position[0] + 1; row++)
        {
            for(int column = position[1] - 1; column <= position[1] + 1; column++ )
            {
                int[] surrounding = new int[2];
                surrounding[0] = row;
                surrounding[1] = column;

                if(!OutOfBounds(surrounding)) {
                    if (Board[row][column] < -1) {
                        return false;
                    }
                }
            }
        }

        return true;

    }

    /**
     *
     * @param position Position to check.
     * @return True if the position is inside of the board, false if not.s
     */
    private boolean OutOfBounds(int[] position)
    {
        if(position[0] < 0)
        {
            return true;
        }
        else if(position[0] > (Board.length - 1))
        {
            return true;
        }
        else if(position[1] < 0)
        {
            return true;
        }
        else if(position[1] > (Board[1].length - 1))
        {
            return true;
        }

        return false;
    }

    /**
     * Checks whether or not a given message corresponds to a "move command".
     * @param msg Message to check.
     * @return True if it's a move command, false if not.
     */
    private boolean IsMoveCommand(String msg)
    {
        return msg.equals("move right") ||
                msg.equals("move left") ||
                msg.equals("move down") ||
                msg.equals("move up") ||
                msg.equals("move up left") ||
                msg.equals("move down left") ||
                msg.equals("move up right") ||
                msg.equals("move down right");
    }

    /**
     * Checks whether a client can perform a certain move.
     * @param msg Message with information about the move to execute.
     * @param ClientId Id of the client.
     * @return True if the client can move there, false if not.
     */
    private boolean CanMoveToPosition(String msg, int ClientId)
    {
        int[] position = GetClientOrRobotPosition(ClientId);
        int[] nextPosition = new int[2];


        switch(msg){
            case "move right":  nextPosition[0] = position[0];
                nextPosition[1] = position[1] + 1;
                return ((position[1] != (Board[1].length - 1)) && IsEmpty(nextPosition));

            case "move left":   nextPosition[0] = position[0];
                nextPosition[1] = position[1] - 1;
                return ((position[1] != 0) && IsEmpty(nextPosition));

            case "move up":     nextPosition[0] = position[0] - 1;
                nextPosition[1] = position[1];
                return ((position[0] != 0) && IsEmpty(nextPosition));

            case "move down":   nextPosition[0] = position[0] + 1;
                nextPosition[1] = position[1];
                return ((position[0] != (Board.length - 1)) && IsEmpty(nextPosition));

            case "move up left":nextPosition[0] = position[0] - 1;
                nextPosition[1] = position[1] - 1;
                return ((position[1] != 0) && (position[0] != 0) && IsEmpty(nextPosition));

            case "move down left":  nextPosition[0] = position[0] + 1;
                nextPosition[1] = position[1] - 1;
                return ((position[1] != 0) && (position[0] != (Board.length - 1)) && IsEmpty(nextPosition));

            case "move up right":   nextPosition[0] = position[0] - 1;
                nextPosition[1] = position[1] + 1;
                return ((position[0] != 0) && (position[1] != (Board[1].length - 1)) && IsEmpty(nextPosition));

            case "move down right": nextPosition[0] = position[0] + 1;
                nextPosition[1] = position[1] + 1;
                return ((position[0] != (Board.length - 1)) && (position[1] != (Board[1].length - 1)) && IsEmpty(nextPosition));
        }

        return false;
    }

    /**
     *
     * @param position Position to check if empty.
     * @return True if the position is not containing a robot, client or rubble. (If 0). False if it does.
     */
    private boolean IsEmpty(int[] position) {
        return !OutOfBounds(position) && Board[position[0]][position[1]] == 0;

    }

    /**
     *
     * @return True if the game has started, false if not.
     */
    public static synchronized boolean GetGameStarted()
    {
        return gameStarted;
    }

    /**
     *
     * @return True if the session is ongoing, false if not.
     */
    public static synchronized  boolean GetSessionOnGoing() { return SessionOngoing;}

    /**
     * Resets the static variables after a game is completed.
     */
    public static synchronized void ResetStaticVariables()
    {
        gameStarted = false;
        SessionOngoing = true;
    }

    /**
     * Removes a specific client from the board, and sends the information to the rest of the clients.s
     * @param clientID Id of the client.
     */
    public static synchronized void RemoveDisconnectedPlayerFromBoard(int clientID)
    {
        for (int i = 0; i < Board.length; i++) {
            for (int j = 0; j < Board[1].length; j++) {
                if (Board[i][j] == clientID) {
                    Board[i][j] = 0;
                    break;
                }
            }
        }

        if(!CommunicationThread.GetAllClients().isEmpty() && (CommunicationThread.GetPlayerTurn() > 0)) {
            CommunicationThread.SendToClients("levelinfo;" + Round + ";" + Level + ";" + CommunicationThread.GetAllClients().get(CommunicationThread.GetPlayerTurn() - 1).GetClientId() + ";");
        }

        CommunicationThread.SendUpdatedBoardToClients(Board);
    }


}
