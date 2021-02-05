package planverkehr;

import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.*;

public class JSONParser {

    final String filename;
    final InputStream is;
    final JSONObject json;


    List<String> commodities;

    public JSONParser() throws JSONException {
        filename = (Config.jsonFile);
        is = this.getClass().getClassLoader().getResourceAsStream(filename);
        json = new JSONObject(new JSONTokener(is));
    }


    public List<String> getCommoditiesFromJSON() {
        if (json.has("commodities")) {
            commodities = new ArrayList<>();
            try {
                JSONArray commoditiesArray = json.getJSONArray("commodities");
                for (int i = 0; i < commoditiesArray.length(); i++) {
                    commodities.add(commoditiesArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                programmBeenden();
            }
        } else {
            System.out.println("No Commodities found!");
            programmBeenden();
        }
        return commodities;
    }

    private void programmBeenden() {
        System.out.println("Programm wird beendet!");
        System.exit(-1);
    }

    public MMap getMapFromJSON() {

        String mapGen;
        String gameMode;
        int width;
        int depth;
        MMap map = null;

        if (json.has("map")) {
            try {
                JSONObject mapObject = json.getJSONObject("map");
                mapGen = getSTRING(mapObject, "mapgen");
                gameMode = getSTRING(mapObject, "gamemode");
                width = getINT(mapObject, "width");
                depth = getINT(mapObject, "depth");
                map = new MMap(mapGen, gameMode, width, depth);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No Map found");
            programmBeenden();
        }
        return map;
    }

    private int getINT(JSONObject mapObject, String extract) {
        int value;
        if (mapObject.has(extract)) {
            try {
                value = mapObject.getInt(extract);
            } catch (JSONException e) {
                value = 0;
                e.printStackTrace();
                programmBeenden();
            }
        } else {
            value = 0;
            System.out.println("No attibute " + extract + "found");
            programmBeenden();
        }
        return value;
    }

    private List<Pair<String, String>> getPairs(JSONObject singleBuilding, String kind) {
        JSONArray pairArray;
        List<Pair<String, String>> pairList;
        if (singleBuilding.has(kind)) {
            pairList = new ArrayList<>();
            pairArray = singleBuilding.getJSONArray(kind);
            for (int i = 0; i < pairArray.length(); i++) {
                JSONArray pairArrayJSONArray = pairArray.getJSONArray(i);
                for (int j = 0; j < 1; j++) {
                    pairList.add(new Pair<>(pairArrayJSONArray.getString(j), pairArrayJSONArray.getString(j + 1)));
                }
            }
        } else {
            pairList = new ArrayList<>();
        }
        return pairList;
    }


    private double getDOUBLE(JSONObject mapObject, String extract) {
        double value;
        if (mapObject.has(extract)) {
            try {
                value = mapObject.getDouble(extract);
            } catch (JSONException e) {
                value = 0;
                e.printStackTrace();
                programmBeenden();
            }
        } else {
            value = 0;
            System.out.println("No attibute " + extract + "found");
            programmBeenden();
        }
        return value;
    }

    private String getSTRING(JSONObject singleVehicle, String string) {
        String stringValue;
        if (singleVehicle.has(string)) {
            try {
                stringValue = singleVehicle.getString(string);
            } catch (JSONException e) {
                stringValue = "";
                e.printStackTrace();
            }
        } else {
            stringValue = "";
            programmBeenden();
        }
        return stringValue;
    }

    private void getCargo(HashMap<String, Integer> cargo, JSONObject cargoObject, String kind) {
        if (cargoObject.has(kind)) {
            try {
                cargo.put(kind, cargoObject.getInt(kind));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public ArrayList<MVehicles> getVehiclesFromJSON() {

        ArrayList<MVehicles> MVehiclesList = new ArrayList<>();
        String kind;
        String graphic;
        HashMap<String, Integer> cargo;
        double speed;
        JSONObject singleVehicle = null;

        JSONObject vehiclesObject = null;

        if (json.has("vehicles")) {
            try {
                vehiclesObject = json.getJSONObject("vehicles");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No vehicles found");
            programmBeenden();
        }

        assert vehiclesObject != null;
        Iterator<String> keys = vehiclesObject.keys();

        while (keys.hasNext()) {
            String keyValue = keys.next();

            try {
                singleVehicle = vehiclesObject.getJSONObject(keyValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            kind = getSTRING(singleVehicle, "kind");
            graphic = getSTRING(singleVehicle, "graphic");
            speed = getDOUBLE(singleVehicle, "speed");

            if (singleVehicle.has("cargo")) {
                cargo = new HashMap<>();
                try {
                    JSONObject cargoObject = singleVehicle.getJSONObject("cargo");
                    for (String commodity : getCommoditiesFromJSON()) {
                        getCargo(cargo, cargoObject, commodity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray cargoArray = singleVehicle.getJSONArray("cargo");
                    for (int i = 0; i < cargoArray.length(); i++) {
                        try {
                            JSONObject cargoObject = cargoArray.getJSONObject(i);
                            for (String commodity : getCommoditiesFromJSON()) {
                                getCargo(cargo, cargoObject, commodity);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                cargo = new HashMap<>();
            }
            MVehiclesList.add(new MVehicles(keyValue, kind, graphic, cargo, speed));
        }
        return MVehiclesList;
    }


    public HashMap<String, Buildings> getBuildingsFromJSON() {

        String buildMenu;
        int width;
        int depth;
        java.util.Map<String, MCoordinate> points;
        List<Pair<String, String>> roads;
        List<Pair<String, String>> rails;
        List<Pair<String, String>> planes;
        int dz;
        String special;
        int maxPlanes;
        java.util.Map<String, String> combines;
        List<Object> productions;

        List<MProductions> MProductionsList = null;
        int duration = 0;
        List<HashMap<String, Integer>> produceAll = null;
        List<HashMap<String, Integer>> consumeAll = null;
        HashMap<String, Integer> storage;


        HashMap<String, Buildings> buildingsList = new HashMap<>();

        JSONObject buildingsObject = json.getJSONObject("buildings");

        JSONObject singleBuilding = null;

        JSONObject pointsObject = null;

        JSONArray roadsArray = null;

        JSONObject combinesObject = null;

        Iterator<String> keys = buildingsObject.keys();

        while (keys.hasNext()) {

            String keyValue = keys.next();
            singleBuilding = buildingsObject.getJSONObject(keyValue);


            width = singleBuilding.optInt("width", 0);
            depth = singleBuilding.optInt("depth", 0);
            dz = singleBuilding.optInt("dz", 1337);
            maxPlanes = singleBuilding.optInt("maxplanes", 0);
            special = singleBuilding.optString("special", "noSpecial");
            buildMenu = singleBuilding.optString("buildmenu", "noBuildMenu");
            roads = getPairs(singleBuilding, "roads");
            rails = getPairs(singleBuilding, "rails");
            planes = getPairs(singleBuilding, "planes");


            if (singleBuilding.has("signals")) {
                if (special.equals("noSpecial")) {
                    special = "signal";
                } else {
                    System.out.println("!!! Signal wurde nicht als Special gesetzt !!!");
                }

            }

            if (singleBuilding.has("storage")) {
                storage = new HashMap<>();
                try {
                    JSONObject storageObject = singleBuilding.getJSONObject("storage");
                    for (String kind : getCommoditiesFromJSON()) {
                        getCargo(storage, storageObject, kind);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                storage = new HashMap<>();
            }


            if (singleBuilding.has("points")) {
                JSONArray singlePoint = null;
                pointsObject = singleBuilding.getJSONObject("points");
                Iterator<String> pointKeys = pointsObject.keys();
                points = new HashMap<>();

                while (pointKeys.hasNext()) {
                    String pointId = pointKeys.next();
                    singlePoint = pointsObject.getJSONArray(pointId);
                    double xCoord = singlePoint.getDouble(0);
                    double yCoord = singlePoint.getDouble(1);
                    MCoordinate tempCoordinate = new MCoordinate(xCoord, yCoord, 0);
                    points.put(pointId, tempCoordinate);

                }

            } else {
                points = new HashMap<>();
            }

            combines = new HashMap<>();
            if (singleBuilding.has("combines")) {
                combinesObject = singleBuilding.getJSONObject("combines");
                Set<String> combinesKeys = combinesObject.keySet();
                JSONObject finalCombinesObject = combinesObject;
                Map<String, String> finalCombines = combines;
                combinesKeys.forEach(key -> {
                    finalCombines.put(key, finalCombinesObject.get(key).toString());
                });

            }


            if (singleBuilding.has("productions")) {
                MProductionsList = new ArrayList<>();
                try {
                    JSONArray productionsArray = singleBuilding.getJSONArray("productions");
                    for (int a = 0; a <= productionsArray.length(); a++) {
                        JSONObject productionsObject = productionsArray.getJSONObject(a);

                        if (productionsObject.has("produce")) {
                            HashMap produce = new HashMap();
                            produceAll = new ArrayList<>();
                            try {
                                JSONObject produceObject = productionsObject.getJSONObject("produce");
                                for (String commodity : getCommoditiesFromJSON()) {
                                    getCargo(produce, produceObject, commodity);
                                }
                                produceAll.add(produce);
                                //System.out.println("Produce: " + keyValue + produceAll);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            produceAll = new ArrayList<>();
                        }

                        if (productionsObject.has("consume")) {
                            HashMap consume = new HashMap();
                            consumeAll = new ArrayList<>();
                            try {
                                JSONObject consumeObject = productionsObject.getJSONObject("consume");
                                for (String commodity : getCommoditiesFromJSON()) {
                                    getCargo(consume, consumeObject, commodity);
                                }
                                consumeAll.add(consume);
                                //System.out.println("ConsumeALL: " + keyValue + consumeAll);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            consumeAll = new ArrayList<>();
                        }
                        duration = getINT(productionsObject, "duration");
                        //System.out.println("Duration: " + keyValue + duration);
                        MProductionsList.add(new MProductions(duration, produceAll, consumeAll, storage));
                        System.out.println(keyValue + " Duration: " + duration + " Consume: " + consumeAll + " Produce: " + produceAll);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject productionsObject = singleBuilding.getJSONObject("productions");
                    if (productionsObject.has("produce")) {
                        HashMap produce = new HashMap();
                        produceAll = new ArrayList<>();
                        try {
                            JSONObject produceObject = productionsObject.getJSONObject("produce");
                            for (String commodity : getCommoditiesFromJSON()) {
                                getCargo(produce, produceObject, commodity);

                            }
                            produceAll.add(produce);
                            //System.out.println("Produce: " + keyValue + produceAll);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        produceAll = new ArrayList<>();
                    }
                    if (productionsObject.has("consume")) {
                        HashMap consume = new HashMap();
                        consumeAll = new ArrayList<>();
                        try {
                            JSONObject consumeObject = productionsObject.getJSONObject("consume");
                            for (String commodity : getCommoditiesFromJSON()) {
                                getCargo(consume, consumeObject, commodity);
                            }
                            consumeAll.add(consume);
                            //System.out.println("ConsumeALL: " + keyValue + consumeAll);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        consumeAll = new ArrayList<>();
                    }

                    duration = getINT(productionsObject, "duration");
                    //System.out.println("Duration: " + keyValue + duration);
                    MProductionsList.add(new MProductions(duration, produceAll, consumeAll, storage));
                    System.out.println(keyValue + " Duration: " + duration + " Consume: " + consumeAll + " Produce: " + produceAll);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                consumeAll = new ArrayList<>();
                produceAll = new ArrayList<>();
                MProductionsList = new ArrayList<>();
            }
            buildingsList.put(keyValue, new Buildings(keyValue, buildMenu, width, depth, points, roads, rails, planes, dz, special, maxPlanes, combines, MProductionsList));
        }
        return buildingsList;
    }
}
