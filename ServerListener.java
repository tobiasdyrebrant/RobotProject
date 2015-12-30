package tobdyh131;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tobias on 2015-11-24.
 */
public class ServerListener extends Thread {
    final private BufferedReader fromServer;

    private Object controller;

    private int BoardHeight;
    private int BoardWidth;

    private boolean ClientConnected = true;


    public ServerListener(BufferedReader fromServer, Object controller)
    {
        this.fromServer = fromServer;
        this.controller = controller;
    }

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
                            ((ClientPlayingController) controller).WriteToTextArea("You've used all the short range attacks for this level \n");
                        } else if (lineFromServer.equals("no st")) {
                            ((ClientPlayingController) controller).WriteToTextArea("You can't safe teleport anymore,\n either kill an enemy or survive to the next round \n");
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
