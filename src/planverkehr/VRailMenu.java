package planverkehr;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public class VRailMenu extends Canvas {
    GraphicsContext gc = this.getGraphicsContext2D();

    public VRailMenu(Map<String, MCoordinate> railDirections, List<Pair<String, String>> l) {
        this.setWidth(20);
        this.setHeight(20);
        rotate();
        draw2DShapes(gc, railDirections, l);

    }

    private void rotate() {
        Rotate r = new Rotate(315, 10, 10);

        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    private void draw2DShapes(GraphicsContext gc, Map<String, MCoordinate> roadDirections, List<Pair<String, String>> l) {
        gc.setFill(Color.GRAY);
        gc.setStroke(Color.GRAY);


        l.forEach((pair) -> gc.strokeLine((roadDirections.get(pair.getKey()).getY() *10), (roadDirections.get(pair.getKey()).getX()*10), (roadDirections.get(pair.getValue()).getY()*10), (roadDirections.get(pair.getValue()).getX()*10)));


    }

}
