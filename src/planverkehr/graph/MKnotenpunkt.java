package planverkehr.graph;

import planverkehr.EBuildType;
import planverkehr.MCoordinate;
import planverkehr.transportation.EDirections;

import java.util.*;

public class MKnotenpunkt {
    MCoordinate gridCoordinate;
    MCoordinate isoCoordinate;
    String feldId,
        name; //nw, ns, p0 usw.

    String knotenpunktId;
    boolean edge;
    EDirections direction; //Himmelsrichtung im Feld in der  Knotenpunkt liegt
    Set possibleConnections; //EnumSet
    HashMap<String, MKnotenpunkt> connectedKnotenpunkte;
    ArrayList<MKnotenpunkt> connectedKnotenpunkteArray;
    ArrayList<String> groupIds; //Alle zu einem Gebäude zugehörigen Koordinaten
  //  HashMap<String, MKnotenpunkt> groupedKnotenpunkte;
    EBuildType type;

    public MKnotenpunkt(String nodeId, String groupId, MCoordinate coords, EBuildType type, String name, String feldId, EDirections direction, boolean isEdge){
        this.knotenpunktId = nodeId;
        groupIds = new ArrayList<>();
        groupIds.add(groupId);

        this.gridCoordinate = coords;
        this.type = type;
        this.name = name;
        this.feldId = feldId;
        this.edge = isEdge;
        this.direction = direction;
        connectedKnotenpunkte = new HashMap<>();
        connectedKnotenpunkteArray = new ArrayList<>();
      //  groupedKnotenpunkte = new HashMap<>();
        possibleConnections = EnumSet.noneOf(EDirections.class);

    }

public void addGroupId(String id){
        groupIds.add(id);
}

    public void setGridCoordinate(MCoordinate gridCoordinate) {
        this.gridCoordinate = gridCoordinate;
    }

    public void setIsoCoordinate(MCoordinate isoCoordinate) {
        this.isoCoordinate = isoCoordinate;
    }

    public void setType(EBuildType type) {
        this.type = type;
    }


    public void addConnectedNode (MKnotenpunkt node) {
        if (!connectedKnotenpunkte.containsKey(node.getTileId())) {
            connectedKnotenpunkteArray.add(node);
            connectedKnotenpunkte.put(node.getTileId(), node);
        }

    }


    public HashMap<String, MKnotenpunkt> getConnectedKnotenpunkte() {
        return connectedKnotenpunkte;
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
            "gridCoordinate=" + gridCoordinate +
            ", feldId='" + feldId + '\'' +
            ", name='" + name + '\'' +
            ", direction=" + direction +
            ", connectedKnotenpunkteArray=" + connectedKnotenpunkteArray.size() +
            '}';
    }

    public MCoordinate getGridCoordinate() {
        return gridCoordinate;
    }

    public ArrayList<MKnotenpunkt> getConnectedKnotenpunkteArray() {
        return connectedKnotenpunkteArray;
    }
}
