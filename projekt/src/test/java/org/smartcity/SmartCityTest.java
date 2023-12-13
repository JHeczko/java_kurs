package org.smartcity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SmartCityTest {
    @Test
    public void testAddRemoveBuilding() {
        SmartCity city = new SmartCity();
        Building apartment = new Apartment("Test Address", 5, 30);
        city.addBuilding(apartment);

        assertEquals(1, city.getBuildings().size());

        city.removeBuilding(apartment);
        assertEquals(0, city.getBuildings().size());
    }
}
