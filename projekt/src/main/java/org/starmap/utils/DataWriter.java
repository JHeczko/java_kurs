package org.starmap.utils;

import java.io.*;
import java.util.List;
import java.util.logging.FileHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.starmap.controller.StarMapController;
import org.starmap.model.Constellation;
import org.starmap.model.Star;


public class DataWriter {

    public DataWriter(){}

    void writeToJsonFile(String filePath, StarMapController controller) throws IOException, ObjectStreamException {
        List<Star> stars = controller.getStars();
        List<Constellation> constellations = controller.getConstellations();
        
        JSONObject fileObject = new JSONObject(); //Output Object
        JSONArray arrStars = new JSONArray();
        JSONArray arrConstelations = new JSONArray();
        //Saving star to a JSONarray
        for(Star star : stars){
            JSONObject tempJsonObject = new JSONObject();

            tempJsonObject.put("brightness",star.getBrightness());
            tempJsonObject.put("xPosition",star.getXPosition());
            tempJsonObject.put("yPosition",star.getYPosition());
            tempJsonObject.put("name",star.getName());

            arrStars.put(tempJsonObject);
        }
        // Saving constellation to a JSONArray
        for(Constellation constellation : constellations){
            JSONObject constelationObject = new JSONObject();
            constelationObject.put("name",constellation.getName());
            JSONArray starsArray = new JSONArray();
            //Saving stars names to an array "stars" in JSON file in constellation array
            for(Star star : constellation.getStars()){
                starsArray.put(star.getName());
            }
            constelationObject.put("stars",starsArray);
            arrConstelations.put(constelationObject);
        }

        fileObject.put("constellations",arrConstelations);
        fileObject.put("stars",arrStars);
        
        //Create and write to a file
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(fileObject.toString());
        fileWriter.close();
    }

}
