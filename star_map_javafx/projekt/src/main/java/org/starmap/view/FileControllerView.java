package org.starmap.view;

import org.starmap.controller.FileMenagmentController;
import org.starmap.controller.StarMapController;
import org.starmap.model.Constellation;
import org.starmap.model.Star;

import java.util.List;



public class FileControllerView {

    private StarMapController controller;
    public FileControllerView(StarMapController controller){
        this.controller = controller;
    }
    public List<Star> loadStarsFromFile(String filePath){
        List<Star> stars = FileMenagmentController.loadStars(filePath);
        for(Star s : stars){
            controller.addStar(s);
        }
        return stars;
    }
    public List<Constellation> loadConstellationsFromFile(String filePath,List<Star> stars){
        List<Constellation> constellations = FileMenagmentController.loadConstellations(filePath,stars);
        for(Constellation c : constellations){
            controller.addConstellation(c);
        }
        return constellations;
    }

    public void writeConfigurationToFile(String filePath){
        FileMenagmentController.writeToJsonFile(filePath,this.controller);
    }


}
