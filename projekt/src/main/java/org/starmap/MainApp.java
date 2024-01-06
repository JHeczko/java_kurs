package org.starmap;

import javafx.application.Application;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.*;
import org.starmap.controller.FileMenagmentController;
import org.starmap.controller.StarMapController;
import org.starmap.view.FileControllerView;
import org.starmap.view.MenuView;
import org.starmap.view.StarMapView;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;

// Main application class for the star map
public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setFullScreen(false);
        primaryStage.setFullScreenExitHint("Esc to exit fullscreen");

        StarMapController controller = new StarMapController("src/main/resources/stars.json");
        StarMapView view = new StarMapView(controller);

        FileControllerView fileControllerView = new FileControllerView(controller);

        MenuView menuBar = new MenuView();
        menuBar.setFileControllerView(fileControllerView);
        menuBar.addMenu("Functions",new ArrayList<String>(Arrays.asList("Star Options","Import Json","Save to Json","Show Grid","Hide Grid","Delete All Stars")));
        menuBar.buttonsEventHandlers(view);

        GridPane root = new GridPane(); // Create Grid container
        root.add(menuBar,0,0);
        root.add(view,0,1);

        Scene scene = new Scene(root, 1280, 768, Color.BLACK); // Create Scene with Group container

        Image icon = new Image("icon.png"); //setting new icon
        primaryStage.getIcons().add(icon);

        //Resizer if we chnge size of window
        primaryStage.widthProperty().addListener((obs,oldVal,NewVal) -> {
            view.resize(scene.getHeight(),scene.getWidth());
        });
        primaryStage.heightProperty().addListener((obs,oldVal,NewVal) -> {
            view.resize(scene.getHeight(),scene.getWidth());
        });

        primaryStage.setTitle("Star Map");
        primaryStage.setScene(scene);
        primaryStage.show();
        view.drawMap(); // Call this after the scene is shown
    }
}
