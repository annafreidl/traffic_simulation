package planverkehr;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class VRoad extends Canvas {
    GraphicsContext gc = this.getGraphicsContext2D();

    public VRoad() {
        this.setWidth(50);
        this.setHeight(50);
        draw2DShapes(gc);
    }

    private void draw2DShapes(GraphicsContext gc) {
        gc.setFill(Color.GRAY);
        gc.setStroke(Color.WHITE);
        //v: wie weit von der linken Canvas Grenze entfernt
        //v1: wie weit von der oberen Canvas Grenze entfernt
        //v2: bis wohin ab v --> vertikal
        //v3: bis wohin ab v1 --> horizontal
        gc.fillRect(0, 15,50, 20);
        gc.strokeLine(5, 25, 15, 25);
        gc.strokeLine(20, 25, 30, 25);
        gc.strokeLine(35, 25, 45, 25);

    }

}
