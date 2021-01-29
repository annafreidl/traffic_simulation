package planverkehr.graph;

import planverkehr.MCoordinate;
import planverkehr.transportation.EDirections;

import java.util.ArrayList;
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

//    public void removeKnotenpunkteByGroupID(String groupId) {
//        ArrayList<String> KnotenpunkteToRemove = new ArrayList<>();
//        System.out.println("removeKnotenpunkteByGroupID");
//        this.forEach((key, point) -> {
//            System.out.println("groupId  not found: " + groupId);
//           if(point.getGroupId().contains(groupId)) {
//               if(point.getGroupId().size() > 1){
//                   System.out.println("multiple groups: " + key);
//                   point.getGroupId().remove(groupId);
//               } else {
//                   KnotenpunkteToRemove.add(key);
//               }
//           }
//        });
//
//        for (String s : KnotenpunkteToRemove) {
//            System.out.println("I have removed: " + s);
//            this.remove(s);
//        }
//    }
}
