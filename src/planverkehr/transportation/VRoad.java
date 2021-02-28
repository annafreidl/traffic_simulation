package planverkehr.transportation;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import planverkehr.Buildings;
import planverkehr.Config;
import planverkehr.MCoordinate;

import java.util.ArrayList;

public class VRoad extends VTransportConnection {
    public VRoad(Buildings road, GraphicsContext gc, ArrayList<MCoordinate> punkteNeu, MCoordinate westVisibleCoord, boolean isSchraeg, int level, boolean hoch) {
        super(road, gc, westVisibleCoord, Color.WHITESMOKE, punkteNeu, isSchraeg, level);


       if(road.getSpecial().equals(String.valueOf(ESpecial.BUSSTOP).toLowerCase())){
           drawBusStop();
        }
        drawRoadOrRail();
    }

    private void drawBusStop() {
       MCoordinate canvasCoord = westAbsCoord.toCanvasCoord();
        gc.setFill(Color.rgb(253, 229, 0));
        gc.setStroke(Color.rgb(2, 158, 25));

        gc.setLineWidth((double) Config.tWidth/50);

        gc.fillOval(canvasCoord.getX() + (double) Config.tHeightHalft / 2, canvasCoord.getY() - (double) Config.tHeightHalft / 4,  (double)Config.tHeightHalft / 2,  (double)Config.tHeightHalft / 2);
        gc.strokeOval(canvasCoord.getX() + (double) Config.tHeightHalft / 2, canvasCoord.getY() - (double) Config.tHeightHalft /4,  (double)Config.tHeightHalft / 2 ,  (double)Config.tHeightHalft / 2);


        gc.setFill(Color.GREEN);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(new Font((double)Config.tWidth / 10));
        gc.fillText(
            "H" ,
            canvasCoord.getX() + (double)Config.tHeightHalft / 2 + (double) Config.tHeightHalft / 4,
            canvasCoord.getY() - 1 + (double) Config.tWidth/100
        );

    }
}
