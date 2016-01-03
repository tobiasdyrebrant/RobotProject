package tobdyh131;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Tobias on 2015-12-23.
 *
 * A controller for the servers connection GUI .
 * This class reacts and handles events that occur during the connection phase.
 */
public class ServerDuringConnectionController implements Initializable, ControlledScreen{

    ScreensController myController;

    public ObservableList<String> listOfPlayers = FXCollections.observableArrayList();

    public Server server;

    private ServerConnectionThread SCT;

    @FXML
    private ListView connectedPlayers;

    @FXML
    private TextArea consoleOutput;

    @FXML
    private Button disconnectPlayer;

    @FXML
    private Button startGame;

    /**
     * If the admin wants to disconnect a certain player,
     * this method is called.
     */
    @FXML
    public void handleDisconnectPlayer()
    {
        int indexOfPlayer = connectedPlayers.getSelectionModel().getSelectedIndex();
        if(indexOfPlayer != -1) {
            consoleOutput.appendText("You've disconnected player \"" + listOfPlayers.get(indexOfPlayer) + "\"\n");
            listOfPlayers.remove(indexOfPlayer);
            CommunicationThread.DisconnectClient(indexOfPlayer);
            CommunicationThread.ResetClientNumbers();
        }

    }

    //TODO
    //Client disconnected during connection time
    public void playerDisconnected(int ClientID)
    {
        int clientIndex = CommunicationThread.GetClientIndex(ClientID);
        listOfPlayers.remove(clientIndex);
        CommunicationThread.DisconnectClient(clientIndex);
        CommunicationThread.ResetClientNumbers();
    }

    /**
     * If enough clients are connected and the admin decides to
     * start the game, this method is called.
     */
    @FXML
    public void handleStartGame()
    {
        //TODO
        //Ska det vara >= eller bara == ?
        if(CommunicationThread.GetNumberOfPlayers() >= server.GetServerSettings().numberOfPlayersBeforeStart) {
            CommunicationThread.SendToClients("start;" + server.GetServerSettings().height + ";" + server.GetServerSettings().width + ";");

            SCT.CloseConnectionThread();

            myController.loadScreen("serverPlaying", "ServerPlayingScene.fxml", server.GetServerSettings());
            myController.setScreen("serverPlaying");
        }
        else
        {
            consoleOutput.appendText("Not enough players are connected just yet \n");
        }

    }

    /**
     * This method makes the server start waiting for connections,
     * so clients can connect to the server.
     */
    public void startWaitingForConnections()
    {
        SCT = new ServerConnectionThread(server.GetServerSettings().port, this);
        Thread t = new Thread(SCT);
        t.setName("Server connection");
        t.start();
        listOfPlayers.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                Platform.runLater(()->{
                    connectedPlayers.setItems(FXCollections.observableArrayList(listOfPlayers));
                });
            }
        });

    }

    /**
     * Sets the screenParent for the controller
     * @param screenParent
     */
    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }

    /**
     * Initialize function of the controller which is called absolutely first.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
