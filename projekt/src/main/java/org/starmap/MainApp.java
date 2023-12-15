package org.starmap;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.starmap.controller.StarMapController;
import org.starmap.view.StarMapView;

// Main application class for the star map
public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StarMapController controller = new StarMapController("src/main/resources/stars.json");
        StarMapView view = new StarMapView(controller);

        Group root = new Group(); // Create Group container
        root.getChildren().add(view); // Add StarMapView to container

        Scene scene = new Scene(root, 1024, 768); // Create Scene with Group container

        primaryStage.setTitle("Star Map");
        primaryStage.setScene(scene);
        primaryStage.show();
        view.drawMap(); // Call this after the scene is shown
    }
}
