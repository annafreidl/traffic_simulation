package planverkehr.airport;


import planverkehr.*;
import planverkehr.graph.MTargetpointList;
import planverkehr.transportation.MTransportConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;



//Model des Flughafens
public class MAirport {
    MGame gameModel;
    VGame gameView;
    Buildings newBuilding;
    TargetpointList waypointList;
    TargetpointList einflugList;
    TargetpointList gateWayList;
    TargetpointList ausflugList;
    TargetpointList waitList;
    TargetpointList otherTargetTypeList;

    Buildings tower, bigTower, runway, terminal, taxiway;
    boolean fullyBuilt;

    //TODO: wir brauchen Listen der Gebäude, da sich der Airport aus diesen zusammensetzt und wir alle Gebäude vom Typ Airport mergen müssen
    //RN THE GOAL IS: wenn wir ein Gebäude setzen vom Typ airport, sollen die tiles drumherum automatisch geprüpft werden, und wenn
    //sich dort ein Gebäude vom Airport befindet, mit dem es sich verknüpfen kann, soll dies zusammen später als ein Airport abgespeichert werden

    //TODO: diese Airports werden dann im AirportManager gespeichert und dort verwaltet

    /** Diese Klasse erstellt vollständige/funktionale Airports aus den einzelenen Gebäuden des Airports */

    public MAirport(MGame gameModel) {

        ArrayList nodesList = new ArrayList<Knotenpunkt>();
        createSpecialTargetTypeLists();
        createWaypointList();

        tower = null;
        bigTower = null;
        terminal = null;
        runway = null;
        taxiway = null;
        fullyBuilt = false;
    }

    private void createSpecialTargetTypeLists() {
        TargetpointList einflugList = new TargetpointList();
        TargetpointList ausflugList = new TargetpointList();
        TargetpointList gateWayList = new TargetpointList();
        TargetpointList waitList = new TargetpointList();
        TargetpointList otherTargetTypeList = new TargetpointList();

        for (Knotenpunkt knotenpunkt : waypointList) {
            switch (knotenpunkt.targetType) {
                case "einflug" -> einflugList.add(knotenpunkt);
                case "gateway" -> gateWayList.add(knotenpunkt);
                case "ausflug" -> ausflugList.add(knotenpunkt);
                case "wait" -> waitList.add(knotenpunkt);
                default -> otherTargetTypeList.add(knotenpunkt);
            }
        }
    }

    private void createWaypointList() {
        TargetpointList waypointList = new TargetpointList();
       /* nodeList.forEach(n -> {
            if (!n.targetType.equals("noTargetType")) {
                waypointList.add(n);
            }
        }); */
    }

    //jedes Mal wenn wir Gebäude adden, checken wir ob Airport functional ist (bzw. voll ist)
    private void isFunctional(){
        fullyBuilt = (tower != null || bigTower != null) && terminal != null && runway != null && taxiway != null; //is true wenn all das zutrifft
    }

    public TargetpointList getWaypointList() {
        return waypointList;
    }

    public Buildings getTower() {
        return tower;
    }

    public Buildings getBigTower() {
        return bigTower;
    }

    public Buildings getTerminal() {
        return terminal;
    }

    public Buildings getRunway() {
        return runway;
    }

    public Buildings getTaxiway() {
        return taxiway;
    }

    public void setTower(Buildings tower) {
        this.tower = tower;
        //jedes Mal wenn wir Gebäude adden, checken wir ob Airport functional ist bzw. voll ist
        isFunctional();
    }

    public void setBigTower(Buildings bigTower) {
        this.bigTower = bigTower;
        isFunctional();
    }

    public void setTerminal(Buildings terminal) {
        this.terminal = terminal;
        isFunctional();
    }

    public void setRunway(Buildings runway) {
        this.runway = runway;
        isFunctional();
    }

    public void setTaxiway(Buildings taxiway) {
        this.taxiway = taxiway;
        isFunctional();
    }

    public void removeTower(){
        tower = null;
        isFunctional();
    }

    public void removeBigTower(){
        bigTower = null;
        isFunctional();
    }

