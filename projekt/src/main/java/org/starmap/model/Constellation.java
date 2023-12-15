package org.starmap.model;

import java.util.List;

// Represents a constellation made up of stars
public class Constellation {
    final private String name;
    final private List<Star> stars;

    public Constellation(String name, List<Star> stars) {
        this.name = name;
        this.stars = stars;
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<Star> getStars() {
        return stars;
    }
}
