package planverkehr;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MKnotenpunkt {
    MCoordinate gridCoordinate;
    MCoordinate isoCoordinate;
    String tileId;
    Set possibleConnections; //EnumSet
    HashMap<String, MKnotenpunkt> connectedKnotenpunkte; //HashMap
    EBuildType type;

    public MKnotenpunkt(String tileId){
        this.tileId = tileId;
        this.connectedKnotenpunkte = new HashMap<>();
       // this.possibleConnections = new EnumSet<>();
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

    public void setTileId(String tileId) {
        this.tileId = tileId;
    }

    public void addConnectedNode (MKnotenpunkt node) {
        connectedKnotenpunkte.put(node.getTileId(), node);
    }

    public String getTileId() {
        return tileId;
    }

    public void addConnections(String neighbourId) {

    }
}
