package tobdyh131;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tobias on 2015-12-23.
 *
 * This is the class the controls the screens to be shown (the GUI).
 * Handles loading screens and switching between them.
 */
public class ScreensController extends StackPane {

    private HashMap<String, Node> screens = new HashMap<>();


    public Node getScreen(String name)
    {
        return screens.get(name);
    }

    public void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }


    /**
     * Loads a screen.
     * @param name Name of the screen.
     * @param resource Which resource to be loaded.
     * @param c The client to be passed to the controller.
     * @return True if succeeded, false if not.
     */
    public boolean loadScreen(String name, String resource, Client c) {
        try {
            FXMLLoader myLoader = new
                    FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) myLoader.load();
            ControlledScreen myScreenController =
                    ((ControlledScreen) myLoader.getController());
            myScreenController.setScreenParent(this);

            myLoader.<ClientPlayingController>getController().myClient = c;

            addScreen(name, loadScreen);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Loads a screen.
     * @param name Name of the screen.
     * @param resource Which resource to be loaded.
     * @param s The server to be passed to the controller.
     * @return True if succeeded, false if not.
     */
    public boolean loadScreen(String name, String resource, Server s) {
        try {
            FXMLLoader myLoader = new
                    FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) myLoader.load();
            ControlledScreen myScreenController =
                    ((ControlledScreen) myLoader.getController());
            myScreenController.setScreenParent(this);

            myLoader.<ServerDuringConnectionController>getController().server = s;
            myLoader.<ServerDuringConnectionController>getController().startWaitingForConnections();
            s.controller = myLoader.<ServerDuringConnectionController>getController();

            addScreen(name, loadScreen);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Loads a screen.
     * @param name Name of the screen.
     * @param resource Which resource to be loaded.
     * @param s The server settings to be passed to the controller.
     * @return True if succeeded, false if not.
     */
    public boolean loadScreen(String name, String resource, ServerSettings s) {
        try {
            FXMLLoader myLoader = new
                    FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) myLoader.load();
            ControlledScreen myScreenController =
                    ((ControlledScreen) myLoader.getController());
            myScreenController.setScreenParent(this);

            myLoader.<ServerPlayingController>getController().settings = s;

            addScreen(name, loadScreen);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Loads a screen.
     * @param name Name of the screen.
     * @param resource Which resource to be loaded.
     * @param ConsecutiveRound If true, then load settings from previous session. If false, don't.
     * @return True if succeeded, false if not.
     */
    public boolean loadScreen(String name, String resource, boolean ConsecutiveRound) {
        try {
            FXMLLoader myLoader = new
                    FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) myLoader.load();
            ControlledScreen myScreenController =
                    ((ControlledScreen) myLoader.getController());
            myScreenController.setScreenParent(this);

            if(ConsecutiveRound)
            {
                myLoader.<ServerStartupController>getController().ReadSettingsFromFile();
            }

            addScreen(name, loadScreen);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Loads a screen.
     * @param name Name of the screen.
     * @param resource Which resource to be loaded.
     * @param highscoreList The high score list to be passed to the controller.
     * @return True if succeeded, false if not.
     */
    public boolean loadScreen(String name, String resource, ObservableList<HighscoreInfo> highscoreList) {
        try {
            FXMLLoader myLoader = new
                    FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) myLoader.load();
            ControlledScreen myScreenController =
                    ((ControlledScreen) myLoader.getController());
            myScreenController.setScreenParent(this);

            myLoader.<HighscoreController>getController().highscoreList = highscoreList;

            addScreen(name, loadScreen);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Loads a screen.
     * @param name Name of the screen.
     * @param resource Which resource to be loaded.
     * @return True if succeeded, false if not.
     */
    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader myLoader = new
                    FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) myLoader.load();
            ControlledScreen myScreenController =
                    ((ControlledScreen) myLoader.getController());
            myScreenController.setScreenParent(this);
            addScreen(name, loadScreen);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Sets which screen to be shown.
     * @param name Name of the screen to be shown.
     * @return True if succeded, false if not.
     */
    public boolean setScreen(final String name) {

        if (screens.get(name) != null) { //screen loaded

            //Is there is more than one screen
            if (!getChildren().isEmpty()) {
                //remove displayed screen
                getChildren().remove(0);
                //add new screen
                getChildren().add(0, screens.get(name));
            } else {
                //no one else been displayed, then just show
                getChildren().add(screens.get(name));

                return true;
            }
        } else {
            System.out.println("screen hasn't been loaded!\n");
            return false;
        }


        return false;
    }

    /**
     * Unloads a certain scene.
     * @param name Name of the screen to be unloaded.
     * @return True if succeded, false it not.
     */
    public boolean unloadScreen(String name) {
        if(screens.remove(name) == null) {
            System.out.println("Screen didn't exist");
            return false;
        } else {
            return true;
        }
    }



}
