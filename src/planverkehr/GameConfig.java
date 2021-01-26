package planverkehr;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class GameConfig {
    JSONParser parser;
    MMap map;
    ArrayList<MVehicles> vehiclesList;
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

    public ArrayList<MVehicles> getVehiclesList() {
        return vehiclesList;
    }
}
