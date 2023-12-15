package org.starmap.view;

import javafx.animation.PauseTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.starmap.controller.StarMapController;
import org.starmap.model.Constellation;
import org.starmap.model.Star;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StarMapView extends Canvas {
    private final StarMapController controller;
    private PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
    private Star currentHoveredStar = null;
    private Map<String, Color> constellationColors = new HashMap<>();

    public StarMapView(StarMapController controller) {
        this.controller = controller;
        this.setWidth(1024); // Set canvas width
        this.setHeight(768); // Set canvas height
        drawMap();
        initializeConstellationColors();
        addMouseMotionListener();
    }

    private void initializeConstellationColors() {
        List<Constellation> constellations = controller.getConstellations();
        for (Constellation constellation : constellations) {
            int hash = constellation.getName().hashCode();
            Random rand = new Random(hash); // Use hash as a seed for random generator
            Color color = new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1);
            constellationColors.put(constellation.getName(), color);
        }
    }

    public void drawMap() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight()); // Set background to black
        drawStars();
        drawConstellations();
    }

    private void drawStars() {
        GraphicsContext gc = getGraphicsContext2D();
        List<Star> stars = controller.getStars();
        for (Star star : stars) {
            double brightnessScale = star.getBrightness() / 2.0; // Scale brightness
            double starSize = 2 + (5 - brightnessScale); // Calculate star size
            Color starColor = Color.hsb(60, 0.5, 1 - 0.2 * brightnessScale); // Color based on brightness
            drawStar(gc, star.getXPosition(), star.getYPosition(), starSize, starColor);
        }
    }

    private void drawStar(GraphicsContext gc, double x, double y, double size, Color color) {
        double[] xPoints = new double[10];
        double[] yPoints = new double[10];
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 5 * i;
            double radius = i % 2 == 0 ? size : size / 2;
            xPoints[i] = x + radius * Math.sin(angle);
            yPoints[i] = y - radius * Math.cos(angle);
        }
        gc.setStroke(color);
        gc.strokePolyline(xPoints, yPoints, 10);
    }

    private void drawConstellations() {
        GraphicsContext gc = getGraphicsContext2D();
        List<Constellation> constellations = controller.getConstellations();

        for (Constellation constellation : constellations) {
            Color lineColor = constellationColors.getOrDefault(constellation.getName(), Color.BLUE);
            gc.setStroke(lineColor);
            gc.setLineWidth(1);
            gc.setFill(lineColor);
            gc.setFont(new Font("Arial", 14));

            List<Star> starsInConstellation = constellation.getStars();
            for (int i = 0; i < starsInConstellation.size() - 1; i++) {
                Star start = starsInConstellation.get(i);
                Star end = starsInConstellation.get(i + 1);
                gc.strokeLine(start.getXPosition(), start.getYPosition(), end.getXPosition(), end.getYPosition());
            }

            // Rysuj nazwę konstelacji obok pierwszej gwiazdy
            if (!starsInConstellation.isEmpty()) {
                Star firstStar = starsInConstellation.get(0);
                gc.fillText(constellation.getName(), firstStar.getXPosition(), firstStar.getYPosition() - 15);
            }
        }
    }

    private void addMouseMotionListener() {
        this.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            Star foundStar = null;

            List<Star> stars = controller.getStars();
            for (Star star : stars) {
                if (Math.abs(mouseX - star.getXPosition()) < 10 && Math.abs(mouseY - star.getYPosition()) < 10) {
                    foundStar = star;
                    break;
                }
            }

            if (foundStar != null && foundStar != currentHoveredStar) {
                currentHoveredStar = foundStar;
                pause.stop(); // Zatrzymaj poprzednie opóźnienie
                drawStarName(foundStar);
            } else if (foundStar == null && currentHoveredStar != null) {
                pause.setOnFinished(e -> {
                    hideStarName();
                    currentHoveredStar = null;
                });
                pause.playFromStart();
            }
        });
    }

    private void drawStarName(Star star) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillText(star.getName(), star.getXPosition() + 10, star.getYPosition() - 10);
    }

    private void hideStarName() {
        if (currentHoveredStar != null) {
            pause.setOnFinished(e -> {
                clearCanvas();
                drawMap(); // Rysuj wszystko od nowa
            });
            pause.playFromStart();
        }
    }

    private void clearCanvas() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }
}
