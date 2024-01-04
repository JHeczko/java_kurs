package org.starmap.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.starmap.controller.StarMapController;
import org.starmap.model.Constellation;
import org.starmap.model.Star;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataWriterTest {
    StarMapController starMapController;
    @TempDir
    Path tempDir;
    private Path testFilePath;

    @BeforeEach
    void setUp() throws IOException {
        testFilePath = tempDir.resolve("test.json");
        String testJson = """
                {
                  "stars": [
                    {
                      "name": "Sirius",
                      "xPosition": 100,
                      "yPosition": 200,
                      "brightness": 1.46
                    },
                    {
                      "name": "Canopus",
                      "xPosition": 150,
                      "yPosition": 250,
                      "brightness": 0.72
                    },
                    {
                      "name": "Aldebaran",
                      "xPosition": 50,
                      "yPosition": 400,
                      "brightness": 0.85
                    },
                    {
                      "name": "Elnath",
                      "xPosition": 100,
                      "yPosition": 450,
                      "brightness": 1.65
                    }
                  ],
                  "constellations": [
                    {
                      "name": "Taurus",
                      "stars": [
                        "Aldebaran",
                        "Elnath"
                      ]
                    }
                  ]
                }""";
        Files.writeString(testFilePath, testJson);
        starMapController = new StarMapController(testFilePath.toString());
    }
    @Test
    void isFilesTheSame() throws IOException{
       DataWriter dataWriter = new DataWriter(starMapController);
       dataWriter.writeToJsonFile("test");

       //Delete created file after test
       File my_file = new File("src/main/resources/test.json");
       my_file.deleteOnExit();

       StarMapController testStarController = new StarMapController(my_file.getPath());

       assertEquals(testStarController.getStars().get(3).getName(),starMapController.getStars().get(3).getName(),"Two stars should be equal");

        assertEquals(testStarController.getConstellations().get(0).getName(),starMapController.getConstellations().get(0).getName(),"Two constellations should be equal");
    }


}
