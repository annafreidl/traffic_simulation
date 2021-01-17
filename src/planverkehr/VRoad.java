package planverkehr;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class VRoad {
    public VRoad(Buildings road, MCoordinate west, GraphicsContext gc, MCoordinate center) {
        double centerX = center.getX();
        double centerY = center.getY();

        gc.setFill(Color.DIMGREY);
        gc.setStroke(Color.DIMGREY);
        gc.setLineWidth(2);
        gc.fillOval(center.getX() - 5, center.getY() - 5, 10, 10);

        road.getPoints().forEach((key, coord) -> {
            MCoordinate temp = new MCoordinate(0, 0);
            if (!key.equals("c")) {
              //  MCoordinates temp = center.getAbsoluteCoordinates(coord);
                switch (key) {
                    case "ne": //0.5 : 1
                        temp.setX(centerX + Config.tHeightHalft);
                        temp.setY(centerY - (Config.tHeightHalft / 2));
                        break;
                    case "nw": //0.5 : 0
                        temp.setX(centerX - Config.tHeightHalft);
                        temp.setY(centerY - (Config.tHeightHalft / 2));
                        break;
                    case "se": //(1: 0.5)
                        temp.setX(centerX + Config.tHeightHalft);
                        temp.setY(centerY + (Config.tHeightHalft / 2));
                        break;
                    case "sw": // (0 : 0.5)
                        temp.setX(centerX - Config.tHeightHalft);
                        temp.setY(centerY + (Config.tHeightHalft / 2));
                }
                gc.strokeLine(center.getX(), center.getY(), temp.getX(), temp.getY());
            }
        });
    }
}
