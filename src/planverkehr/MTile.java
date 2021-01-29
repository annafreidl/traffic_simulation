package planverkehr;

import planverkehr.graph.Graph;
import planverkehr.graph.MKnotenpunkt;
import planverkehr.transportation.EDirections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;

public class MTile {
    String id;
    boolean isSelected = false;
    boolean isFirstTile = true;
    double xNew, yNew, xIsoWest, yIsoWest, differenz;
    int höhenorth, höheeast, höhesouth, höhewest, level;
    ArrayList<MKnotenpunkt> knotenpunkteArray;
    EnumSet<EDirections> possibleConnections = EnumSet.noneOf(EDirections.class);
    MCoordinate gridCoordinates;
    MCoordinate isoWest, isoSouth, isoNorth, isoEast, isoCenter;
    EBuildType state;
    String building;
    Buildings connectedBuilding;
    ArrayList höhen = new ArrayList(4);
    Buildings buildingOnTile;
    ArrayList<MCoordinate> punkte = new ArrayList();

    public MTile(MCoordinate gridCoordinates, MCoordinate isoCoordinates, String id) {
        this.id = id;
        this.id = id;
        höhen.addAll(Arrays.asList(höhenorth, höheeast, höhesouth, höhewest));
        differenz = (Integer) Collections.max(höhen) - (Integer) Collections.min(höhen);
        level = 0;
        isoWest = isoCoordinates;
        xIsoWest = isoWest.getX();
        yIsoWest = isoWest.getY();

        this.gridCoordinates = gridCoordinates;
        xNew = gridCoordinates.getX();
        yNew = gridCoordinates.getY();

        state = EBuildType.free;
        building = "";
        buildingOnTile = null;

        isoNorth = new MCoordinate(xIsoWest + Config.tWidthHalft, yIsoWest - Config.tHeightHalft);
        isoEast = new MCoordinate(xIsoWest + Config.tWidth, yIsoWest);
        isoSouth = new MCoordinate(xIsoWest + Config.tWidthHalft, yIsoWest + Config.tHeightHalft);
        isoCenter = new MCoordinate(xIsoWest + Config.tWidthHalft, yIsoWest);

        knotenpunkteArray = new ArrayList<>();

        punkte.addAll(Arrays.asList(isoNorth, isoEast, isoSouth, isoWest));

    }

    public void changeIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public ArrayList<MCoordinate> getPunkte() {
        return punkte;
    }

    ;

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

    public MCoordinate getIDCoordinates() {
        int indexOfSeparator = this.id.indexOf("-");

        String xString;
        String yString;
        if (indexOfSeparator != -1) {
            xString = this.id.substring(0, indexOfSeparator);
            yString = this.id.substring(indexOfSeparator + 2);

            if (yString.length() == 0) {
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


    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setBuildingOnTile(Buildings buildingOnTile) {
        this.buildingOnTile = buildingOnTile;
    }

    public Buildings getBuildingOnTile() {
        return buildingOnTile;
    }

    public boolean isFree() {
        return this.state == EBuildType.free;
    }

    public void setFirstTile(boolean firstTile) {
        this.isFirstTile = firstTile;
    }

    public void addKnotenpunkt(MKnotenpunkt mKnotenpunkt) {
        knotenpunkteArray.add(mKnotenpunkt);
        if (mKnotenpunkt.isEdge()) {
            possibleConnections.add(getOppositeDirection(mKnotenpunkt.getDirection()));
        }
    }

    public ArrayList<MKnotenpunkt> getKnotenpunkteArray() {
        return knotenpunkteArray;
    }

    private EDirections getOppositeDirection(EDirections directions) {
        EDirections oppositeDirection;
        switch (directions) {
            case NW -> oppositeDirection = EDirections.SE;
            case NE -> oppositeDirection = EDirections.SW;
            case SE -> oppositeDirection = EDirections.NW;
            default -> oppositeDirection = EDirections.NE;
        }
        return oppositeDirection;

    }

    public Optional<MKnotenpunkt> getNodeByCoordinatesString(String coordsString) {
        MKnotenpunkt knotenpunkt = null;
        boolean searching = true;
        for (int i = 0; i < knotenpunkteArray.size() && searching; i++) {
            if (knotenpunkteArray.get(i).getGridCoordinate().toStringCoordinates().equals(coordsString)) {
                knotenpunkt = knotenpunkteArray.get(i);
                searching = false;
            }
        }
        return Optional.ofNullable(knotenpunkt);
    }

    public EBuildType getConnectedBuildingType() {
        return connectedBuilding.getBuildType();
    }


    public Optional<MKnotenpunkt> getNodeByName(String name) {
        MKnotenpunkt knotenpunkt = null;
        boolean searching = true;
        for (int i = 0; i < knotenpunkteArray.size() && searching; i++) {
            if (knotenpunkteArray.get(i).getName().equals(name)) {
                knotenpunkt = knotenpunkteArray.get(i);
                searching = false;
            }
        }
        return Optional.ofNullable(knotenpunkt);
    }

    public String getGroupId() {
        CharSequence nameArray = this.connectedBuilding.getBuildingName();
        String possibleID = "";
        int idOccurrences = 0;
        for (int i = 0; i < getKnotenpunkteArray().size() && idOccurrences < 2; i++) {
            possibleID = getKnotenpunkteArray().get(i).getGroupId().get(0);
            idOccurrences = 0;
            System.out.println("Enthählt possibleID: [" + possibleID + "] nameArray: [" + nameArray + "] ?");

            if (possibleID.contains(nameArray)) {
                System.out.println("Ja");

                for (MKnotenpunkt k : getKnotenpunkteArray()) {
                    for (String id : k.getGroupId()) {
                        System.out.println("entspricht id: [" + id + "] der possibleID: [" + possibleID + "] ?");
                        if (id.equals(possibleID)) {
                            System.out.println("Ja");
                            idOccurrences++;
                        } else {
                            System.out.println("nein");
                        }
                    }
                }
            } else {
                System.out.println("nein");
            }

        }

        return idOccurrences > 1 ? possibleID : "";
    }

    public void reset() {

        isFirstTile = true;
        knotenpunkteArray = new ArrayList<>();
        possibleConnections = EnumSet.noneOf(EDirections.class);
        state = EBuildType.free;
        building = null;
        connectedBuilding = null;
        buildingOnTile = null;

    }
}
