package planverkehr.verkehrslinien;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import planverkehr.Config;
import planverkehr.MCoordinate;
import planverkehr.graph.MWegKnotenpunkt;

public class VActiveLinie {
    public VActiveLinie(MWegKnotenpunkt wp, GraphicsContext gcFront, boolean isActive, Color color) {
        MCoordinate canvasCoord = wp.getKnotenpunkt().getVisibleCoordinate().toCanvasCoord();
        gcFront.setFill(color);
        if(isActive) {
            gcFront.fillText("" + wp.getBetretenUm(), canvasCoord.getX() + Config.tHeightHalft, canvasCoord.getY() );
        }
    }
}
