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
        super(road, gc, westVisibleCoord, Color.WHITESMOKE, punkteNeu, isSchraeg, level, hoch);


       if(road.getSpecial().equals(String.valueOf(ESpecial.BUSSTOP).toLowerCase())){
           drawBusStop();
        }
        drawRoadOrRail();
    }

    private void drawBusStop() {
       MCoordinate canvasCoord = westAbsCoord.toCanvasCoord();
        gc.setFill(Color.rgb(253, 229, 0));
        gc.setStroke(Color.rgb(2, 158, 25));

        gc.setLineWidth(Config.tWidth/50);

        gc.fillOval(canvasCoord.getX() + Config.tHeightHalft / 2, canvasCoord.getY() - Config.tHeightHalft / 4, Config.tHeightHalft / 2, Config.tHeightHalft / 2);
        gc.strokeOval(canvasCoord.getX() + Config.tHeightHalft / 2, canvasCoord.getY() - Config.tHeightHalft /4, Config.tHeightHalft / 2 , Config.tHeightHalft / 2);


        gc.setFill(Color.GREEN);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(new Font(Config.tWidth / 10));
        gc.fillText(
            "H" ,
            canvasCoord.getX() + (double)Config.tHeightHalft / 2 + Config.tHeightHalft / 4,
            canvasCoord.getY() - 1 + Config.tWidth/100
        );

    }
}
