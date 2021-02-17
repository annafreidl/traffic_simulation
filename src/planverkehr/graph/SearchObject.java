package planverkehr.graph;

import planverkehr.EBuildType;

import java.util.ArrayList;
import java.util.Stack;

public class SearchObject {
    final ArrayList<MWegKnotenpunkt> arrayListZuBesuchenderWegpunkte;
    final Stack<MWegKnotenpunkt> arrayListBesuchterWegpunkte;
    MWegKnotenpunkt currentWayNode;
    final MTargetpointList wayPointList;
 //   final MTargetpointList waitList;


    public SearchObject(MKnotenpunkt start, MTargetpointList wayPointList, int tickNumber) {
        arrayListZuBesuchenderWegpunkte = new ArrayList<>();
        arrayListBesuchterWegpunkte = new Stack<>();
        currentWayNode = new MWegKnotenpunkt(tickNumber, start, start);
        this.wayPointList = wayPointList;
        arrayListZuBesuchenderWegpunkte.add(currentWayNode);
       // this.waitList = waitList;
    }


    public MWegKnotenpunkt getCurrentWayNode() {
        return currentWayNode;
    }

    public ArrayList<MKnotenpunkt> getWayPointList() {
        return wayPointList;
    }

    public ArrayList<MWegKnotenpunkt> getArrayListZuBesuchenderWegpunkte() {
        return arrayListZuBesuchenderWegpunkte;
    }

    public void setLastElementZuBesuchenderToCurrentNode(){
        setCurrentWayNode(arrayListZuBesuchenderWegpunkte.get(arrayListZuBesuchenderWegpunkte.size()-1));

    }

    public void removeFirstFromZuBesuchendeWegpunkte(){
        arrayListZuBesuchenderWegpunkte.remove(0);
    }

    public void setCurrentWayNode(MWegKnotenpunkt currentWayNode) {
        this.currentWayNode = currentWayNode;
    }

    public MWegKnotenpunkt getFirstElementZuBesuchenderWegpunkte(){
        return arrayListZuBesuchenderWegpunkte.get(0);
    }

    public MWegKnotenpunkt getFirstElementBesuchterWegpunkte(){
        return arrayListBesuchterWegpunkte.get(0);
    }

    public void addCurrentWaynodeToBesuchterWegpunkte(){
        arrayListBesuchterWegpunkte.add(currentWayNode);
    }

    public void clearBesuchteWegpunkte(){
        arrayListBesuchterWegpunkte.clear();
    }

    public EBuildType getCurrentNodeKind(){
        return currentWayNode.knotenpunkt.surfaceType;
    }

    public void clearZuBesuchendeWegpunkte() {
        arrayListZuBesuchenderWegpunkte.clear();
    }

    public int getCurrentNodeBetretenUm(){
        return currentWayNode.betretenUm;
    }

    public MKnotenpunkt getCurrentNodeKnotenpunkt(){
        return currentWayNode.knotenpunkt;
    }

    public void addWaynodeToBesuchteWegpunkte(MWegKnotenpunkt wp){
        arrayListBesuchterWegpunkte.add(wp);
    }

    public ArrayList<MKnotenpunkt> getNeighboursOfCurrentWaynode(){
        return currentWayNode.getNeighbours();
    }

    public boolean isZuBesuchendeWegeContaining(MWegKnotenpunkt s){
        return arrayListZuBesuchenderWegpunkte.contains(s);
    }

    public boolean isWayPointListContaining(MKnotenpunkt k){
        return wayPointList.contains(k);
    }

    public boolean isTempTarget(){
        return currentWayNode.knotenpunkt.isTempTarget;
    }

    public void addWaynodeToZuBesuchendeWegpunkte(MWegKnotenpunkt tempWegpunkt) {
        arrayListZuBesuchenderWegpunkte.add(tempWegpunkt);
    }

    public MKnotenpunkt getVorgaenger(){
        return currentWayNode.getVorgaenger();
    }

//    public int getWaitTime(){
//        return currentWayNode.knotenpunkt.waitTime;
//    }

    public MWegKnotenpunkt getWegKnotenpunktAtIndex(int i){
        return arrayListBesuchterWegpunkte.get(i);
    }
}

