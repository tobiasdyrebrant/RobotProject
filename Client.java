package tobdyh131;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * Created by Tobias on 2015-11-27.
 *
 * This is the Client, its main purpose is to pass messages from the user
 * through the socket where the message is received and handled.
 */
class Client implements Runnable{
    private static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public BufferedReader in;
    public PrintWriter out;
    private BufferedReader kdb_reader;
    private Socket socket;

    private boolean Connected = true;

    public boolean SocketError = false;

    /**
     * Constructor that creates the client based on the 3 input arguments.
     * @param address The ip address of the connection
     * @param port The port of the connection
     * @param UserName The client's username
     */
    public Client(InetAddress address, int port, String UserName)
    {
        try {
            socket = new Socket(address,port);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            kdb_reader = new BufferedReader(new InputStreamReader(System.in));

            out.println(UserName);

        }
        catch (IOException e)
        {
            SocketError = true;
            LOGGER.info("Client constructor I/O error : " + e.getMessage());
        }
    }


    /**
     * The function which is continuously executed during runtime.
     * It checks for messages in the queue (which receives messages from user input via the GUI)
     * and then passes it through the socket.
     */
    public void run()
    {
        while(Connected)
        {
            String buf;
            while ((buf = queue.poll()) != null) {

                out.println(buf);

            }

        }

        try{
            in.close();
            out.close();
            socket.close();
            kdb_reader.close();
        }
        catch(IOException e)
        {
            LOGGER.info(e.getMessage());
        }
    }

    /**
     * A method to cancel the run-method before to terminate this thread.
     */
    public void disconnectClient()
    {
        Connected = false;
    }

}
