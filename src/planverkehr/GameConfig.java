package planverkehr;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameConfig {
    JSONParser parser;
    MMap map;
    ArrayList<MVehicles> vehiclesList;
    HashMap<String, Buildings> buildingsList;
    List<String> commoditiesList;



    public GameConfig() throws JSONException {
        parser = new JSONParser();

        map = parser.getMapFromJSON();
        vehiclesList= parser.getVehiclesFromJSON();
        buildingsList = parser.getBuildingsFromJSON();
        commoditiesList = parser.getCommoditiesFromJSON();


    }


    public HashMap<String, Buildings> getBuildingsList() {
        return buildingsList;
    }

    public ArrayList<MVehicles> getVehiclesList() {
        return vehiclesList;
    }

    public List<String> getCommodities(){ return commoditiesList;}
}
