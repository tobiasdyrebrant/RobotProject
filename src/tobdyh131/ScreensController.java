package tobdyh131;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.time.Duration;
import java.util.HashMap;

/**
 * Created by Tobias on 2015-12-23.
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

    public boolean unloadScreen(String name) {
        if(screens.remove(name) == null) {
            System.out.println("Screen didn't exist");
            return false;
        } else {
            return true;
        }
    }



}
