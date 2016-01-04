package tobdyh131;

/**
 * Created by Tobias on 2015-12-01.
 *
 * A class that is used when passing messages to queues.
 * Used so the server can see who sent the message and what it says.
 */
class ComMessage {
    public final String Message;
    public final int ClientId;

    /**
     * The constructor that creates a "Communication Message" based on the arguments.
     * @param message The message to handled
     * @param ClientId The id of the client
     */
    public ComMessage(String message, int ClientId)
    {
        Message = message;
        this.ClientId = ClientId;
    }

}