    public void removeTerminal(){
        terminal = null;
        fullyBuilt = false;
    }

    public void removeRunway(){
        runway = null;
        fullyBuilt = false;
    }

    public void removeTaxiway(){
        taxiway = null;
        fullyBuilt = false;
    }
}


// AB HIER IST ALT ---------------------------------------------------
/*
    public MAirport(AirportConfig config) {
        this.config = config;
        visibleAirplaneList = new ArrayList<>();
        nodeList = config.getNodeList();
        linkNodeList();
        createWaypointList();
        createSpecialTargetTypeLists();
        availableAirplaneQueue = config.getDefaultPlanesQueue();
    }

    //erstellt Listen für die jeweiligen Zieltypen
    private void createSpecialTargetTypeLists() {
        einflugList = new TargetpointList();
        ausflugList = new TargetpointList();
        gateWayList = new TargetpointList();
        waitList = new TargetpointList();
        otherTargetTypeList = new TargetpointList();

        for (Knotenpunkt knotenpunkt : waypointList) {
            switch (knotenpunkt.targetType) {
                case "einflug" -> einflugList.add(knotenpunkt);
                case "gateway" -> gateWayList.add(knotenpunkt);
                case "ausflug" -> ausflugList.add(knotenpunkt);
                case "wait" -> waitList.add(knotenpunkt);
                default -> otherTargetTypeList.add(knotenpunkt);
            }
        }
    }

    //erstellt eine Liste mit allen Knotenpunkten, die einen Targettype haben
    private void createWaypointList() {
        waypointList = new TargetpointList();
        nodeList.forEach(n -> {
            if (!n.targetType.equals("noTargetType")) {
                waypointList.add(n);
            }
        });
    }

    private void linkNodeList() {
        for (Knotenpunkt p : nodeList
        ) {

            String[] toAsStringArray = p.getTo();
            for (String s : toAsStringArray
            ) {
                Knotenpunkt toNode = searchForNode(s);
                p.addToKnotenpunkt(toNode);
            }

            if (p.kind.equals("concrete")) {
                p.addToKnotenpunkt(p);
            }

            if (p.hasConflict) {
                String[] conflictAsStringArray = p.getConflict();
                for (String s : conflictAsStringArray
                ) {
                    Knotenpunkt conflictNode = searchForNode(s);
                    p.addConflictNode(conflictNode);
                }
            }

        }
    }

    private Knotenpunkt searchForNode(String s) {

        for (Knotenpunkt p : nodeList
        ) {
            if (p.getName().equals(s)) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Knotenpunkt> getNodeList() {
        return nodeList;
    }

    public ArrayList<Integer> getNachbarpunkte(Knotenpunkt k1) {
        ArrayList<Integer> indexlistnachbarn = new ArrayList<>();
        for (Knotenpunkt k2 : getNodeList()) {
            for (String s1 : k1.getTo()) {
                if (s1.equals(k2.getName())) {
                    indexlistnachbarn.add(nodeList.indexOf(k2));
                }
            }
        }
        return indexlistnachbarn;
    }

    public ArrayList<MAirplane> getVisibleAirplaneList() {
        return visibleAirplaneList;
    }

    public void addAirplaneToVisiblePlanesList(MAirplane plane) {
        numOfPlanesOnAirport++;
        visibleAirplaneList.add(plane);
    }

    public Queue<MAirplane> getAvailableAirplaneQueue() {
        return availableAirplaneQueue;
    }

    public void addAirplaneToAvailableAirplaneQueue(MAirplane plane) {
        availableAirplaneQueue.add(plane);
    }

    public TargetpointList getEinflugList() {
        return einflugList;
    }

    public TargetpointList getWaypointList() {
        return waypointList;
    }



    public boolean hasSpaceForNewPlane() {
        return numOfPlanesOnAirport < config.maxPlanes;
    }



    public TargetpointList getWaitList() {
        return waitList;
    }
}

*/

/** Bei hasSpaceForNewPlane statt config.MaxPlanes > building.MaxPlanes **/