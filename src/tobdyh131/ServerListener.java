package tobdyh131;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Tobias on 2015-11-24.
 */
public class ServerListener extends Thread {
    final private BufferedReader fromServer;

    public ServerListener(BufferedReader fromServer)
    {
        this.fromServer = fromServer;
    }

    public void run()
    {
        String lineFromServer;
        try
        {
            while((lineFromServer = fromServer.readLine()) != null && !lineFromServer.equals("quit"))
            {
                System.out.println("From server: " + lineFromServer);
            }
        }
        catch (IOException e)
        {
            System.out.println("Could not read from server!");
        }
    }
}
