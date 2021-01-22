package planverkehr.transportation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import planverkehr.Buildings;
import planverkehr.MCoordinate;

public class VRail extends VTransportation {
    public VRail(Buildings road, GraphicsContext gc, MCoordinate center) {
        super(road, gc, center, Color.FIREBRICK);
        drawRoadOrRail();
    }
}
