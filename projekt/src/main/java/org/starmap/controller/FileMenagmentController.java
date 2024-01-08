package org.starmap.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.starmap.model.Constellation;
import org.starmap.model.Star;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileMenagmentController {
    public FileMenagmentController(){}
    //Load Stars from Json and return as ArrayList
    public static List<Star> loadStars(String filePath) {
        List<Star> stars = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray starsJson = jsonObject.getJSONArray("stars");

            for (int i = 0; i < starsJson.length(); i++) {
                JSONObject starJson = starsJson.getJSONObject(i);
                Star star = new Star(
                        starJson.getString("name"),
                        starJson.getDouble("xPosition"),
                        starJson.getDouble("yPosition"),
                        starJson.getDouble("brightness")
                );
                stars.add(star);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stars;
    }
    //Load Constelations from json and return as ArrayList
    public static List<Constellation> loadConstellations(String filePath, List<Star> stars) {
        List<Constellation> constellations = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray constellationsJson = jsonObject.getJSONArray("constellations");

            for (int i = 0; i < constellationsJson.length(); i++) {
                JSONObject constellationJson = constellationsJson.getJSONObject(i);
                List<Star> constellationStars = new ArrayList<>();
                JSONArray starNames = constellationJson.getJSONArray("stars");

                for (int j = 0; j < starNames.length(); j++) {
                    String starName = starNames.getString(j);
                    stars.stream()
                            .filter(star -> star.getName().equals(starName))
                            .findFirst()
                            .ifPresent(constellationStars::add);
                }

                Constellation constellation = new Constellation(
                        constellationJson.getString("name"),
                        constellationStars
                );
                constellations.add(constellation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return constellations;
    }
    //Write a whole controller to a Json file
    public static void writeToJsonFile(String filePath, StarMapController controller){
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
        try{
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(fileObject.toString());
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
