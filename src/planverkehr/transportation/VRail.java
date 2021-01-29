package planverkehr.transportation;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.TextAlignment;
import planverkehr.Buildings;
import planverkehr.Config;
import planverkehr.MCoordinate;

public class VRail extends VTransportation {
    public VRail(Buildings road, GraphicsContext gc, MCoordinate west) {
        super(road, gc, west, Color.FIREBRICK);
        drawRoadOrRail();

        if(road.getSpecial().equals(String.valueOf(ESpecial.RAILSTATION).toLowerCase())){
            drawRailStation();
        } else if (road.getSpecial().equals(String.valueOf(ESpecial.SIGNAL).toLowerCase())){
            drawSignal();
        }
    }

    private void drawSignal() {
        double centerX = westX + Config.tWidthHalft;
        double centerY = westY;
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
        gc.setFill(Color.DARKBLUE);

        gc.fillRect(this.westX + 10, this.westY - 8, 14, 14);



        gc.setFill(Color.WHITESMOKE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(
            "U" ,
            this.westX + 16.5,
            this.westY - 1
        );
    }
}
