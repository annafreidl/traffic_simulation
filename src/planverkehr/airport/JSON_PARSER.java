package planverkehr.airport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;



/*
public class JSON_PARSER {

    private List<String> targetTypeList;
    private List<String> kindList;
    private List<String> nameList;


    final String filename = (Config.jsonFile);
    final InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
    final JSONObject json = new JSONObject(new JSONTokener(is));



    public JSON_PARSER() throws JSONException {
        targetTypeList();
        kindList();
        nameList();

        if (!((planesInJSON()) || (generatorInJSON()))) {
            System.out.println("Weder ein Planes-Objekt, noch ein Generator-Objekt kann in " + filename + " ausgelesen werden.");
            System.out.println("Programm wird beendet!");
            System.exit(-1);

        }
        if (!nodesInJSON()) {
            System.out.println("Es koennen keine Node-Objekte in " + filename + " ausgelesen werden.");
            System.out.println("Programm wird beendet!");
            System.exit(-1);
        }
    }



    private void programmBeenden() {
        System.out.println("Programm wird beendet!");
        System.exit(-1);
    }



    private void targetTypeList() {
        targetTypeList = new ArrayList<>();
        targetTypeList.add("einflug");
        targetTypeList.add("gateway");
        targetTypeList.add("ausflug");
        targetTypeList.add("wait");
        targetTypeList.add("enteisen");
        targetTypeList.add("hangar");
        targetTypeList.add("tanken");
    }


    private void kindList() {
        kindList = new ArrayList<>();
        kindList.add("air");
        kindList.add("concrete");
        kindList.add("hangar");
        kindList.add("runway");
    }



    private void nameList() throws JSONException {
        String name;
        nameList = new ArrayList<>();
        JSONArray nodes = new JSONArray();

        try {
            nodes = json.getJSONArray("nodes");
        } catch (JSONException e) {
            System.out.println("Problem with nodes." + e);
        }

        for (int i = 0; i < nodes.length(); i++) {
            JSONObject currentObject = nodes.getJSONObject(i);

            if (currentObject.has("name")) {
                try {
                    name = currentObject.getString("name");
                    if (nameList.contains(name)) {
                        System.out.println("Knotenpunkt Duplikat " + name + " an der Stelle " + (i + 1) + " in " + filename + " entdeckt!");
                        programmBeenden();
                    }
                    nameList.add(name);
                } catch (JSONException e) {
                    System.out.println(e);
                    System.out.println("Knoten-Name von Nodes-Objekt " + (i + 1) + " in " + filename + " kann nicht ausgelesen werden.");
                    programmBeenden();
                }
            } else {
                System.out.println("Knoten-Name von Nodes-Objekt " + (i + 1) + " in " + filename + " kann nicht gefunden werden.");
                programmBeenden();
            }
        }
    }


    public int getMaxPlanesFromJSON() throws JSONException {

        int maxPlanesSubsitute = (int) (Config.maxPlanesDefaultFactor * nameList.size());
        int maxPlanes;

        if (json.has("maxplanes")) {
            try {
                maxPlanes = json.getInt("maxplanes");
                return maxPlanes;
            } catch (JSONException e) {
                System.out.println(e);
                System.out.println("Das Objekt 'maxplanes' in " + filename + " ist fehlerhaft. Als Standartwert wurde nun " + maxPlanesSubsitute + " benutzt.");
            }
        } else {
            System.out.println("Das Objekt 'maxplanes' in " + filename + " wurde nicht gefunden. Als Standartwert wurde nun " + maxPlanesSubsitute + " benutzt.");
        }
        return maxPlanesSubsitute;
    }



    public boolean planesInJSON() {
        return (json.has("planes"));
    }



    public Queue<MAirplane> getPlanesFromJSON() throws JSONException {

        Queue<MAirplane> planesQueue = new PriorityQueue<>();

        if (planesInJSON()) {
            String[] waypoint;
            int inittime;
            //PLANES ARRAY
            JSONArray planes = new JSONArray();
            int planeId = 0;

            try {
                planes = json.getJSONArray("planes");
            } catch (JSONException e) {
                System.out.println("Problem with planes " + e);
            }

            //Planes Array hat mehrere Objekte, we need to access them all
            for (int i = 0; i < planes.length(); i++) {
                JSONObject currentObject = planes.getJSONObject(i);

                //getting INITTIME
                if (currentObject.has("inittime")) {
                    try {
                        inittime = currentObject.getInt("inittime");
                    } catch (JSONException e) {
                        inittime = 0;
                        System.out.println(e);
                        System.out.println("Inittime von Planes-Objekt " + (i + 1) + " ist fehlerhaft.");
                        programmBeenden();
                    }
                } else {
                    inittime = 0;
                    System.out.println("Inittime von Planes-Objekt " + (i + 1) + " fehlt.");
                    programmBeenden();
                }

                //getting WAYPOINTS
                if (currentObject.has("waypoints")) {
                    try {
                        JSONArray waypointsArray = currentObject.getJSONArray("waypoints");
                        if (waypointsArray.length() <= 0) {
                            System.out.println("Waypoints von Planes-Objekt " + (i + 1) + " fehlen.");
                            programmBeenden();
                        }
                        waypoint = new String[waypointsArray.length()];
                        for (int j = 0; j < waypointsArray.length(); j++) {
                            if (targetTypeList.contains(waypointsArray.getString(j))) {
                                waypoint[j] = waypointsArray.getString(j);
                            } else {
                                System.out.println("Targettype " + waypointsArray.getString(j) + " in Waypoints-Liste von Planes-Objekt " + (i + 1) + " in " + filename + " nicht bekannt.");
                                System.out.println("Valide Eingaben sind: " + targetTypeList);
                                programmBeenden();
                            }
                        }
                    } catch (JSONException e) {
                        waypoint = new String[0];
                        System.out.println(e);
                        System.out.println("Waypoints von Planes-Objekt " + (i + 1) + " ist fehlerhaft.");
                        programmBeenden();
                    }
                } else {
                    waypoint = new String[0];
                    System.out.println("Waypoints von Planes-Objekt " + (i + 1) + " fehlt.");
                    programmBeenden();
                }
                planesQueue.add(new MAirplane(waypoint, inittime, planeId));
                planeId++;
            }
        } else {
            System.out.println("Planes Objekt in " + filename + " nicht gefunden.");
        }
        return planesQueue;
    }


    public boolean generatorInJSON() {
        return (json.has("generators"));
    }



    public List<Generator> getGeneratorsFromJSON() throws JSONException {

        targetTypeList();
        List<Generator> generatorList = new ArrayList<>();

        if (generatorInJSON()) {
            double chance;
            double default_chance = Config.generatorDefaultChance;
            String[] waypoint;
            //GENERATOR ARRAY
            JSONArray generators = new JSONArray();

            try {
                generators = json.getJSONArray("generators");
            } catch (JSONException e) {
                System.out.println("Problem with generators." + e);
            }

            for (int i = 0; i < generators.length(); i++) {
                JSONObject currentObject = generators.getJSONObject(i);

                if (currentObject.has("chance")) {
                    try {
                        chance = currentObject.getDouble("chance");
                    } catch (JSONException e) {
                        chance = default_chance;
                        System.out.println(e);
                    }
                } else {
                    chance = default_chance;
                    System.out.println("Chace von Generator-Objekt " + (i + 1) + " fehlt.");
                    System.out.println("Als Defaultwert wird " + default_chance + " genutzt.");
                }

                //getting WAYPOINTS
                if (currentObject.has("waypoints")) {
                    try {
                        JSONArray waypointsArray = currentObject.getJSONArray("waypoints");
                        if (waypointsArray.length() <= 0) {
                            System.out.println("Waypoints von Generator-Objekt " + (i + 1) + " fehlen.");
                            programmBeenden();
                        }
                        waypoint = new String[waypointsArray.length()];
                        for (int j = 0; j < waypointsArray.length(); j++) {
                            if (targetTypeList.contains(waypointsArray.getString(j))) {
                                waypoint[j] = waypointsArray.getString(j);
                            } else {
                                System.out.println("Targettype " + waypointsArray.getString(j) + " in Waypoints-Liste von Generator-Objekt " + (i + 1) + " in " + filename + " nicht bekannt.");
                                System.out.println("Valide Eingaben sind: " + targetTypeList);
                                programmBeenden();
                            }
                        }
                    } catch (JSONException e) {
                        waypoint = new String[0];
                        System.out.println(e);
                        System.out.println("Waypoints von Generator-Objekt " + (i + 1) + " ist fehlerhaft.");
                        programmBeenden();
                    }
                } else {
                    waypoint = new String[0];
                    System.out.println("Waypoints von Planes-Objekt " + (i + 1) + " fehlt.");
                    programmBeenden();
                }
                generatorList.add(new Generator(waypoint, chance));
            }
        } else {
            System.out.println("Generator-Objekt in " + filename + " nicht gefunden.");
        }
        return generatorList;
    }



    private boolean nodesInJSON() {
        return (json.has("nodes"));
    }



    public ArrayList<Knotenpunkt> getNodesFromJSON() throws JSONException {

        ArrayList<Knotenpunkt> nodeList = new ArrayList<>();

        String targettype;
        String[] conflict;
        int waittime;
        double x;
        double y;
        String name;
        String kind;
        String[] to;

        //NODES ARRAY
        JSONArray nodes = new JSONArray();

        try {
            nodes = json.getJSONArray("nodes");
        } catch (JSONException e) {
            System.out.println("Problem with nodes." + e);
        }

        for (int i = 0; i < nodes.length(); i++) {
            JSONObject currentObject = nodes.getJSONObject(i);

            //getting X
            if (currentObject.has("x")) {
                try {
                    x = currentObject.getDouble("x");
                } catch (JSONException e) {
                    x = 0;
                    System.out.println(e);
                    System.out.println("X-Koordinate von Nodes-Objekt " + (i + 1) + " in " + filename + " kann nicht ausgelesen werden.");
                    programmBeenden();
                }
            } else {
                x = 0;
                System.out.println("X-Koordinate von Nodes-Objekt " + (i + 1) + " in " + filename + " fehlt.");
                programmBeenden();
            }

            //getting Y
            if (currentObject.has("y")) {
                try {
                    y = currentObject.getDouble("y");
                } catch (JSONException e) {
                    y = 0;
                    System.out.println(e);
                    System.out.println("Y-Koordinate von Nodes-Objekt " + (i + 1) + " in " + filename + " kann nicht ausgelesen werden.");
                    programmBeenden();
                }
            } else {
                y = 0;
                System.out.println("Y-Koordinate von Nodes-Objekt " + (i + 1) + " in " + filename + " fehlt.");
                programmBeenden();
            }

            //getting NAME
            //Fehlerbehandlung an dieser Stelle unnoetig, da Fehler bereits in der Methode nameList behandelt werden
            name = currentObject.getString("name");

            //getting KIND
            if (currentObject.has("kind")) {
                try {
                    kind = currentObject.getString("kind");
                    if (!kindList.contains(kind)) {
                        System.out.println("Kind " + kind + " von Nodes-Objekt " + (i + 1) + " in " + filename + " nicht bekannt.");
                        System.out.println("Valide Eingaben sind: " + kindList);
                        programmBeenden();
                    }
                } catch (JSONException e) {
                    kind = "";
                    System.out.println(e);
                    System.out.println("Kind von Nodes-Objekt " + (i + 1) + " in " + filename + " kann nicht ausgelesen werden.");
                    programmBeenden();
                }
            } else {
                kind = "";
                System.out.println("Kind von Nodes-Objekt " + (i + 1) + " in " + filename + " fehlt.");
                programmBeenden();
            }

            //getting TARGETTYPE
            if (currentObject.has("targettype")) {
                try {
                    targettype = currentObject.getString("targettype");
                    if (!targetTypeList.contains(targettype)) {
                        System.out.println("Targettype " + targettype + " von Nodes-Objekt " + (i + 1) + " in " + filename + " nicht bekannt.");
                        System.out.println("Valide Eingaben sind: " + targetTypeList);
                        programmBeenden();
                    }
                } catch (JSONException e) {
                    targettype = "";
                    System.out.println(e);
                    System.out.println("Targettype von Nodes-Objekt " + (i + 1) + " in " + filename + " kann nicht ausgelesen werden.");
                    programmBeenden();
                }
            } else {
                targettype = "noTargetType";
            }

            //getting TO
            if (currentObject.has("to")) {
                try {
                    JSONArray toArray = currentObject.getJSONArray("to");
                    if (toArray.length() <= 0 && !targettype.equals("ausflug")) {
                        System.out.println("To-Liste von Nodes-Objekt " + (i + 1) + " in " + filename + " darf nicht leer sein.");
                        programmBeenden();
                    }
                    if (toArray.length() > 0 && targettype.equals("ausflug")) {
                        System.out.println("To-Liste von Nodes-Objekt " + (i + 1) + " in " + filename + " muss leer sein.");
                        programmBeenden();
                    }
                    to = new String[toArray.length()];
                    for (int j = 0; j < toArray.length(); j++) {
                        if (nameList.contains(toArray.getString(j))) {
                            to[j] = toArray.getString(j);
                        } else {
                            System.out.println("Name " + toArray.getString(j) + " in To-Liste von Node-Objekt " + (i + 1) + " in " + filename + " nicht bekannt.");
                            System.out.println("Valide Eingaben sind: " + nameList);
                            programmBeenden();
                        }
                    }
                } catch (JSONException e) {
                    to = new String[0];
                    System.out.println(e);
                    System.out.println("To-Liste von Nodes-Objekt " + (i + 1) + " in " + filename + " kann nicht ausgelesen werden.");
                    programmBeenden();
                }
            } else {
                to = new String[0];
            }

            //getting CONFLICTS
            if (currentObject.has("conflicts")) {
                try {
                    JSONArray conflictsArray = currentObject.getJSONArray("conflicts");
                    conflict = new String[conflictsArray.length()];
                    for (int j = 0; j < conflictsArray.length(); j++) {
                        if (nameList.contains(conflictsArray.getString(j))) {
                            conflict[j] = conflictsArray.getString(j);
                        } else {
                            System.out.println("Name " + conflictsArray.getString(j) + " in Conflict-Liste von Node-Objekt " + (i + 1) + " in " + filename + " nicht bekannt.");
                            System.out.println("Valide Eingaben sind: " + nameList);
                            programmBeenden();
                        }
                    }
                } catch (JSONException e) {
                    conflict = new String[0];
                    System.out.println(e);
                    System.out.println("Conflict-Liste von Nodes-Objekt " + (i + 1) + " in " + filename + " kann nicht ausgelesen werden.");
                    programmBeenden();
                }
            } else {
                conflict = new String[0];
            }

            //getting WAITTIME
            if (currentObject.has("waittime")) {
                try {
                    //waittime wird als String angegeben, das ist allerdings für den einsatzzweck nicht sinnvoll
                    waittime = Integer.parseInt(currentObject.getString("waittime"));
                } catch (JSONException e) {
                    waittime = 0;
                    System.out.println(e);
                    System.out.println("Knoten-Waittime in " + filename + " kann nicht ausgelesen werden.");
                    programmBeenden();
                }
            } else {
                waittime = 0;
            }
            nodeList.add(new Knotenpunkt(x, y, name, kind, to, conflict, targettype, waittime));
        }
        return nodeList;
    }
}

*/