package planverkehr;

import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JSONParser {

    final String filename = (Config.jsonFile);
    final InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
    final JSONObject json = new JSONObject(new JSONTokener(is));


    List<String> commodities;

    public JSONParser() throws JSONException {
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
            }
        }
        return commodities;
    }


    public MMap getMapFromJSON() {

        String mapGen;
        String gameMode;
        int width;
        int depth;
        MMap map;

        JSONObject mapObject = json.getJSONObject("map");

        mapGen = mapObject.getString("mapgen");
        gameMode = mapObject.getString("gamemode");
        width = mapObject.getInt("width");
        depth = mapObject.getInt("depth");

        map = new MMap(mapGen, gameMode, width, depth);

        System.out.println(mapGen);
        System.out.println(gameMode);
        System.out.println(width);
        System.out.println(depth);

        return map;
    }


    public List<MVehicles> getVehiclesFromJSON() {

        List<MVehicles> MVehiclesList = new ArrayList<>();
        String kind;
        String graphic;
        HashMap<String, Integer> cargo;
        double speed;

        JSONObject vehiclesObject = json.getJSONObject("vehicles");

        Iterator<String> keys = vehiclesObject.keys();

        while (keys.hasNext()) {
            String keyValue = keys.next();
            JSONObject singleVehicle = vehiclesObject.getJSONObject(keyValue);

            kind = singleVehicle.getString("kind");
            graphic = singleVehicle.getString("graphic");
            speed = singleVehicle.getDouble("speed");

            if (singleVehicle.has("cargo")) {
                cargo = new HashMap<>();

                try {
                    JSONObject cargoObject = singleVehicle.getJSONObject("cargo");

                    if (cargoObject.has("sand")) {
                        try {
                            cargo.put("sand", cargoObject.getInt("sand"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (cargoObject.has("glass")) {
                        try {
                            cargo.put("glass", cargoObject.getInt("glass"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (cargoObject.has("silicon")) {
                        try {
                            cargo.put("silicon", cargoObject.getInt("silicon"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (cargoObject.has("solar panels")) {
                        try {
                            cargo.put("solar panels", cargoObject.getInt("solar panels"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (cargoObject.has("methyl chloride")) {
                        try {
                            cargo.put("methyl chloride", cargoObject.getInt("methyl chloride"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (cargoObject.has("silicone")) {
                        try {
                            cargo.put("silicone", cargoObject.getInt("silicone"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray cargoArray = singleVehicle.getJSONArray("cargo");
                    for (int i = 0; i < cargoArray.length(); i++) {
                        try {
                            JSONObject cargoObject = cargoArray.getJSONObject(i);
                            if (cargoObject.has("sand")) {
                                try {
                                    cargo.put("sand", cargoObject.getInt("sand"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (cargoObject.has("glass")) {
                                try {
                                    cargo.put("glass", cargoObject.getInt("glass"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (cargoObject.has("silicon")) {
                                try {
                                    cargo.put("silicon", cargoObject.getInt("silicon"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (cargoObject.has("solar panels")) {
                                try {
                                    cargo.put("solar panels", cargoObject.getInt("solar panels"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (cargoObject.has("methyl chloride")) {
                                try {
                                    cargo.put("methyl chloride", cargoObject.getInt("methyl chloride"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (cargoObject.has("silicone")) {
                                try {
                                    cargo.put("silicone", cargoObject.getInt("silicone"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

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

//            System.out.println();
//            System.out.println(keyValue);
//            System.out.println(kind);
//            System.out.println(graphic);
//            System.out.println(cargo);
//            System.out.println(speed);
//            System.out.println();
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
        java.util.Map<String, Object> combines;
        List<Object> productions;


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

            if (singleBuilding.has("roads")) {
                roads = new ArrayList<>();
                roadsArray = singleBuilding.getJSONArray("roads");
                for (int i = 0; i < roadsArray.length(); i++) {
                    JSONArray singleRoadsArray = roadsArray.getJSONArray(i);
                    for (int j = 0; j < 1; j++) {
                        roads.add(new Pair<>(singleRoadsArray.getString(j), singleRoadsArray.getString(j + 1)));
                    }
                }
            } else {
                roads = new ArrayList<>();
            }


            if (singleBuilding.has("rails")) {
                rails = new ArrayList<>();
                JSONArray railsArray = singleBuilding.getJSONArray("rails");
                for (int i = 0; i < railsArray.length(); i++) {
                    JSONArray singleRailsArray = railsArray.getJSONArray(i);
                    for (int j = 0; j < 1; j++) {
                        rails.add(new Pair<>(singleRailsArray.getString(j), singleRailsArray.getString(j + 1)));
                    }
                }
            } else {
                rails = new ArrayList<>();
            }

            if (singleBuilding.has("planes")) {
                planes = new ArrayList<>();
                JSONArray planesArray = singleBuilding.getJSONArray("planes");
                for (int i = 0; i < planesArray.length(); i++) {
                    JSONArray singlePlanesArray = planesArray.getJSONArray(i);
                    for (int j = 0; j < 1; j++) {
                        planes.add(new Pair<>(singlePlanesArray.getString(j), singlePlanesArray.getString(j + 1)));
                    }
                }
            } else {
                planes = new ArrayList<>();
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
                    MCoordinate tempCoordinate = new MCoordinate(xCoord, yCoord);
                    points.put(pointId, tempCoordinate);

                }

            } else {
                points = new HashMap<>();
            }


            if (singleBuilding.has("combines")) {
                combinesObject = singleBuilding.getJSONObject("combines");
                combines = combinesObject.toMap();
            } else {
                combines = new HashMap<>();
            }


            if (singleBuilding.has("productions")) {
                productions = new ArrayList<>();
                try {
                    JSONArray productionsArray = singleBuilding.getJSONArray("productions");
                    for (int a = 0; a <= productionsArray.length(); a++) {
                        JSONObject productionsObject = productionsArray.getJSONObject(a);
                        productions.add(productionsObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject productionsObject = singleBuilding.getJSONObject("productions");
                    productions.add(productionsObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                productions = new ArrayList<>();
            }


            buildingsList.put(keyValue, new Buildings(keyValue, buildMenu, width, depth, points, roads, rails, planes, dz, special, maxPlanes, combines, productions));
//            System.out.println(keyValue);
//            System.out.println("buildmenu " + buildMenu);
//            System.out.println("width " + width);
//            System.out.println("depth " + depth);
//            System.out.println("maxplanes " + maxPlanes);
//            System.out.println("combines " + combines);
//            System.out.println("roads " + roads);
//            System.out.println("rails " + rails);
//            System.out.println("planes " + planes);
//            System.out.println("points " + points);
//            System.out.println("dz " + dz);
//            System.out.println("special " + special);
//            System.out.println("productions " + productions);
//            System.out.println();
//            System.out.println();

        }
        return buildingsList;
    }
}
