package planverkehr.graph;

import planverkehr.EBuildType;
import planverkehr.MCoordinate;
import planverkehr.MHaltestelle;
import planverkehr.transportation.EDirections;
import planverkehr.transportation.ESpecial;

import java.util.*;

public class MKnotenpunkt {
    MCoordinate visibleCoordinate;
    String feldId,
        name; //nw, ns, p0 usw.
    int blockId;
    String knotenpunktId; //type(bspw road)+ zahl
    boolean edge; //Wenn einer der x oder y-Werte eine ganze Zahl ist
    HashMap<String, MKnotenpunkt> connectedKnotenpunkte;
    ArrayList<MKnotenpunkt> connectedKnotenpunkteArray;
    ArrayList<String> groupIds; //Alle zu einem Gebäude zugehörigen Koordinaten haben dieselbe GroupId: feldID-buildingToBeBuiltID;
    EBuildType surfaceType;
    ESpecial targetType;
    final TreeSet<Double> blockedForTickListLeft;
    final TreeSet<Double> blockedForTickListRight;
    MHaltestelle haltestelle = null;
    boolean isBlocked = false;


    public MKnotenpunkt(String nodeId, String groupId, MCoordinate visibleCoords, EBuildType type, String name, String feldId, boolean isEdge) {
        this.knotenpunktId = nodeId;
        groupIds = new ArrayList<>();
        groupIds.add(groupId);

        blockId = 0;

        this.visibleCoordinate = visibleCoords;
        this.surfaceType = type;
        this.name = name;
        this.feldId = feldId;
        this.edge = isEdge;

        connectedKnotenpunkte = new HashMap<>();
        connectedKnotenpunkteArray = new ArrayList<>();
        targetType = ESpecial.NOTHING;
        blockedForTickListLeft = new TreeSet<>();
        blockedForTickListRight = new TreeSet<>();
    }

    public String getFeldId() {
        return feldId;
    }

    public void addGroupId(String id) {
        groupIds.add(id);
    }


    public void addConnectedNode(MKnotenpunkt node) {
        if (!connectedKnotenpunkte.containsKey(node.getTileId())) {
            connectedKnotenpunkteArray.add(node);
            connectedKnotenpunkte.put(node.getTileId(), node);
        }

    }

    public String getName() {
        return name;
    }



    public String getTileId() {
        return knotenpunktId;
    }

    @Override
    public String toString() {
        return "MKnotenpunkt{" +
            "gridCoordinate=" + visibleCoordinate +
            ", feldId='" + feldId + '\'' +
            ", name='" + name + '\'' +
            ", targetType: " + targetType + '\'' +
            ", connectedKnotenpunkteArray=" + connectedKnotenpunkteArray.size() +
            '}';
    }

    public MCoordinate getVisibleCoordinate() {
        return visibleCoordinate;
    }

    public ArrayList<MKnotenpunkt> getConnectedKnotenpunkteArray() {
        return connectedKnotenpunkteArray;
    }

    public void setTargetType(ESpecial targetType) {
        this.targetType = targetType;
    }

    public ArrayList<String> getListOfGroupId() {
        return groupIds;
    }

    public String getKnotenpunktId() {
        return knotenpunktId;
    }

    public ESpecial getTargetType() {
        return targetType;
    }

    public void addEntryToBlockedForTickList(double tick, boolean isLeft) {
      if(isLeft) {
          blockedForTickListLeft.add(tick);
      } else {
          blockedForTickListRight.add(tick);
      }
    }

    public boolean isFreeFor(double timeBetretenUm, boolean isLeft) {
        TreeSet<Double> relevantBlockedForTickList = isLeft ? blockedForTickListLeft : blockedForTickListRight;
        Double lowerTime = relevantBlockedForTickList.higher(timeBetretenUm - 1);
        Double upperTime = relevantBlockedForTickList.higher(timeBetretenUm + 1);

        if (isBlocked) {
            return false;
        } else if ((relevantBlockedForTickList.isEmpty() || (lowerTime == null))) {
            return true;
        } else if ((upperTime == null)) {
            return false;
        } else if (relevantBlockedForTickList.subSet(lowerTime, upperTime).isEmpty()) {
            return true;
        }
        return true;
    }

    public EBuildType getSurfaceType() {
        return surfaceType;
    }

    public void removeAllBlockedForTicksSmallerThen(double tickNumber) {
        blockedForTickListLeft.removeIf(integer -> integer < tickNumber);
        blockedForTickListRight.removeIf(integer -> integer < tickNumber);
    }

    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public void setHaltestelle(MHaltestelle h) {
        this.haltestelle = h;
    }

    public MHaltestelle getHaltestelle() {
        return haltestelle;
    }
}
