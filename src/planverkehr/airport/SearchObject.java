package planverkehr.airport;

import java.util.ArrayList;
import java.util.Stack;

/*
public class SearchObject {
    final ArrayList<WegKnotenpunkt> arrayListZuBesuchenderWegpunkte;
    final Stack<WegKnotenpunkt> arrayListBesuchterWegpunkte;
    WegKnotenpunkt currentWayNode;
    final TargetpointList wayPointList;
    final TargetpointList waitList;


    public SearchObject(Knotenpunkt start, TargetpointList wayPointList, int ticknumber, TargetpointList waitList) {
        arrayListZuBesuchenderWegpunkte = new ArrayList<>();
        arrayListBesuchterWegpunkte = new Stack<>();
        currentWayNode = new WegKnotenpunkt(ticknumber, start, start);
        this.wayPointList = wayPointList;
        arrayListZuBesuchenderWegpunkte.add(currentWayNode);
        this.waitList = waitList;
    }


    public WegKnotenpunkt getCurrentWayNode() {
        return currentWayNode;
    }

    public ArrayList<Knotenpunkt> getWayPointList() {
        return wayPointList;
    }

    public ArrayList<WegKnotenpunkt> getArrayListZuBesuchenderWegpunkte() {
        return arrayListZuBesuchenderWegpunkte;
    }

    public void setLastElementZuBesuchenderToCurrentNode(){
        setCurrentWayNode(arrayListZuBesuchenderWegpunkte.get(arrayListZuBesuchenderWegpunkte.size()-1));

    }

    public void removeFirstFromZuBesuchendeWegpunkte(){
        arrayListZuBesuchenderWegpunkte.remove(0);
    }

    public void setCurrentWayNode(WegKnotenpunkt currentWayNode) {
        this.currentWayNode = currentWayNode;
    }

    public WegKnotenpunkt getFirstElementZuBesuchenderWegpunkte(){
        return arrayListZuBesuchenderWegpunkte.get(0);
    }

    public WegKnotenpunkt getFirstElementBesuchterWegpunkte(){
        return arrayListBesuchterWegpunkte.get(0);
    }

    public void addCurrentWaynodeToBesuchterWegpunkte(){
        arrayListBesuchterWegpunkte.add(currentWayNode);
    }

    public void clearBesuchteWegpunkte(){
        arrayListBesuchterWegpunkte.clear();
    }

    public String getCurrentNodeKind(){
        return currentWayNode.knotenpunkt.kind;
    }

    public void clearZuBesuchendeWegpunkte() {
        arrayListZuBesuchenderWegpunkte.clear();
    }

    public int getCurrentNodeBetretenUm(){
        return currentWayNode.betretenUm;
    }

    public Knotenpunkt getCurrentNodeKnotenpunkt(){
        return currentWayNode.knotenpunkt;
    }

    public void addWaynodeToBesuchteWegpunkte(WegKnotenpunkt wp){
        arrayListBesuchterWegpunkte.add(wp);
    }

    public ArrayList<Knotenpunkt> getNeighboursOfCurrentWaynode(){
        return currentWayNode.getNeighbours();
    }

    public boolean isZuBesuchendeWegeContaining(WegKnotenpunkt s){
        return arrayListZuBesuchenderWegpunkte.contains(s);
    }

    public boolean isWayPointListContaining(Knotenpunkt k){
        return wayPointList.contains(k);
    }

    public boolean isTempTarget(){
        return currentWayNode.knotenpunkt.isTempTarget;
    }

    public void addWaynodeToZuBesuchendeWegpunkte(WegKnotenpunkt tempWegpunkt) {
        arrayListZuBesuchenderWegpunkte.add(tempWegpunkt);
    }

    public Knotenpunkt getVorgaenger(){
        return currentWayNode.vorgaenger;
    }

    public int getWaitTime(){
        return currentWayNode.knotenpunkt.waitTime;
    }

    public WegKnotenpunkt getWegKnotenpunktAtIndex(int i){
        return arrayListBesuchterWegpunkte.get(i);
    }
}

*/
