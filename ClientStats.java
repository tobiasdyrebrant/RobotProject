package tobdyh131;

/**
 * Created by Tobias on 27-Dec-15.
 */
public class ClientStats {
    public String clientUserName;
    public int clientID;
    public int score;

    public ClientStats(String ClientUserName,int ClientID,  int Score)
    {
        clientUserName = ClientUserName;
        clientID = ClientID;
        score = Score;
    }

    @Override
    public String toString()
    {
        return clientUserName + "  " + "ID: " + clientID + " Score: " + score;
    }
}
