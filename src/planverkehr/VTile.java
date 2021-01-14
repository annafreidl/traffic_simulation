package planverkehr;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;


public class VTile  {
    MFeld tileModel;


    public VTile(MFeld tileModel, GraphicsContext gc) {
        this.tileModel = tileModel;

        int x = tileModel.getX();
        int y = tileModel.getY();
        double xIso = tileModel.getXIso();
        double yIso = tileModel.getYIso();
        double widthHalf = Config.tWidth / 2;
        double heightHalf = Config.tWidth / 4;

        gc.beginPath();
        gc.moveTo(xIso, yIso);
        gc.lineTo(xIso + widthHalf, yIso - heightHalf);
        gc.lineTo(xIso + Config.tWidth, yIso);
        gc.lineTo(xIso + widthHalf, yIso + heightHalf);

        if (tileModel.getIsSelected()){
            gc.setFill(Color.LIME);
        } else {
            gc.setFill(Color.LIMEGREEN);
        }

        gc.closePath();
        gc.fill();
        gc.stroke();
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(
            x + "-" + (y - Config.worldWidth + 1),
            xIso,
            yIso - 2
        );
    }
}
