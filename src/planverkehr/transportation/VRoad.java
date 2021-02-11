package planverkehr.transportation;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import planverkehr.Buildings;
import planverkehr.MCoordinate;

import java.util.ArrayList;

public class VRoad extends VTransportation{
    public VRoad(Buildings road, GraphicsContext gc, ArrayList<MCoordinate> punkteNeu, MCoordinate westVisibleCoord, boolean isSchraeg, int level, boolean hoch) {
        super(road, gc, westVisibleCoord, Color.WHITE, punkteNeu, isSchraeg, level, hoch);
        drawRoadOrRail();

       if(road.getSpecial().equals(String.valueOf(ESpecial.BUSSTOP).toLowerCase())){
           drawBusStop();
        }
    }

    private void drawBusStop() {
       MCoordinate canvasCoord = westAbsCoord.toCanvasCoord();
        gc.setFill(Color.YELLOW);
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(1);

        gc.fillOval(canvasCoord.getX() + 10, canvasCoord.getY() - 8, 14, 14);
        gc.strokeOval(canvasCoord.getX() + 10, canvasCoord.getY() - 8, 14, 14);


        gc.setFill(Color.GREEN);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(
            "H" ,
            canvasCoord.getX() + 16.5,
            canvasCoord.getY() - 1
        );

    }
}
