package tobdyh131;

/**
 * Created by Tobias on 27-Dec-15.
 *
 * A class which describes the stats of the user.
 */
class ClientStats {
    public final String clientUserName;
    private final int clientID;
    public int score;

    /**
     * The constructor that creates the clients stats based on the arguments.
     * @param ClientUserName Username of the client
     * @param ClientID Id of the client
     */
    public ClientStats(String ClientUserName, int ClientID)
    {
        clientUserName = ClientUserName;
        clientID = ClientID;
        score = 0;
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
