package planverkehr;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;


public class VTile {
    MTile tileModel;


    public VTile(MTile tileModel) {
        this.tileModel = tileModel;
    }

    public void drawBackground(GraphicsContext gc){
        gc.beginPath();

        //West Point x, y
        gc.moveTo(tileModel.getXIsoWest(), tileModel.getYIsoWest());

        //North
        gc.lineTo(tileModel.getIsoNorth().getX(), tileModel.getIsoNorth().getY());

        //West
        gc.lineTo(tileModel.getIsoEast().getX(), tileModel.getIsoEast().getY());

        //South
        gc.lineTo(tileModel.getIsoSouth().getX(), tileModel.getIsoSouth().getY());

        if (tileModel.getIsSelected()) {
            gc.setFill(Color.LIME);
        } else {
            gc.setFill(Color.LIMEGREEN);
        }
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.closePath();

        gc.fill();
        gc.stroke();
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(
            (int)tileModel.getGridCoordinates().getX() + "-" + ((int)tileModel.getGridCoordinates().getY() - Config.worldWidth + 1),
            tileModel.getXIsoWest(),
            tileModel.getYIsoWest() - 2
        );
    }

    public void drawForeground(GraphicsContext gc){

        double xIso = tileModel.getXIsoWest();
        double yIso = tileModel.getYIsoWest();
        double widthHalf = Config.tWidth / 2;
        double heightHalf = Config.tWidth / 4;

        switch (tileModel.getState()) {
            case building -> {
                Image image = new Image("Images/building.png");

                double scale = widthHalf / image.getWidth();
                double height = image.getHeight() * scale;
                double width = image.getWidth() * scale;
                gc.drawImage(image, (xIso + heightHalf), (yIso - widthHalf), width, height);

            }
            case road -> {
                new VRoad(tileModel.getConnectedBuilding(), tileModel.getIsoWest(), gc, tileModel.getIsoCenter());
            }
        }
    }
}
