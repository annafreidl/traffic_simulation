package planverkehr.transportation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import planverkehr.Buildings;
import planverkehr.MCoordinate;

public class VRoad extends VTransportation{
    public VRoad(Buildings road, GraphicsContext gc, MCoordinate west) {
        super(road, gc, west, Color.DIMGRAY);
        drawRoadOrRail();
    }
}
