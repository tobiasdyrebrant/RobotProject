package tobdyh131;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Tobias on 2015-11-24.
 *
 * A class that listens to the server, receives the messages the server is sending, and the passes it to the client.
 */
class ServerListener extends Thread {
    final private BufferedReader fromServer;

    private final Object controller;

    private int BoardHeight;
    private int BoardWidth;

    private boolean ClientConnected = true;


    /**
     * The constructor that creates the server listener based on the arguments.
     * @param fromServer Buffered reader from the server.
     * @param controller he controller of the GUI which is displayed for the client during playing.
     */
    public ServerListener(BufferedReader fromServer, Object controller)
    {
        this.fromServer = fromServer;
        this.controller = controller;
    }

    /**
     * The function which is continuously executed during runtime.
     * If the client is connected to the server, it reads from the server and handles the message.
     */
    public void run()
    {
        String lineFromServer;


            while (ClientConnected) {
                try {
                    while ((lineFromServer = fromServer.readLine()) != null && !lineFromServer.equals("quit")) {
                        if (lineFromServer.contains(";")) {
                            String[] line = lineFromServer.split(";");
                            if (line[0].equals("start")) {
                                int height = Integer.valueOf(line[1]);
                                BoardHeight = height;
                                int width = Integer.valueOf(line[2]);
                                BoardWidth = width;
                                ((ClientPlayingController) controller).CreateBoard(height, width);
                                ((ClientPlayingController) controller).WriteToTextArea("Starting game, get ready!\n");
                            }

                            if (line[0].equals("board")) {
                                int Board[][] = new int[BoardHeight][BoardWidth];
                                int index = 1;
                                while (index < line.length) {
                                    if (line[index].equals("rubble")) {
                                        Board[Integer.valueOf(line[index + 1])][Integer.valueOf(line[index + 2])] = -1;
                                        index += 3;
                                    } else if (line[index].equals("robot") || line[index].equals("client")) {
                                        Board[Integer.valueOf(line[index + 2])][Integer.valueOf(line[index + 3])] = Integer.valueOf(line[index + 1]);
                                        index += 4;
                                    }
                                }

                                ((ClientPlayingController) controller).UpdateBoard(Board);
                            }

                            if (line[0].equals("levelinfo")) {
                                ((ClientPlayingController) controller).SetLevelInformation(Integer.valueOf(line[1]), Integer.valueOf(line[2]), Integer.valueOf(line[3]));
                            }

                            if (line[0].equals("id")) {
                                ((ClientPlayingController) controller).clientId = Integer.valueOf(line[1]);
                            }
                            if(line[0].equals("disconnected") || line[0].equals("killed"))
                            {
                                ObservableList<HighscoreInfo> highScoreList = FXCollections.observableArrayList();
                                int index = 1;
                                while(index < line.length)
                                {
                                    HighscoreInfo hsi = new HighscoreInfo(line[index], Integer.valueOf(line[index + 1]));
                                    highScoreList.add(hsi);
                                    index += 2;
                                }

                                ((ClientPlayingController) controller).showHighScoreScene(highScoreList);
                                ClientConnected = false;
                                break;
                            }
                        }

                        else if (lineFromServer.equals("no sra")) {
                            ((ClientPlayingController) controller).WriteToTextArea("You've used all the short range attacks for this level ");
                        }
                        else if (lineFromServer.equals("no st")) {
                            ((ClientPlayingController) controller).WriteToTextArea("You can't safe teleport anymore,\n either kill an enemy or survive to the next round");
                        }
                        else if(lineFromServer.equals("wrong move"))
                        {
                            ((ClientPlayingController) controller).WriteToTextArea("You can't move there!");
                        }

                    }
                }
            catch(IOException e) {
                System.out.println("Could not read from server!");
                ClientConnected = false;
            }
        }

    }
}
