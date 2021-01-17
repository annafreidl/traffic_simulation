package planverkehr;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.Map;

public class VRoadMenu extends Canvas {
    GraphicsContext gc = this.getGraphicsContext2D();

    public VRoadMenu(Map<String, MCoordinate> roadDirections) {
        this.setWidth(20);
        this.setHeight(20);
       // gc.rotate(45);
        rotate(325, 10, 10);
        draw2DShapes(gc, roadDirections);

    }

    private void rotate(double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    private void draw2DShapes(GraphicsContext gc, Map<String, MCoordinate> roadDirections) {
        gc.setFill(Color.GRAY);
        gc.setStroke(Color.GRAY);
        //v: wie weit von der linken Canvas Grenze entfernt
        //v1: wie weit von der oberen Canvas Grenze entfernt
        //v2: bis wohin ab v --> vertikal
        //v3: bis wohin ab v1 --> horizontal
        gc.fillOval(9, 9, 2, 2);
        roadDirections.forEach((key, coord) -> {
            if(!key.equals("c")){
                gc.strokeLine(10, 10, (coord.getY()*20), (coord.getX()*20));
            }
            });


//        gc.fillRect(0, 15,50, 20);
//        gc.strokeLine(5, 25, 15, 25);
//        gc.strokeLine(20, 25, 30, 25);
//        gc.strokeLine(35, 25, 45, 25);

    }

}
