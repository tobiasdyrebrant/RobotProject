package tobdyh131;

/**
 * Created by Tobias on 2015-12-04.
 *
 * A class the defines a robot.
 */
public class Robot {
    public int ID;
    public int[] position;
    public boolean hasMoved;
    public int lockedToClientID;
    public boolean hasBeenChecked;

    /**
     * The constructor that creates a new robot with the argument.
     * @param ID Id of the robot to be created.
     */
    public Robot(int ID) {
        this.ID = ID;
        position = new int[2];
        hasMoved = false;
        lockedToClientID = -1;
        hasBeenChecked = false;
    }
}
