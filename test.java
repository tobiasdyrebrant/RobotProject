package tobdyh131;

import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

public class test{

    BufferedReader in = new BufferedReader(
            new InputStreamReader( System.in ) );
    private String str = "";

    TimerTask task = new TimerTask(){
        public void run(){
            try
            {
                str = in.readLine();
            }
            catch(IOException e)
            {
                System.out.println(e.getMessage());
            }

            if( str.equals("") ){
                System.out.println( "you input nothing. exit..." );
            }
        }
    };


    public void getInput() throws Exception{

        System.out.println( "Input a string within 2 seconds: " );

        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {

                            System.out.println("blabla");


                    }
                },
                2000
        );


    }


    public static void main( String[] args ){
            try {
                (new test()).getInput();
            } catch (Exception e) {
                System.out.println(e);
            }


    }
}
