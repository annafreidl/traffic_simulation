package planverkehr.transportation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import planverkehr.Buildings;
import planverkehr.MCoordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class VTransportConnection {
    Color color;
    GraphicsContext gc;
    double westAbsolutX, westAbsolutY, westAbsolutZ;
    Buildings road_rail;
    ArrayList<MCoordinate> eckpunkteTile;
    MCoordinate westAbsCoord;
    boolean isSchraeg;
    boolean gehtHoch;
    boolean isHoch;

    public VTransportConnection(Buildings roadOrRail, GraphicsContext gc, MCoordinate westVisible, Color color, ArrayList<MCoordinate> punkteNeu, boolean isSchraeg, int level, boolean isHoch) {
        MCoordinate westRelativ = punkteNeu.get(3); //0:0:Z
        //if (isHoch) {
        gehtHoch = westRelativ.getZ() != level;
        //}
//        else {
//            gehtHoch = westRelativ.getZ() == level;
//        }
//        this.isHoch = isHoch;

        MCoordinate westAbsolut = new MCoordinate(westVisible.getX() + westRelativ.getX(), westVisible.getY() + westRelativ.getY(), westRelativ.getZ());
        this.westAbsCoord = westAbsolut;

        this.westAbsolutX = westAbsolut.getX();
        this.westAbsolutY = westAbsolut.getY();
        this.westAbsolutZ = westAbsolut.getZ();
        this.color = color;
        this.gc = gc;
        this.road_rail = roadOrRail;
        this.isSchraeg = isSchraeg;
        this.eckpunkteTile = punkteNeu;
    }

    public void drawRoadOrRail() {

        HashMap<String, MCoordinate> absCoordMap = new HashMap<>();
        road_rail.getPoints().forEach((key, coord) -> {
            MCoordinate temp;
            if (!isSchraeg) {
                temp = new MCoordinate(coord.getX() + westAbsolutX, westAbsolutY - coord.getY(), westAbsolutZ).toCanvasCoordWithoutOffset();
            } else {
                double schraege;
                MCoordinate firstCoord = null;
                MCoordinate secondCoord = null;
                if (coord.getX() % 1 == 0) {

                    for (MCoordinate c : eckpunkteTile
                    ) {
                        if (c.getX() == coord.getX() && c.getY() == (coord.getY() - 0.5)) {
                            firstCoord = c;
                        } else if (c.getX() == coord.getX() && c.getY() == (coord.getY() + 0.5)) {
                            secondCoord = c;
                        }
                    }
                } else if (coord.getY() % 1 == 0) {
                    for (MCoordinate c : eckpunkteTile
                    ) {
                        if (c.getX() == (coord.getX() - 0.5) && c.getY() == (coord.getY())) {
                            firstCoord = c;
                        } else if (c.getX() == coord.getX() + 0.5 && c.getY() == coord.getY()) {
                            secondCoord = c;
                        }
                    }
                }
                if (firstCoord != null && secondCoord != null && firstCoord.getZ() == secondCoord.getZ()) {
                    schraege = firstCoord.getZ();
                } else {
                    schraege = gehtHoch ? westAbsolutZ - 0.5 : westAbsolutZ + 0.5;
                }
                temp = new MCoordinate(coord.getX() + westAbsolutX, westAbsolutY - coord.getY(), schraege).toCanvasCoordWithoutOffset();
            }
            absCoordMap.put(key, temp);
        });
        gc.setFill(color);
        gc.setStroke(color);
        gc.setLineWidth(3);
        List<Pair<String, String>> connectionsList = road_rail.getRoads().isEmpty() ? road_rail.getRails() : road_rail.getRoads();
        boolean isRoad = !road_rail.getRoads().isEmpty();
        if (isRoad) {
            gc.setLineDashes(7.5);
        }
        connectionsList.forEach((pair) -> {

            gc.strokeLine((absCoordMap.get(pair.getKey()).getX()), (absCoordMap.get(pair.getKey()).getY()), (absCoordMap.get(pair.getValue()).getX()), (absCoordMap.get(pair.getValue()).getY()));

        });
        gc.setLineDashes(0);


    }
}
