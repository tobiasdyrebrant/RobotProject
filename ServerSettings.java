package tobdyh131;

/**
 * Created by Tobias on 2015-12-10.
 *
 * A class which defines all the settings of the server.
 */
public class ServerSettings {
    public int height;
    public int width;

    public int numberOfPlayersBeforeStart;
    public int numberOfRoundsPerLevel;

    public int numberOfRobots;
    public int increaseOfRobotsPerLevel;
    public boolean robotsMergeOnCollision;

    public int numberOfRubbles;
    public int changeOfRubblesPerLevel;

    public int numberOfSafeTeleportationsAwardedPerKill;
    public int numberOfSafeTeleportationsAwardedPerLevel;
    public int numberOfShortRangeAttacksAwardedPerLevel;


    public boolean robotsLockToTarget;
    public boolean shortRangeAttacksKillsAllAdjacentRobots;

    public int robotPerceptionRowRange;
    public int robotPerceptionColumnRange;

    public int port;

    /**
     * The constructor that creates the settings based on all the arguments.
     * @param height Height of the board.
     * @param width Width of the board.
     * @param numberOfPlayersBeforeStart Number of clients that need to be connected before the admin can start.
     * @param numberOfRoundsPerLevel Number of rounds a level consist of.
     * @param numberOfRobots Number of robots the level will consist of.
     * @param increaseOfRobotsPerLevel The increment of robots per level.
     * @param robotsMergeOnCollision If robots merge on collision and become a robot, or if they become a pile of rubble.
     * @param numberOfRubbles Number of rubbles the level will consist of.
     * @param changeOfRubblesPerLevel The change of rubbles per level.
     * @param numberOfSafeTeleportationsAwardedPerKill How many safe teleportations are awarded to the client per kill.
     * @param numberOfSafeTeleportationsAwardedPerLevel How many safe teleportations that the client starts with per level.
     * @param numberOfShortRangeAttacksAwardedPerLevel How many short range attacks the the client stats with per level.
     * @param robotsLockToTarget If robots will lock to a specific client, or if the will chase the closest.
     * @param shortRangeAttacksKillsAllAdjacentRobots If short range attacks kills all adjacent robots, or just one (but has greater range)
     * @param robotPerceptionRowRange How far a robot can "see" in rows.
     * @param robotPerceptionColumnRange How far a robot can "see" in columns.
     * @param port The port on which the server socket shall be created.
     */
    public ServerSettings(int height, int width, int numberOfPlayersBeforeStart, int numberOfRoundsPerLevel, int numberOfRobots,
                          int increaseOfRobotsPerLevel, boolean robotsMergeOnCollision, int numberOfRubbles,
                          int changeOfRubblesPerLevel, int numberOfSafeTeleportationsAwardedPerKill, int numberOfSafeTeleportationsAwardedPerLevel,
                          int numberOfShortRangeAttacksAwardedPerLevel, boolean robotsLockToTarget,
                          boolean shortRangeAttacksKillsAllAdjacentRobots, int robotPerceptionRowRange,
                          int robotPerceptionColumnRange, int port)
    {
        this.height = height;
        this.width = width;
        this.numberOfPlayersBeforeStart = numberOfPlayersBeforeStart;
        this.numberOfRoundsPerLevel = numberOfRoundsPerLevel;
        this.numberOfRobots = numberOfRobots;
        this.increaseOfRobotsPerLevel = increaseOfRobotsPerLevel;
        this.robotsMergeOnCollision = robotsMergeOnCollision;
        this.numberOfRubbles = numberOfRubbles;
        this.changeOfRubblesPerLevel = changeOfRubblesPerLevel;
        this.numberOfSafeTeleportationsAwardedPerKill = numberOfSafeTeleportationsAwardedPerKill;
        this.numberOfSafeTeleportationsAwardedPerLevel = numberOfSafeTeleportationsAwardedPerLevel;
        this.numberOfShortRangeAttacksAwardedPerLevel = numberOfShortRangeAttacksAwardedPerLevel;
        this.robotsLockToTarget = robotsLockToTarget;
        this.shortRangeAttacksKillsAllAdjacentRobots = shortRangeAttacksKillsAllAdjacentRobots;
        this.robotPerceptionRowRange = robotPerceptionRowRange;
        this.robotPerceptionColumnRange = robotPerceptionColumnRange;
        this.port = port;
    }

    /**
     * The default constructor.
     */
    public ServerSettings()
    {
        this.height = 0;
        this.width = 0;
        this.numberOfPlayersBeforeStart = 0;
        this.numberOfRoundsPerLevel = 0;
        this.numberOfRobots = 0;
        this.increaseOfRobotsPerLevel = 0;
        this.robotsMergeOnCollision = false;
        this.numberOfRubbles = 0;
        this.changeOfRubblesPerLevel = 0;
        this.numberOfSafeTeleportationsAwardedPerKill = 0;
        this.numberOfSafeTeleportationsAwardedPerLevel = 0;
        this.numberOfShortRangeAttacksAwardedPerLevel = 0;
        this.robotsLockToTarget = false;
        this.shortRangeAttacksKillsAllAdjacentRobots = false;
        this.robotPerceptionRowRange = 0;
        this.robotPerceptionColumnRange = 0;
        this.port = 1111;
    }

}
