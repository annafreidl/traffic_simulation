package planverkehr;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class VVehicle {
    double isoX;
    double isoY;
    public VVehicle(MVehicles vehicle, GraphicsContext gc) {
        isoX = vehicle.getCurrentPosition().toIso().getX();
        isoY = vehicle.getCurrentPosition().toIso().getY();
        gc.setFill(Color.BLUEVIOLET);
        gc.fillRect(isoX, isoY, 5, 5);
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(
            "" + vehicle.getId() ,
            isoX,
            isoY - 2
        );
    }
}
