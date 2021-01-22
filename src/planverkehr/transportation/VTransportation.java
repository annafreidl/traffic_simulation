package planverkehr.transportation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import planverkehr.Buildings;
import planverkehr.MCoordinate;

import java.util.HashMap;
import java.util.List;


public class VTransportation {
    Color color;
    GraphicsContext gc;
    double westX, westY;
    Buildings road_rail;

    public VTransportation(Buildings roadOrRail, GraphicsContext gc, MCoordinate west, Color color) {
        this.westX = west.getX();
        this.westY = west.getY();
        this.color = color;
        this.gc = gc;
        this.road_rail = roadOrRail;
    }

    public void drawRoadOrRail() {

        HashMap<String, MCoordinate> absCoordMap = new HashMap<>();
        road_rail.getPoints().forEach((key, coord) -> {
            MCoordinate temp = new MCoordinate((coord.getX()), (coord.getY()));
            temp = temp.toIsoWithoutOffset();
            absCoordMap.put(key, temp);
        });
        gc.setFill(color);
        gc.setStroke(color);
        gc.setLineWidth(3);
        List<Pair<String, String>> connectionsList = road_rail.getRoads().isEmpty() ? road_rail.getRails() : road_rail.getRoads();

        connectionsList.forEach((pair) -> gc.strokeLine((westX + absCoordMap.get(pair.getKey()).getX()), (westY + absCoordMap.get(pair.getKey()).getY()), (westX + absCoordMap.get(pair.getValue()).getX()), (westY + absCoordMap.get(pair.getValue()).getY())));

    }
}
