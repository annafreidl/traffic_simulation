package planverkehr;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import planverkehr.transportation.VRail;
import planverkehr.transportation.VRoad;


public class VTile {
    MTile tileModel;

    Image image;
    double imageScale;
    double imageHeight;
    double imageWidth;


    public VTile(MTile tileModel) {
        this.tileModel = tileModel;
        this.image = null;
        imageScale = 0;
        imageHeight = 0;
        imageWidth = 0;
    }

    public void drawBackground(GraphicsContext gc) {
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
            (int) tileModel.getGridCoordinates().getX() + "-" + ((int) tileModel.getGridCoordinates().getY() - Config.worldWidth + 1),
            tileModel.getXIsoWest(),
            tileModel.getYIsoWest() - 2
        );
    }

    public void drawForeground(GraphicsContext gc) {

        double xIso = tileModel.getXIsoWest();
        double yIso = tileModel.getYIsoWest();
        double widthHalf = Config.tWidth / 2;
        double heightHalf = Config.tWidth / 4;

        String building = tileModel.getBuilding();

        //pics must have same scale!
        //alle gleiche kann in default bleiben
        //wenn Buildings NICHT gleiche Scales haben, dann verschiedene cases und anpassen

        switch (tileModel.getState()) {

            case building -> {
                String buildingName = building.replace(" ","-");
                image = new Image("Images/" + buildingName + ".png"); //Bilder muessen so benannt sein wie Menu-Items!!

                imageScale = widthHalf / image.getWidth();
                imageHeight = image.getHeight() * imageScale;
                imageWidth = image.getWidth() * imageScale;

                gc.drawImage(image, (xIso + heightHalf), (yIso - widthHalf), imageWidth, imageHeight);
            }

            case road -> {
                new VRoad(tileModel.getConnectedBuilding(), gc, tileModel.getIsoWest());
            }

            case rail -> {
                new VRail(tileModel.getConnectedBuilding(), gc, tileModel.getIsoWest());
            }

            case airport -> {
                String buildingName = building.replace(" ","-");
                System.out.println(buildingName);
                image = new Image("Images/" + buildingName + ".png"); //Bilder muessen so benannt sein wie Menu-Items!!

                imageScale = widthHalf / image.getWidth();
                imageHeight = image.getHeight() * imageScale;
                imageWidth = image.getWidth() * imageScale;

                gc.drawImage(image, (xIso + heightHalf), (yIso - widthHalf), imageWidth, imageHeight);
            }

            case nature -> {
                String buildingName = building.replace(" ","-");
                image = new Image("Images/" + buildingName + ".png"); //Bilder muessen so benannt sein wie Menu-Items!!

                imageScale = widthHalf / image.getWidth();
                imageHeight = image.getHeight() * imageScale;
                imageWidth = image.getWidth() * imageScale;

                gc.drawImage(image, (xIso + heightHalf), (yIso - widthHalf), imageWidth, imageHeight);
            }

           /* default -> {
                gc.drawImage(image, (xIso + heightHalf), (yIso - widthHalf), imageWidth, imageHeight);
            }*/


       /* switch (tileModel.getState()) {
            case building -> {
                Image image = new Image("Images/building.png");

                double scale = widthHalf / image.getWidth();
                double height = image.getHeight() * scale;
                double width = image.getWidth() * scale;
                gc.drawImage(image, (xIso + heightHalf), (yIso - widthHalf), width, height);

            }
            case road -> {
                new VRoad(tileModel.getConnectedBuilding(), gc, tileModel.getIsoWest());
            }
            case rail -> {
                new VRail(tileModel.getConnectedBuilding(), gc, tileModel.getIsoWest());
            }
        }*/
        }
    }
}
