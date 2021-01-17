package planverkehr;

import java.util.EnumSet;

public class RoadKnotenpunkt extends MKnotenpunkt {
  // Set<EDircetion> possibleConnections; //EnumSet
//    Set<RoadKnotenpunkt> //HashSet

    public RoadKnotenpunkt(String tileId) {
        super(tileId);
        this.possibleConnections = EnumSet.noneOf(ERoadDircetion.class);

    }

    public void addPossibleConnection(ERoadDircetion connection) {
       possibleConnections.add(connection);
    }
}
