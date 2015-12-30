package tobdyh131;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Tobias on 2015-11-27.
 */
public class GameEngine implements Runnable {
    public static BlockingQueue<ComMessage> queue = new LinkedBlockingQueue<>();
    public static int Board[][];
    public static int Round;
    public static int Level;
    private static boolean gameStarted = false;
    private static boolean SessionOngoing = true;
    private ComMessage msg;

    private ArrayList<Robot> robotList;

    private Object controller;



    // 0 = Empty space
    // 0< = Clienter
    // -1 = Rubble
    // -1> = Robots

    private ServerSettings Settings;
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


    public void run()
    {

            while (gameStarted) {
                //TODO
                //controllern går tillbaka till main screen osv.
                if(CommunicationThread.GetAllClients().isEmpty())
                {
                    ((ServerPlayingController) controller).GoToStartup();
                    SessionOngoing = false;
                    gameStarted = false;
                    break;
                }
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
                            //TODO
                            //Skicka felmeddelande till clienten
                            //Skapa en statisk funktion i CommunicationThread och skicka med client id
                            //Den i sin tur går igenom allClients listan och när den hittar rätt klient så skriver
                            //den ut i dess out variabel
                            System.out.println("Can't move there");
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

                    CommunicationThread.ResetDeadlines();

                }

                if (Round > Settings.numberOfRoundsPerLevel) {
                    NextLevel();
                }

            }

    }

    private void Move(int ClientIndex, String msg)
    {
        int[] currentPosition = GetClientOrRobotPosition(ClientIndex);
        Board[currentPosition[0]][currentPosition[1]] = 0;

        switch(msg){
            case "move right":  Board[currentPosition[0]][currentPosition[1] + 1] = ClientIndex;
                                ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientIndex + " moved right");
                                break;
            case "move left":   Board[currentPosition[0]][currentPosition[1] - 1] = ClientIndex;
                                ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientIndex + " moved left");
                                break;
            case "move up":     Board[currentPosition[0] - 1][currentPosition[1]] = ClientIndex;
                                ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientIndex + " moved up");
                                break;
            case "move down":   Board[currentPosition[0] + 1][currentPosition[1]] = ClientIndex;
                                ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientIndex + " moved down");
                                break;
            case "move up right":   Board[currentPosition[0] - 1][currentPosition[1] + 1] = ClientIndex;
                                    ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientIndex + " moved up right");
                                    break;
            case "move down right": Board[currentPosition[0] + 1][currentPosition[1] + 1] = ClientIndex;
                                    ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientIndex + " down up");
                                    break;
            case "move up left":    Board[currentPosition[0] - 1][currentPosition[1] - 1] = ClientIndex;
                                    ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientIndex + " moved up left");
                                    break;
            case "move down left":  Board[currentPosition[0] + 1][currentPosition[1] - 1] = ClientIndex;
                                    ((ServerPlayingController) controller).WriteToTextArea("Client " + ClientIndex + " moved down left");
                                    break;
        }
    }


    private void MoveRobotsToClosestClient()
    {

        ArrayList<int[]> clientPositions = new ArrayList<>();

        for (int i = 0; i < Board[0].length; i++) {
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
            for (int i = 0; i < robotList.size(); i++) {
                int closestClient = -1;

                //Find closest client
                for (int j = 0; j < clientPositions.size(); j++) {
                    int rowDifference;
                    if (robotList.get(i).position[0] > clientPositions.get(j)[0])
                        rowDifference = robotList.get(i).position[0] - clientPositions.get(j)[0];
                    else
                        rowDifference = clientPositions.get(j)[0] - robotList.get(i).position[0];

                    int columnDifference;
                    if (robotList.get(i).position[1] > clientPositions.get(j)[1])
                        columnDifference = robotList.get(i).position[1] - clientPositions.get(j)[1];
                    else
                        columnDifference = clientPositions.get(j)[1] - robotList.get(i).position[1];

                    //Compares to the current closest clients
                    if(closestClient != -1) {
                        int currentClosestClientRowDifference;
                        if (robotList.get(i).position[0] > clientPositions.get(closestClient)[0])
                            currentClosestClientRowDifference = robotList.get(i).position[0] - clientPositions.get(closestClient)[0];
                        else
                            currentClosestClientRowDifference = clientPositions.get(j)[0] - robotList.get(closestClient).position[0];

                        int currentClosestClientColumnDifference;
                        if (robotList.get(i).position[1] > clientPositions.get(closestClient)[1])
                            currentClosestClientColumnDifference = robotList.get(i).position[1] - clientPositions.get(closestClient)[1];
                        else
                            currentClosestClientColumnDifference = clientPositions.get(closestClient)[1] - robotList.get(i).position[1];
                        if((rowDifference <= Settings.robotPerceptionRowRange) && (columnDifference <= Settings.robotPerceptionColumnRange)) {
                            if ((rowDifference + columnDifference) < (currentClosestClientRowDifference + currentClosestClientColumnDifference)) {
                                closestClient = j;
                            }
                        }
                    }
                    else
                    {
                        if((rowDifference <= Settings.robotPerceptionRowRange) && (columnDifference <= Settings.robotPerceptionColumnRange)) {
                            closestClient = j;
                        }
                    }


                }


                //If the robot has found any close clients
                if(closestClient != -1) {
                    //Move towards closest client
                    Board[robotList.get(i).position[0]][robotList.get(i).position[1]] = 0;
                    if (robotList.get(i).position[0] > clientPositions.get(closestClient)[0]) {
                        robotList.get(i).position[0] -= 1;
                    } else if (robotList.get(i).position[0] < clientPositions.get(closestClient)[0]) {
                        robotList.get(i).position[0] += 1;
                    }

                    if (robotList.get(i).position[1] > clientPositions.get(closestClient)[1]) {
                        robotList.get(i).position[1] -= 1;
                    } else if (robotList.get(i).position[1] < clientPositions.get(closestClient)[1]) {
                        robotList.get(i).position[1] += 1;
                    }
                    robotList.get(i).hasMoved = true;
                }
                else
                {
                    robotList.get(i).hasMoved = false;
                }


            }

            CheckAndUpdateWhereRobotsMoved();

        }
    }

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
                                    for(Robot robot : robotList)
                                    {
                                        if(robot.lockedToClientID == clientId)
                                        {
                                            robot.lockedToClientID = -1;
                                        }
                                    }

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

    private int GetClientIdWithLeastLocks()
    {
        List<ClientToLocksEntry> CTLE = new ArrayList<ClientToLocksEntry>();
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

    private void RandomTeleport(int ClientIndex)
    {
        int currentPosition[] = GetClientOrRobotPosition(ClientIndex);
        Board[currentPosition[0]][currentPosition[1]] = 0;
        int nextPosition[] = RandomPosition();

        while(currentPosition == nextPosition) {
            nextPosition = RandomPosition();
        }

        Board[nextPosition[0]][nextPosition[1]] = ClientIndex;
    }

    private void SafeTeleport(int ClientIndex)
    {
        int currentPosition[] = GetClientOrRobotPosition(ClientIndex);
        Board[currentPosition[0]][currentPosition[1]] = 0;
        int nextPosition[] = RandomPosition();

        while(!(IsSafe(nextPosition) && (currentPosition != nextPosition)))
        {
            nextPosition = RandomPosition();
        }

        Board[nextPosition[0]][nextPosition[1]] = ClientIndex;
    }


    private void ShortRangeAttack(int ClientIndex)
    {
        int numberOfRobotsKilled = 0;
        if(Settings.shortRangeAttacksKillsAllAdjacentRobots) {

            int[] clientPosition = GetClientOrRobotPosition(ClientIndex);

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
            List<Integer> robotIdsAroundClient = new ArrayList<Integer>();

            int[] clientPosition = GetClientOrRobotPosition(ClientIndex);

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

        CommunicationThread.IncreaseNumberOfSafeTeleportations(ClientIndex, numberOfRobotsKilled);

        ((ServerPlayingController) controller).IncreasePointsOfPlayer(CommunicationThread.GetClientUserName(ClientIndex), numberOfRobotsKilled);
        CommunicationThread.IncreaseScoreOfClient(ClientIndex, numberOfRobotsKilled);




    }

    /**
     * Generates a random position.
     */
    private int[] RandomPosition() {
        boolean taken = true;
        int[] position = new int[2];
        while(taken) {
            int rowLength = Board[0].length;
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

    private int[] GetClientOrRobotPosition(int Id)
    {
        for (int i = 0; i < Board[0].length; i++) {
            for (int j = 0; j < Board[1].length; j++) {
                if (Board[i][j] == Id) {
                    int[] Position = new int[2];
                    Position[0] = i;
                    Position[1] = j;
                    return Position;
                }
            }
        }

        return new int[2];
    }

    private void PlaceRubbleOnMap(int numberOfPiles)
    {
        for(int i = 0; i < numberOfPiles; i++)
        {
            int[] position = RandomPosition();
            Board[position[0]][position[1]] = -1;
        }
    }

    private void CreateAndPlaceRobots(int numberOfRobots)
    {
        robotList = new ArrayList<Robot>();

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

    private void KillRobot(int RobotId)
    {
        Iterator<Robot> it = robotList.iterator();
        while(it.hasNext())
        {
            Robot r = it.next();
            if(r.ID == RobotId)
            {
                robotList.remove(r);
                break;
            }
        }
    }

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

        /*
        int[] surrounding = position.clone();
        surrounding[1] += 1;
        if(!OutOfBounds(surrounding))
        {
            if(Board[surrounding[0]][surrounding[1]] < -1)
                return false;
        }

        surrounding = position.clone();
        surrounding[1] -= 1;
        if(!OutOfBounds(surrounding)) {
            if (Board[surrounding[0]][surrounding[1]] < -1)
                return false;
        }

        surrounding = position.clone();
        surrounding[0] += 1;
        if(!OutOfBounds(surrounding))
        {
            if(Board[surrounding[0]][surrounding[1]]  < -1)
                return false;
        }

        surrounding = position.clone();
        surrounding[0] -= 1;
        if(!OutOfBounds(surrounding))
        {
            if(Board[surrounding[0]][surrounding[1]]  < -1)
                return false;
        }

        surrounding = position.clone();
        surrounding[0] += 1;
        surrounding[1] += 1;
        if(!OutOfBounds(surrounding))
        {
            if(Board[surrounding[0]][surrounding[1]]  < -1)
                return false;
        }

        surrounding = position.clone();
        surrounding[0] -= 1;
        surrounding[1] -= 1;
        if(!OutOfBounds(surrounding))
        {
            if(Board[surrounding[0]][surrounding[1]]  < -1)
                return false;
        }

        surrounding = position.clone();
        surrounding[0] -= 1;
        surrounding[1] += 1;
        if(!OutOfBounds(surrounding))
        {
            if(Board[surrounding[0]][surrounding[1]]  < -1)
                return false;
        }

        surrounding = position.clone();
        surrounding[0] += 1;
        surrounding[1] -= 1;
        if(!OutOfBounds(surrounding))
        {
            if(Board[surrounding[0]][surrounding[1]]  < -1)
                return false;
        }

        return true;
        */
    }

    private boolean OutOfBounds(int[] position)
    {
        if(position[0] < 0)
        {
            return true;
        }
        else if(position[0] > (Board[0].length - 1))
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

    private boolean IsMoveCommand(String msg)
    {
        if(msg.equals("move right") ||
                msg.equals("move left") ||
                msg.equals("move down") ||
                msg.equals("move up") ||
                msg.equals("move up left") ||
                msg.equals("move down left") ||
                msg.equals("move up right") ||
                msg.equals("move down right"))
        {
            return true;
        }
        return false;
    }

    private boolean CanMoveToPosition(String msg, int ClientIndex)
    {
        int[] position = GetClientOrRobotPosition(ClientIndex);
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
                return ((position[0] != (Board[0].length - 1)) && IsEmpty(nextPosition));

            case "move up left":nextPosition[0] = position[0] - 1;
                nextPosition[1] = position[1] - 1;
                return ((position[1] != 0) && (position[0] != 0) && IsEmpty(nextPosition));

            case "move down left":  nextPosition[0] = position[0] + 1;
                nextPosition[1] = position[1] - 1;
                return ((position[1] != 0) && (position[0] != (Board[0].length - 1)) && IsEmpty(nextPosition));

            case "move up right":   nextPosition[0] = position[0] - 1;
                nextPosition[1] = position[1] + 1;
                return ((position[0] != 0) && (position[1] != (Board[1].length - 1)) && IsEmpty(nextPosition));

            case "move down right": nextPosition[0] = position[0] + 1;
                nextPosition[1] = position[1] + 1;
                return ((position[0] != (Board[0].length - 1)) && (position[1] != (Board[1].length - 1)) && IsEmpty(nextPosition));
        }

        return false;
    }

    private boolean IsEmpty(int[] position)
    {
        if(OutOfBounds(position))
        {
            return false;
        }
        return Board[position[0]][position[1]] == 0;

    }

    public static synchronized boolean GetGameStarted()
    {
        return gameStarted;
    }

    public static synchronized  boolean GetSessionOnGoing() { return SessionOngoing;}

    public static synchronized void ResetStaticVariables()
    {
        gameStarted = false;
        SessionOngoing = true;
    }

    public static synchronized void RemoveDisconnectedPlayerFromBoard(int clientID)
    {
        for (int i = 0; i < Board[0].length; i++) {
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
