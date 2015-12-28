package tobdyh131;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.Buffer;

/**
 * Created by Tobias on 2015-12-18.
 */
public class ReadInput implements Runnable{
    BufferedReader in;

    public ReadInput(BufferedReader i)
    {
        in = i;
    }

    public void run()
    {
        while(true)
        {

        }
    }

    public String getInput()
    {
        try
        {
            return in.readLine();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        return "";

    }
}
