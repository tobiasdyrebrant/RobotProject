package tobdyh131;

/**
 * Created by Tobias on 27-Dec-15.
 *
 * A class which describes the stats of the user.
 */
public class ClientStats {
    public String clientUserName;
    public int clientID;
    public int score;

    /**
     * The constructor that creates the clients stats based on the arguments.
     * @param ClientUserName Username of the client
     * @param ClientID Id of the client
     * @param Score Score of the client
     */
    public ClientStats(String ClientUserName,int ClientID,  int Score)
    {
        clientUserName = ClientUserName;
        clientID = ClientID;
        score = Score;
    }

    /**
     * An override function that's used so the client info list can easily be displayed.
     * @return A string definition of the class.
     */
    @Override
    public String toString()
    {
        return clientUserName + "  " + "ID: " + clientID + " Score: " + score;
    }
}
