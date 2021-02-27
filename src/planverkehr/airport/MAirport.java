package planverkehr.airport;

import planverkehr.*;
import java.util.ArrayList;

//Model des Flughafens
public class MAirport {

    TargetpointList waypointList;
    TargetpointList einflugList;
    TargetpointList gateWayList;
    TargetpointList ausflugList;
    TargetpointList waitList;
    TargetpointList otherTargetTypeList;

    Buildings tower, bigTower, runway, terminal, taxiway;
    int maxPlanes; //wieviele kann der Flughafen spawnen
    boolean fullyBuilt;
    boolean noBuildingsSet;

    /** Diese Klasse enthält/verwaltet die einzelenen Gebäuden des Airports und prüft, ob sich diese zu einem funktionalen Airport zusammensetzen. */

    public MAirport() {

        ArrayList nodesList = new ArrayList<Knotenpunkt>();
        //createSpecialTargetTypeLists();
        createWaypointList();

        tower = null;
        bigTower = null;
        terminal = null;
        runway = null;
        taxiway = null;
        maxPlanes = 0;
        fullyBuilt = false;
        noBuildingsSet = false;
    }

/*    private void createSpecialTargetTypeLists() {
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
    }*/

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

    private void airportEmpty(){
        noBuildingsSet = tower == null && bigTower == null && terminal == null && runway == null && taxiway == null; //is true wenn all das zutrifft
    }

    public void updateMaxPlanes(){
        if(fullyBuilt) {
            if (bigTower == null) maxPlanes = tower.getMaxPlanes();
            else maxPlanes = bigTower.getMaxPlanes();
        }
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

    public int getMaxPlanes() {
        return maxPlanes;
    }

    public boolean isFullyBuilt() {
        return fullyBuilt;
    }

    public boolean isNoBuildingsSet() {
        return noBuildingsSet;
    }

    public void setTower(Buildings tower) {
        this.tower = tower;
        //jedes Mal wenn wir Gebäude adden, checken wir ob Airport functional ist bzw. voll ist
        isFunctional();
        updateMaxPlanes();
    }

    public void setBigTower(Buildings bigTower) {
        this.bigTower = bigTower;
        isFunctional();
        updateMaxPlanes();
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
        updateMaxPlanes();
        airportEmpty();
    }

    public void removeBigTower(){
        bigTower = null;
        isFunctional();
        updateMaxPlanes();
        airportEmpty();
    }

    public void removeTerminal(){
        terminal = null;
        fullyBuilt = false;
        airportEmpty();
    }

    public void removeRunway(){
        runway = null;
        fullyBuilt = false;
        airportEmpty();
    }

    public void removeTaxiway(){
        taxiway = null;
        fullyBuilt = false;
        airportEmpty();
    }
}
