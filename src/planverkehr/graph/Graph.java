package planverkehr.graph;

import planverkehr.MCoordinate;
import planverkehr.transportation.EDirections;

import java.util.HashMap;


public class Graph extends HashMap<String, MKnotenpunkt> {
    int id = 0;
    public boolean canConnectToNeighbour(MKnotenpunkt neighbour, EDirections roadDirection) {
       return neighbour.possibleConnections.contains(roadDirection);
    }

    public void print() {
        this.forEach((key, point) -> {
           System.out.println("id: " + point.getTileId() + " possible: " + point.possibleConnections.toString() + " connected: " + point.connectedKnotenpunkte.toString());
        });
    }

    public int getIncreasedId(){
       return id++;
    }
}
