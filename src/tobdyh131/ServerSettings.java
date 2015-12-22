package tobdyh131;

/**
 * Created by Tobias on 2015-12-10.
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
