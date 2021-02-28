package planverkehr.verkehrslinien;

import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import planverkehr.MCoordinate;
import planverkehr.graph.MWegKnotenpunkt;

public class VLinie {
    MLinie l;
    GraphicsContext gc;
    int ID;
    Group group;

    public VLinie(GraphicsContext gc, Group group, MLinie l, int iD){
        this.l = l;
        this.gc = gc;
        this.ID = iD;
        this.group = group;
        gc.setFill(l.getColor());
        gc.setStroke(l.getColor());
        drawOnRoads();
    }

    private void drawOnRoads() {
        for (MWegKnotenpunkt wp: l.getListeAllerLinienKnotenpunkte()
             ) {
            MCoordinate canvasCoordCurrent = wp.getKnotenpunkt().getVisibleCoordinate().toCanvasCoord();
            MCoordinate canvasCoordPrev = wp.getVorgaenger().getVisibleCoordinate().toCanvasCoord();
            gc.strokeLine(canvasCoordCurrent.getX(), canvasCoordCurrent.getY(), canvasCoordPrev.getX(), canvasCoordPrev.getY());
        }
    }


}
