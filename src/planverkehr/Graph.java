package planverkehr;

import java.util.HashMap;

//todo: muss auf MKnotenpunkt ausgelegt werden
public class Graph extends HashMap<String, RoadKnotenpunkt> {
    public boolean canConnectToNeighbour(RoadKnotenpunkt neighbour, ERoadDircetion roadDirection) {
       return neighbour.possibleConnections.contains(roadDirection);
    }

    public void print() {
        this.forEach((key, point) -> {
           System.out.println("id: " + point.getTileId() + "possible: " + point.possibleConnections.toString() + "connected: " + point.connectedKnotenpunkte.toString());
        });
    }
}
