package tobdyh131;

/**
 * Created by Tobias on 30-Dec-15.
 *
 * A class which defines a high score.
 */
public class HighscoreInfo {
    public String userName;
    public int score;

    /**
     * The constructor that creates the high score info based on the arguments.
     * @param UserName Username of the client.
     * @param Score Score of the client.
     */
    public HighscoreInfo(String UserName, int Score)
    {
        userName = UserName;
        score = Score;
    }

    /**
     * An override function that's used so the high score list can easily be printed.
     * @return A string definition of the class.
     */
    @Override
    public String toString()
    {
        return "User: " + userName + "  Score: " + score;
    }
}
