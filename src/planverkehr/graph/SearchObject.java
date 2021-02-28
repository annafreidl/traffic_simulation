package planverkehr.graph;


import java.util.ArrayList;
import java.util.Stack;

public class SearchObject {
    final ArrayList<MWegKnotenpunkt> arrayListZuBesuchenderWegpunkte;
    final Stack<MWegKnotenpunkt> arrayListBesuchterWegpunkte;
    MWegKnotenpunkt currentWayNode;
    final MTargetpointList wayPointList;


    public SearchObject(MKnotenpunkt start, MTargetpointList wayPointList, int tickNumber) {
        arrayListZuBesuchenderWegpunkte = new ArrayList<>();
        arrayListBesuchterWegpunkte = new Stack<>();
        currentWayNode = new MWegKnotenpunkt(tickNumber, start, start);
        this.wayPointList = wayPointList;
        arrayListZuBesuchenderWegpunkte.add(currentWayNode);
    }


    public MWegKnotenpunkt getCurrentWayNode() {
        return currentWayNode;
    }


    public ArrayList<MWegKnotenpunkt> getArrayListZuBesuchenderWegpunkte() {
        return arrayListZuBesuchenderWegpunkte;
    }

    public void setLastElementZuBesuchenderToCurrentNode() {
        setCurrentWayNode(arrayListZuBesuchenderWegpunkte.get(arrayListZuBesuchenderWegpunkte.size() - 1));

    }

    public void removeFirstFromZuBesuchendeWegpunkte() {
        arrayListZuBesuchenderWegpunkte.remove(0);
    }

    public void setCurrentWayNode(MWegKnotenpunkt currentWayNode) {
        this.currentWayNode = currentWayNode;
    }

    public MWegKnotenpunkt getFirstElementZuBesuchenderWegpunkte() {
        return arrayListZuBesuchenderWegpunkte.get(0);
    }

    public MWegKnotenpunkt getFirstElementBesuchterWegpunkte() {
        return arrayListBesuchterWegpunkte.get(0);
    }

    public void addCurrentWaynodeToBesuchterWegpunkte() {
        arrayListBesuchterWegpunkte.add(currentWayNode);
    }

    public void clearBesuchteWegpunkte() {
        arrayListBesuchterWegpunkte.clear();
    }

    public void clearZuBesuchendeWegpunkte() {
        arrayListZuBesuchenderWegpunkte.clear();
    }

    public int getCurrentNodeBetretenUm() {
        return currentWayNode.betretenUm;
    }

    public MKnotenpunkt getCurrentNodeKnotenpunkt() {
        return currentWayNode.knotenpunkt;
    }

    public void addWaynodeToBesuchteWegpunkte(MWegKnotenpunkt wp) {
        arrayListBesuchterWegpunkte.add(wp);
    }

    public ArrayList<MKnotenpunkt> getNeighboursOfCurrentWaynode() {
        return currentWayNode.getNeighbours();
    }

    public boolean isZuBesuchendeWegeContaining(MWegKnotenpunkt s) {
        return arrayListZuBesuchenderWegpunkte.contains(s);
    }

    public boolean isWayPointListContaining(MKnotenpunkt k) {
        return wayPointList.contains(k);
    }


    public void addWaynodeToZuBesuchendeWegpunkte(MWegKnotenpunkt tempWegpunkt) {
        arrayListZuBesuchenderWegpunkte.add(tempWegpunkt);
    }

    public MKnotenpunkt getVorgaenger() {
        return currentWayNode.getVorgaenger();
    }


    public MWegKnotenpunkt getWegKnotenpunktAtIndex(int i) {
        return arrayListBesuchterWegpunkte.get(i);
    }

    public ArrayList<MKnotenpunkt> getHaltestellenNodes() {
        ArrayList<MKnotenpunkt> haltstellenNodes = new ArrayList<>();
        if (currentWayNode.getKnotenpunkt().getHaltestelle() != null) {
            currentWayNode.getKnotenpunkt().getHaltestelle().getKnotenpunkteList().forEach((key, knotenpunkt) -> {
                if (currentWayNode.getKnotenpunkt() != knotenpunkt) {
                    haltstellenNodes.add(knotenpunkt);
                }
            });
        }
        return haltstellenNodes;
    }

    public boolean containsKontenpunkt(MKnotenpunkt knotenpunkt) {
        for (MWegKnotenpunkt mWegKnotenpunkt : arrayListBesuchterWegpunkte) {
            if(mWegKnotenpunkt.getKnotenpunkt().getKnotenpunktId().equals(knotenpunkt.getKnotenpunktId())){
                return true;
            }
        }
        for (MWegKnotenpunkt mWegKnotenpunkt : arrayListZuBesuchenderWegpunkte) {
            if(mWegKnotenpunkt.getKnotenpunkt().getKnotenpunktId().equals(knotenpunkt.getKnotenpunktId())){
                return true;
            }
        }

        return false;
    }
}

