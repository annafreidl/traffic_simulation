package planverkehr;

import planverkehr.transportation.EDirections;

import java.util.EnumSet;

public class RoadKnotenpunkt extends MKnotenpunkt {


    public RoadKnotenpunkt(String tileId) {
        super(tileId);
        this.possibleConnections = EnumSet.noneOf(EDirections.class);

    }

    public void addPossibleConnection(EDirections connection) {
       possibleConnections.add(connection);
    }
}
