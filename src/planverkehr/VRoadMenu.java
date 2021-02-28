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
        rotate();
        draw2DShapes(gc, roadDirections);

    }

    private void rotate() {
        Rotate r = new Rotate(325, 10, 10);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    private void draw2DShapes(GraphicsContext gc, Map<String, MCoordinate> roadDirections) {
        gc.setFill(Color.GRAY);
        gc.setStroke(Color.GRAY);

        gc.fillOval(9, 9, 2, 2);
        roadDirections.forEach((key, coord) -> {
            if(!key.equals("c")){
                gc.strokeLine(10, 10, (coord.getY()*20), (coord.getX()*20));
            }
            });


    }

}
