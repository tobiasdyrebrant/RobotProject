package tobdyh131;

import java.util.Comparator;

/**
 * Created by Tobias on 2015-12-17.
 */
public class ClientToLocksEntry implements Comparable<ClientToLocksEntry> {
    public int ClientId;
    public int NumberOfLocks;

    public ClientToLocksEntry(int clientId, int numberOfLocks)
    {
        ClientId = clientId;
        NumberOfLocks = numberOfLocks;
    }

    @Override
    public int compareTo(ClientToLocksEntry o) {
        return this.NumberOfLocks - o.NumberOfLocks;
    }
}
