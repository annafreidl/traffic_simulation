package planverkehr.transportation;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import planverkehr.Buildings;
import planverkehr.MCoordinate;

import java.util.Locale;

public class VRoad extends VTransportation{
    public VRoad(Buildings road, GraphicsContext gc, MCoordinate west) {
        super(road, gc, west, Color.DIMGRAY);
        drawRoadOrRail();

       if(road.getSpecial().equals(String.valueOf(ESpecial.BUSSTOP).toLowerCase())){
           drawBusStop();
        }
    }

    private void drawBusStop() {
        gc.setFill(Color.YELLOW);
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(1);

        gc.fillOval(this.westX + 10, this.westY - 8, 14, 14);
        gc.strokeOval(this.westX + 10, this.westY - 8, 14, 14);


        gc.setFill(Color.GREEN);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(
            "H" ,
            this.westX + 16.5,
            this.westY - 1
        );

    }
}
