package org.starmap.model;

// Represents a star in the star map
public class Star {
    final private String name;
    final private double xPosition;
    final private double yPosition;
    final private double brightness;

    public Star(String name, double xPosition, double yPosition, double brightness) {
        this.name = name;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.brightness = brightness;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public double getXPosition() {
        return xPosition;
    }

    public double getYPosition() {
        return yPosition;
    }

    public double getBrightness() {
        return brightness;
    }
}
