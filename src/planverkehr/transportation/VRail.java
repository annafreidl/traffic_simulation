package planverkehr.transportation;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.TextAlignment;
import planverkehr.Buildings;
import planverkehr.Config;
import planverkehr.MCoordinate;

import java.util.ArrayList;

public class VRail extends VTransportation {
    public VRail(Buildings road, GraphicsContext gc, ArrayList<MCoordinate> punkteNeu, MCoordinate westVisibleCoord, boolean isSchraeg, int level, boolean hoch) {
        super(road, gc, westVisibleCoord, Color.FIREBRICK, punkteNeu, isSchraeg, level, hoch);
        drawRoadOrRail();

        if(road.getSpecial().equals(String.valueOf(ESpecial.RAILSTATION).toLowerCase())){
            drawRailStation();
        } else if (road.getSpecial().equals(String.valueOf(ESpecial.SIGNAL).toLowerCase())){
            drawSignal();
        }
    }

    private void drawSignal() {
        double schraege;
        if(isSchraeg) {
            schraege = gehtHoch ? westAbsolutZ - 0.5 : westAbsolutZ + 0.5;
        } else {
            schraege = westAbsolutZ;
        }
       MCoordinate centerCoord = new MCoordinate(0.5 + westAbsolutX, westAbsolutY - 0.5, schraege).toCanvasCoordWithoutOffset();

        double centerX = centerCoord.getX();
        double centerY = centerCoord.getY();
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineWidth(1);
        gc.strokeLine(centerX, centerY - 10, centerX, centerY-2);
        gc.fillRoundRect(centerX - 2.5, centerY - 15, 5, 10, 5 ,5);
        gc.setFill(Color.GREEN);
        gc.fillOval(centerX - 1.4, centerY - 13.5, 2.5, 2.5);
        gc.fillOval(centerX - 1.4, centerY - 9.5, 2.5, 2.5);
    }

    private void drawRailStation() {
        MCoordinate canvasCoord = westAbsCoord.toCanvasCoord();
        gc.setFill(Color.DARKBLUE);

        gc.fillRect(canvasCoord.getX() + 10, canvasCoord.getY()- 8, 14, 14);



        gc.setFill(Color.WHITESMOKE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(
            "U" ,
            canvasCoord.getX()+ 16.5,
            canvasCoord.getY() - 1
        );
    }
}
