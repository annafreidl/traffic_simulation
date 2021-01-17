package planverkehr;

import java.util.ArrayList;

public class MTile {
    String id;
    boolean isSelected = false;
    double  xNew, yNew, xIsoWest, yIsoWest;
    MCoordinate gridCoordinates;
    MCoordinate isoWest, isoSouth, isoNorth, isoEast, isoCenter;
    EBuildType state;
    Buildings connectedBuilding;
    ArrayList felder = new ArrayList();
    ArrayList punkt = new ArrayList();

    public MTile(MCoordinate gridCoordinates, MCoordinate isoCoordinates, String id) {
        this.id=id;
        isoWest = isoCoordinates;
        xIsoWest = isoWest.getX();
        yIsoWest = isoWest.getY();

        this.gridCoordinates = gridCoordinates;
        xNew = gridCoordinates.getX();
        yNew = gridCoordinates.getY();

        state = EBuildType.free;

        isoSouth = new MCoordinate(xIsoWest + Config.tWidthHalft, yIsoWest + Config.tHeightHalft);
        isoEast = new MCoordinate(xIsoWest + Config.tWidth, yIsoWest);
        isoNorth = new MCoordinate(xIsoWest + Config.tWidthHalft, yIsoWest - Config.tHeightHalft);
        isoCenter = new MCoordinate(xIsoWest + Config.tWidthHalft, yIsoWest);


        punkt.add(xNew);
        punkt.add(yNew);
        felder.add(punkt);
    }

    public void changeIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public double getXIsoWest() {
        return xIsoWest;
    }

    public double getYIsoWest() {
        return yIsoWest;
    }

    public MCoordinate getIsoCenter() {
        return isoCenter;
    }

    public MCoordinate getIsoEast() {
        return isoEast;
    }

    public MCoordinate getIsoSouth() {
        return isoSouth;
    }

    public MCoordinate getIsoNorth() {
        return isoNorth;
    }

    public MCoordinate getIsoWest() {
        return isoWest;
    }

    public EBuildType getState() {
        return state;
    }

    public void setState(EBuildType state) {
        this.state = state;
    }

    public MCoordinate getGridCoordinates() {
        return gridCoordinates;
    }

    public void setConnectedBuilding(Buildings connectedBuilding) {
        this.connectedBuilding = connectedBuilding;
    }

    public String getId() {
        return id;
    }

    public Buildings getConnectedBuilding() {
        return connectedBuilding;
    }
}
