package tobdyh131;

import java.util.Comparator;

/**
 * Created by Tobias on 2015-12-17.
 *
 * A class that is used when handling with the case when robots
 * locks to a certain client.
 */
public class ClientToLocksEntry implements Comparable<ClientToLocksEntry> {
    public int ClientId;
    public int NumberOfLocks;

    /**
     * The constructor that creates an object based on the arguments
     * @param clientId Id of the client.
     * @param numberOfLocks Number of robots that has locked to the specific client.
     */
    public ClientToLocksEntry(int clientId, int numberOfLocks)
    {
        ClientId = clientId;
        NumberOfLocks = numberOfLocks;
    }

    /**
     * An override method that compares to ClientToLocksEntry objects
     * @param o A ClientToLocksEntry object
     * @return An integer that represents the difference of the locks between two clients
     */
    @Override
    public int compareTo(ClientToLocksEntry o) {
        return this.NumberOfLocks - o.NumberOfLocks;
    }
}
