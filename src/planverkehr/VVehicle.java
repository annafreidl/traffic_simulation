package planverkehr;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class VVehicle {
    double isoX;
    double isoY;

    public VVehicle(MVehicles vehicle, GraphicsContext gc) {


        isoX = vehicle.getCurrentPosition().toCanvasCoord().getX();
        isoY = vehicle.getCurrentPosition().toCanvasCoord().getY();

        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(
            "" + vehicle.getId(),
            isoX - 2,
            isoY - 10
        );

        if (vehicle.getKind().equals("engine")) {
            gc.setFill(Color.NAVAJOWHITE);
            gc.fillRect(isoX, isoY, 5, 5);
        } else if (vehicle.getKind().equals("road vehicle")) {
            MCoordinate canvasCoordVehicle = new MCoordinate(isoX, isoY, 0);

//          if(vehicle.isDrivesLeft()){
//              canvasCoordVehicle = new MCoordinate(isoX - Config.tHeightHalft / 2, isoY - Config.tHeightHalft / 4, 0);
//          } else {
//              canvasCoordVehicle = new MCoordinate(isoX + Config.tHeightHalft / 2, isoY + Config.tHeightHalft / 4, 0);
//
//          }


            gc.setFill(Color.GOLDENROD);


            gc.fillRoundRect(canvasCoordVehicle.getX() + 2, canvasCoordVehicle.getY() - 8, 12, 8, 10, 10);

            if (vehicle.isAtGoal()) {
                gc.setFill(Color.BLUE);
            } else {
                gc.setFill(Color.RED);
            }
            gc.fillRoundRect(canvasCoordVehicle.getX() - 2, canvasCoordVehicle.getY() - 4, 18, 6, 5, 5);
//            gc.setFill(Color.RED);
//            gc.fillRoundRect(isoX - 2, isoY, 10, 1.5, 10, 5);
            gc.setFill(Color.LIGHTSLATEGRAY);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeOval(canvasCoordVehicle.getX(), canvasCoordVehicle.getY(), 4, 4);
            gc.fillOval(canvasCoordVehicle.getX(), canvasCoordVehicle.getY(), 4, 4);

            gc.strokeOval(canvasCoordVehicle.getX() + 10, canvasCoordVehicle.getY(), 4, 4);
            gc.fillOval(canvasCoordVehicle.getX() + 10, canvasCoordVehicle.getY(), 4, 4);


        }


    }
}
