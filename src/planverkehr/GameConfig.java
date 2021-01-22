package planverkehr;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

public class GameConfig {
    JSONParser parser;
    MMap map;
    List<MVehicles> vehiclesList;
    HashMap<String, Buildings> buildingsList;



    public GameConfig() throws JSONException {
        parser = new JSONParser();

        map = parser.getMapFromJSON();
        vehiclesList= parser.getVehiclesFromJSON();
        buildingsList = parser.getBuildingsFromJSON();

    }

    public HashMap<String, Buildings> getBuildingsList() {
        return buildingsList;
    }
}
