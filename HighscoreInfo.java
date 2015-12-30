package tobdyh131;

/**
 * Created by Tobias on 30-Dec-15.
 */
public class HighscoreInfo {
    public String userName;
    public int score;

    public HighscoreInfo(String UserName, int Score)
    {
        userName = UserName;
        score = Score;
    }

    @Override
    public String toString()
    {
        return "User: " + userName + " Score: " + score;
    }
}
