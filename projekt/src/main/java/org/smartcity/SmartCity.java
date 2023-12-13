package org.smartcity;

import java.util.ArrayList;
import java.util.List;

public class SmartCity {
    private final List<Building> buildings;

    public SmartCity() {
        buildings = new ArrayList<>();
    }

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public void removeBuilding(Building building) {
        buildings.remove(building);
    }

    public List<Building> getBuildings() {
        return buildings;
    }
}
