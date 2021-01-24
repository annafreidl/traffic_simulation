package planverkehr;

import java.util.ArrayList;

public class MTile {
    String id;
    boolean isSelected = false;
    boolean shouldDraw = true;
    double  xNew, yNew, xIsoWest, yIsoWest;
    MCoordinate gridCoordinates;
    MCoordinate isoWest, isoSouth, isoNorth, isoEast, isoCenter;
    EBuildType state;
    String building;
    Buildings connectedBuilding;
    Buildings buildingOnTile;
    ArrayList<ArrayList<Double>> felder = new ArrayList<>();
    ArrayList<Double> punkt = new ArrayList<>();

    public MTile(MCoordinate gridCoordinates, MCoordinate isoCoordinates, String id) {
        this.id=id;
        isoWest = isoCoordinates;
        xIsoWest = isoWest.getX();
        yIsoWest = isoWest.getY();

        this.gridCoordinates = gridCoordinates;
        xNew = gridCoordinates.getX();
        yNew = gridCoordinates.getY();

        state = EBuildType.free;
        building = "";
        buildingOnTile = null;

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

    public MCoordinate getIDCoordinates(){
        int indexOfSeparator = this.id.indexOf("-");

        String xString;
        String yString;
        if (indexOfSeparator != -1) {
            xString = this.id.substring(0, indexOfSeparator);
            yString = this.id.substring(indexOfSeparator + 2);

            if(yString.length() == 0){
                yString = this.id.substring(indexOfSeparator + 1);
            }

            int x = Integer.parseInt(xString);
            int y = Integer.parseInt(yString);
            return new MCoordinate(x, y);
        } else {
            System.out.println("ID-Fehler");
            return new MCoordinate(-1, -1);
        }

    }

    public void addConnectedBuilding(Buildings connectedBuilding) {
        this.connectedBuilding = connectedBuilding;
    }

    public String getId() {
        return id;
    }

    public Buildings getConnectedBuilding() {
        return connectedBuilding;
    }


    public String getBuilding(){
        return building;
    }

    public void setBuilding(String building){
        this.building = building;
    }

    public void setBuildingOnTile(Buildings buildingOnTile){ this.buildingOnTile = buildingOnTile; }

    public Buildings getBuildingOnTile(){return buildingOnTile;}

    public boolean isFree() {
        return this.state == EBuildType.free;
    }

    public void setShouldDraw(boolean shouldDraw) {
        this.shouldDraw = shouldDraw;
    }
}
