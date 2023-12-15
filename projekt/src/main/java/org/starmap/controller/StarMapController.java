package org.starmap.controller;

import org.starmap.model.Constellation;
import org.starmap.model.Star;
import org.starmap.utils.DataLoader;

import java.util.List;
import java.util.Optional;

// Controller for managing the star map
public class StarMapController {
    private List<Star> stars;
    private List<Constellation> constellations;

    public StarMapController(String dataFilePath) {
        this.stars = DataLoader.loadStars(dataFilePath);
        this.constellations = DataLoader.loadConstellations(dataFilePath, stars);
    }

    public List<Star> getStars() {
        return stars;
    }

    public void setStars(List<Star> stars) {
        this.stars = stars;
    }

    public List<Constellation> getConstellations() {
        return constellations;
    }

    public void setConstellations(List<Constellation> constellations) {
        this.constellations = constellations;
    }

    // Get a star by its name
    public Optional<Star> getStarByName(String name) {
        return stars.stream().filter(star -> star.getName().equalsIgnoreCase(name)).findFirst();
    }

    // Get a constellation by its name
    public Optional<Constellation> getConstellationByName(String name) {
        return constellations.stream().filter(constellation -> constellation.getName().equalsIgnoreCase(name)).findFirst();
    }

    // Add a new star to the map
    public void addStar(Star star) {
        stars.add(star);
    }

    // Remove a star from the map
    public void removeStar(String name) {
        stars.removeIf(star -> star.getName().equalsIgnoreCase(name));
    }

    // Add a new constellation to the map
    public void addConstellation(Constellation constellation) {
        constellations.add(constellation);
    }

    // Remove a constellation from the map
    public void removeConstellation(String name) {
        constellations.removeIf(constellation -> constellation.getName().equalsIgnoreCase(name));
    }
}
