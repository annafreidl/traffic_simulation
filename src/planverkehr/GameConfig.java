package planverkehr;

import org.json.JSONException;

import java.util.List;

public class GameConfig {
    JSONParser parser;
    Map map;
    List<MVehicles> vehiclesList;
    List<Buildings> buildingsList;



    public GameConfig() throws JSONException {
        parser = new JSONParser();

        map = parser.getMapFromJSON();
        vehiclesList= parser.getVehiclesFromJSON();
        buildingsList = parser.getBuildingsFromJSON();
    }

    public List<Buildings> getBuildingsList() {
        return buildingsList;
    }
}
