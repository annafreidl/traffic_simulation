package planverkehr.graph;

import planverkehr.Buildings;
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
    //todo: prüfen ob die nächsten beiden noch benötigt werden
    EDirections direction; //Himmelsrichtung im Feld in der  Knotenpunkt liegt
    Set possibleConnections; //EnumSet
    HashMap<String, MKnotenpunkt> connectedKnotenpunkte;
    ArrayList<MKnotenpunkt> connectedKnotenpunkteArray;
    ArrayList<String> groupIds; //Alle zu einem Gebäude zugehörigen Koordinaten haben dieselbe GroupId: feldID-buildingToBeBuiltID;
    EBuildType surfaceType;
    ESpecial targetType;
    final TreeSet<Integer> blockedForTickListLeft;
    final TreeSet<Integer> blockedForTickListRight;
    MHaltestelle haltestelle = null;
    boolean isBlocked = false;
    boolean isTempTarget = false;  //todo: prüfen ob überhaupt noch nötig


    public MKnotenpunkt(String nodeId, String groupId, MCoordinate visibleCoords, EBuildType type, String name, String feldId, EDirections direction, boolean isEdge) {
        this.knotenpunktId = nodeId;
        groupIds = new ArrayList<>();
        groupIds.add(groupId);

        blockId = 0;

        this.visibleCoordinate = visibleCoords;
        this.surfaceType = type;
        this.name = name;
        this.feldId = feldId;
        this.edge = isEdge;
        this.direction = direction;
        connectedKnotenpunkte = new HashMap<>();
        connectedKnotenpunkteArray = new ArrayList<>();
        //  groupedKnotenpunkte = new HashMap<>();
        possibleConnections = EnumSet.noneOf(EDirections.class);
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

    public boolean isEdge() {
        return edge;
    }

    public EDirections getDirection() {
        return direction;
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
            ", direction=" + direction +
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

    public void addEntryToBlockedForTickList(int tick, boolean isLeft) {
      if(isLeft) {
          blockedForTickListLeft.add(tick);
      } else{
      }  blockedForTickListRight.add(tick);
    }


    //todo: muss für Verlehrsmittel geschrieben werden
    public boolean isFreeFor(int timeBetretenUm, boolean isLeft) {
        TreeSet<Integer> relevantBlockedForTickList = isLeft ? blockedForTickListLeft : blockedForTickListRight;
        Integer lowerTime = relevantBlockedForTickList.higher(timeBetretenUm - 1);
        Integer upperTime = relevantBlockedForTickList.higher(timeBetretenUm + 1);

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

    public void removeAllBlockedForTicksSmallerThen(int tickNumber) {
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
