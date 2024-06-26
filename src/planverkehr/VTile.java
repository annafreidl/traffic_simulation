package planverkehr;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import planverkehr.transportation.VRail;
import planverkehr.transportation.VRoad;

import java.util.Collections;


public class VTile {
    MTile tileModel;
    Paint schatten, eben, hell, mittel, dunkel;

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

        double visibleXOriginal = tileModel.getVisibleCoordinates().getX();
        double visibleYOriginal = tileModel.getVisibleCoordinates().getY();
        gc.setStroke(Color.rgb(1, 89, 0));
        gc.stroke();

        if (tileModel.getIsSelected()) {
            gc.setFill(Color.LIME);
            gc.beginPath();
            MCoordinate temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
            MCoordinate tempIso = temp.toCanvasCoord();
            gc.moveTo(tempIso.getX(), tempIso.getY());
            temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
            tempIso = temp.toCanvasCoord();
            gc.lineTo(tempIso.getX(), tempIso.getY());
            temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
            tempIso = temp.toCanvasCoord();
            gc.lineTo(tempIso.getX(), tempIso.getY());
            temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
            tempIso = temp.toCanvasCoord();
            gc.lineTo(tempIso.getX(), tempIso.getY());
            gc.setLineWidth(1);
            gc.setStroke(Color.BLACK);
            gc.closePath();
            gc.fill();
            gc.stroke();
        } else {


            eben = Color.rgb(87, 210, 42);
            hell = Color.rgb(106, 255, 51);
            mittel = Color.rgb(85, 204, 41);
            dunkel = Color.rgb(43, 102, 20);
            schatten = Color.rgb(22, 51, 10);


            if (tileModel.getState() == EBuildType.road) {

                eben = Color.rgb(153, 153, 153);
                hell = Color.rgb(229, 229, 229);
                mittel = Color.rgb(204, 204, 204);
                dunkel = Color.rgb(102, 102, 102);
                schatten = Color.rgb(51, 51, 51);
            } else if (tileModel.getState() == EBuildType.factory) {
                eben = Color.rgb(205, 112, 84);
                hell = Color.rgb(205, 112, 84);
                mittel = Color.rgb(205, 112, 84);
                dunkel = Color.rgb(205, 112, 84);
                schatten = Color.rgb(205, 112, 84);
            } else if (Collections.min(tileModel.höhen) < 0) {
                eben = Color.rgb(44, 100, 145);
                hell = Color.rgb(145, 93, 15);
                mittel = Color.rgb(133, 85, 13);
                dunkel = Color.rgb(107, 69, 11);
                schatten = Color.rgb(96, 44, 7);
            } else if (tileModel.getState() == EBuildType.cathedral) {
                eben = Color.rgb(255, 255, 0);
                hell = Color.rgb(205, 205, 0);
                mittel = Color.rgb(205, 173, 0);
                dunkel = Color.rgb(166, 132, 0);
                schatten = Color.rgb(139, 117, 10);
            } else if (tileModel.getState() == EBuildType.cathedral_foundation) {
                eben = Color.rgb(125, 125, 90);
                hell = Color.rgb(120, 125, 90);
                mittel = Color.rgb(120, 143, 90);
                dunkel = Color.rgb(116, 112, 90);
                schatten = Color.rgb(119, 117, 90);
            } else if (tileModel.getState() == EBuildType.cathedral_nave) {
                eben = Color.rgb(255, 25, 15);
                hell = Color.rgb(205, 25, 15);
                mittel = Color.rgb(205, 17, 15);
                dunkel = Color.rgb(166, 13, 15);
                schatten = Color.rgb(139, 11, 15);
            }

           
            switch (tileModel.höhendif().toString()) {
                case "[0, 0, 0, 0]", "[1, 1, 1, 1]":
                    //flat
                    gc.beginPath();
                    MCoordinate temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    MCoordinate tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());

                    gc.setFill(eben);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;


                case "[0, 0, 0, 1]":
                    //north
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    //gc.lineTo(tileModel.getIsoNorth().getX(), tileModel.getIsoNorth().getY());
                    gc.setFill(schatten);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    //south
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(dunkel);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                case "[0, 0, 1, 0]":
                    //east
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(schatten);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    //west
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(mittel);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                case "[0, 1, 0, 0]":
                    //north
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(mittel);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    //south
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(hell);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                case "[1, 0, 0, 0]":
                case "[1, 0, 1, 0]":
                    //east
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(dunkel);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    //west
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(hell);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                case "[1, 1, 0, 0]":
                    //inclined
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(hell);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                case "[1, 0, 0, 1]":
                    //flat
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(dunkel);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                case "[0, 0, 1, 1]":
                    //inclined
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(schatten);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                case "[0, 1, 1, 0]":
                    //inclined
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(mittel);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                case "[1, 1, 1, 0]":
                    //north
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(hell);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    //south
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(mittel);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                case "[1, 0, 1, 1]":
                    //north
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(dunkel);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    //south
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(schatten);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                case "[1, 1, 0, 1]":
                case "[0, 1, 0, 1]":
                    //east
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(hell);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    //west
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(dunkel);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                case "[0, 1, 1, 1]":
                    //east
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(mittel);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    //west
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    gc.setFill(schatten);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    break;
                //west


                default:
                    gc.beginPath();
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getEast().getX(), visibleYOriginal - tileModel.getEast().getY(), tileModel.getEast().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getSouth().getX(), visibleYOriginal - tileModel.getSouth().getY(), tileModel.getSouth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.lineTo(tempIso.getX(), tempIso.getY());
                    temp = new MCoordinate(visibleXOriginal + tileModel.getNorth().getX(), visibleYOriginal - tileModel.getNorth().getY(), tileModel.getNorth().getZ());
                    tempIso = temp.toCanvasCoord();
                    gc.moveTo(tempIso.getX(), tempIso.getY());

                    // gc.lineTo(gridX + tileModel.getNorth().toIso().getX(), gridY + tileModel.getIsoNorth().getY());
                    gc.setFill(eben);
                    gc.fill();
                    gc.stroke();
                    gc.closePath();
                    // throw new IllegalStateException("Unexpected value: " + tileModel.höhen.toString());
            }
        }
    }


    public void drawForeground(GraphicsContext gc) {

        double visibleXOriginal = tileModel.getVisibleCoordinates().getX();
        double visibleYOriginal = tileModel.getVisibleCoordinates().getY();

        //Hilfestellung: Koordinaten an den Westnodes der Tiles
        gc.setFill(Color.rgb(0, 0, 0, 0.75));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(new Font(Config.tHeightHalft / 1.5));
        gc.setTextBaseline(VPos.CENTER);
        MCoordinate temp = new MCoordinate(visibleXOriginal + tileModel.getWest().getX(), visibleYOriginal - tileModel.getWest().getY(), tileModel.getWest().getZ());
        MCoordinate tempIso = temp.toCanvasCoord();
//        if (tileModel.getVisibleCoordinates().getY() - Config.worldWidth + 1 == 0) {
//            gc.fillText(
//                (int) tileModel.getVisibleCoordinates().getX() + "-" + ((int) tileModel.getVisibleCoordinates().getY() - Config.worldWidth + 1),
//                tempIso.getX(),
//                tempIso.getY() - 2, 100
//            );
//        } else {
//            gc.fillText(
//                (int) tileModel.getVisibleCoordinates().getX() + "" + ((int) tileModel.getVisibleCoordinates().getY() - Config.worldWidth + 1),
//                tempIso.getX(),
//                tempIso.getY() - 2, 100
//            );
//        }


        //pics must have same scale!
        //alle gleiche kann in default bleiben
        //wenn Buildings NICHT gleiche Scales haben, dann verschiedene cases und anpassen
        if (tileModel.getConnectedBuilding() != null) {
            Buildings b = tileModel.getConnectedBuilding();

            switch (tileModel.getState()) {
                case factory, nature -> {
                    image = new Image("Images/" + b.getBuildingName() + ".png");
                    drawInCenter(image, gc);
                }
                case cathedral -> {
                    switch (b.getCathedralState()) {
                        case ground -> drawInCenter(new Image("Images/cathedral.png"), gc);
                        case foundation -> drawInCenter(new Image("Images/cathedral_foundation.png"), gc);
                        case nave -> drawInCenter(new Image("Images/cathedral_nave.png"), gc);

                    }
                }

                case road -> new VRoad(tileModel.getConnectedBuilding(), gc, tileModel.getPunkteNeu(), tileModel.getVisibleCoordinates(), tileModel.isSchraeg(), tileModel.getLevel(), tileModel.isHoch());

                case rail -> new VRail(tileModel.getConnectedBuilding(), gc, tileModel.getPunkteNeu(), tileModel.getVisibleCoordinates(), tileModel.isSchraeg(), tileModel.getLevel(), tileModel.isHoch());

                case airport -> {
                    String buildingName = b.getBuildingName().replace(" ", "-");
                    image = new Image("Images/" + buildingName + ".png"); //Bilder muessen so benannt sein wie Menu-Items!!
                    drawInCenter(image, gc);
                }
                case cathedral_foundation, cathedral_nave -> image = new Image("Images/" + b.getEbuildType() + ".png");

            }
        }
    }

    public void drawInCenter(Image image, GraphicsContext gc) {

        imageScale = Config.tWidthHalft / image.getWidth();
        imageHeight = image.getHeight() * imageScale;
        imageWidth = image.getWidth() * imageScale;

        double schraege;
        MCoordinate westRelativ = tileModel.punkteNeu.get(3);
        MCoordinate westVisible = tileModel.getVisibleCoordinates();
        boolean gehtHoch = tileModel.punkteNeu.get(3).getZ() != tileModel.getLevel();
        MCoordinate westAbsolut = new MCoordinate(westVisible.getX() + westRelativ.getX(), westVisible.getY() + westRelativ.getY(), westRelativ.getZ());
        double westAbsolutX = westAbsolut.getX();
        double westAbsolutY = westAbsolut.getY();
        double westAbsolutZ = westAbsolut.getZ();
        if (tileModel.isSchraeg()) {
            schraege = gehtHoch ? westAbsolutZ - 0.5 : westAbsolutZ + 0.5;
        } else {
            schraege = westAbsolutZ;
        }
        MCoordinate centerCoord = new MCoordinate(0.5 + westAbsolutX, westAbsolutY - 0.5, schraege).toCanvasCoordWithoutOffset();

        gc.drawImage(image, centerCoord.getX() - imageWidth / 2, centerCoord.getY() - centerCoord.getZ() - imageHeight + (double) Config.tHeightHalft / 2, imageWidth, imageHeight);
    }
}