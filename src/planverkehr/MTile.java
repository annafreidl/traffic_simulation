package planverkehr;

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
    boolean hoch = true;
    double xNew, yNew, xIsoWest, yIsoWest;
    int level;
    boolean isStation = false;
    EnumSet<EDirections> possibleConnections = EnumSet.noneOf(EDirections.class);
    MCoordinate visibleCoordinates, isoWest, isoSouth, isoNorth, isoEast, isoCenter, north, south, east, west;
    EBuildType state;
    int haltestellenID = -1;
    Buildings connectedBuilding, buildingOnTile;
    ArrayList<MKnotenpunkt> knotenpunkteArray;
    ArrayList<Integer> höhen = new ArrayList<>(4);
    ArrayList<MCoordinate> punkte = new ArrayList();
    ArrayList<MCoordinate> punkteNeu = new ArrayList();

    public MTile(MCoordinate visibleCoordinate, MCoordinate canvasCoordinates, String id) {
        this.id = id;
        höhen.addAll(Arrays.asList(0,0,0,0));
        level = 0;
        isoWest = canvasCoordinates;
        xIsoWest = isoWest.getX();
        yIsoWest = isoWest.getY();

        this.visibleCoordinates = visibleCoordinate;
        xNew = visibleCoordinate.getX();
        yNew = visibleCoordinate.getY();

        state = EBuildType.free;
        buildingOnTile = null;

        isoNorth = new MCoordinate(xIsoWest + Config.tWidthHalft, yIsoWest - Config.tHeightHalft, 0);
        isoEast = new MCoordinate(xIsoWest + Config.tWidth, yIsoWest, 0);
        isoSouth = new MCoordinate(xIsoWest + Config.tWidthHalft, yIsoWest + Config.tHeightHalft, 0);
        isoCenter = new MCoordinate(xIsoWest + Config.tWidthHalft, yIsoWest, 0);

        knotenpunkteArray = new ArrayList<>();

        punkte.addAll(Arrays.asList(isoNorth, isoEast, isoSouth, isoWest));

        createCornerPoints();
        createHöhenArray();
        getIncline();
    }

    // Tile schräg? falls ja: true, falls nein: false
    public boolean getIncline() {
        boolean incline;
        if((Collections.max(this.höhen) - Collections.min(this.höhen))==0 ){
            incline = false;
        }
        else incline = true;

        return incline;
    }

    public ArrayList<Integer> createHöhenArray() {
        for (MCoordinate c: punkteNeu
             ) {
            höhen.set(punkteNeu.indexOf(c) , (int) c.getZ());
        }
        return höhen;
    }

    public ArrayList<MCoordinate> intersection(MTile mTile) {
        ArrayList<MCoordinate> list = new ArrayList<>();

        for (MCoordinate t : this.getPunkte()) {
            for (MCoordinate p : mTile.getPunkte()) {
                if (t.istGleich(p)) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    public ArrayList<Integer> höhendif(){
        int min = Collections.min(höhen);
        ArrayList<Integer> höhendiff = new ArrayList();
        for(int höhe : höhen){
            höhendiff.add(höhe-min);
        }
        return höhendiff;
    }

    public Integer TileDz() {

        int dz = Collections.max(höhendif())-Collections.min
            (höhendif());
        return dz;
    }

    private void createCornerPoints() {
        west = new MCoordinate(0, 0, 0);
        north = new MCoordinate(0, 1, 0);
        east = new MCoordinate(1, 1, 0);
        south = new MCoordinate(1, 0, 0);
        punkteNeu = new ArrayList<>();
        punkteNeu.addAll(Arrays.asList(north, east, south, west));
    }

    public void changeIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public ArrayList<MCoordinate> getPunkte() {
        return punkte;
    }


    public boolean getIsSelected() {
        return isSelected;
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

    public MCoordinate getVisibleCoordinates() {
        return visibleCoordinates;
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
            return new MCoordinate(x, y, 0);
        } else {
            System.out.println("ID-Fehler");
            return new MCoordinate(-1, -1, -1);
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
            if (knotenpunkteArray.get(i).getVisibleCoordinate().toStringCoordinates().equals(coordsString)) {
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
            possibleID = getKnotenpunkteArray().get(i).getListOfGroupId().get(0);
            idOccurrences = 0;
            System.out.println("Enthählt possibleID: [" + possibleID + "] nameArray: [" + nameArray + "] ?");

            if (possibleID.contains(nameArray)) {
                System.out.println("Ja");

                for (MKnotenpunkt k : getKnotenpunkteArray()) {
                    for (String id : k.getListOfGroupId()) {
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

    public MCoordinate getNorth() {
        return north;
    }

    public MCoordinate getSouth() {
        return south;
    }

    public MCoordinate getEast() {
        return east;
    }

    public MCoordinate getWest() {
        return west;
    }

    public ArrayList<MCoordinate> getPunkteNeu() {
        return punkteNeu;
    }

    public void erhöhePunkte(ArrayList<MCoordinate> ecken, int factor){
        for (MCoordinate samePoint : ecken ){
            for (int indexi = 0; indexi < this.getPunkteNeu().size(); indexi++) {

                MCoordinate currentpoint = this.getPunkte().get(indexi);

                if (currentpoint.istGleich(samePoint)) {

                    int index = this.getPunkte().indexOf(currentpoint);
                    MCoordinate currentPointRel = this.getPunkteNeu().get(index);
                    currentPointRel.setZ(currentPointRel.getZ() + factor);
                }
                this.createHöhenArray();

                boolean isLand = true;
                for(int m = 0; m<this.höhen.size()&& isLand; m++){
                    if(this.höhen.get(m)<0){
                        isLand = false;
                        this.setState(EBuildType.water);
                    }
                }
            }
        }
    }
    public int getMeZ(MCoordinate m){

        int zet = 0;

        for(int indexi = 0; indexi < this.getPunkteNeu().size(); indexi++){
            MCoordinate currentpoint = this.getPunkte().get(indexi);
            if(currentpoint.istGleich(m)){
                int index = this.getPunkte().indexOf(currentpoint);
                MCoordinate currentPointRel = this.getPunkteNeu().get(index);
                zet = (int)currentPointRel.getZ();
            }
        }
        return zet;
    }


    public void erhöheSchnittpunkte(MTile feld, int factor){

        for (MCoordinate samePoint : this.intersection(feld)) {

            for (int indexi = 0; indexi < feld.getPunkteNeu().size(); indexi++) {

                MCoordinate currentpoint = feld.getPunkte().get(indexi);
                if (currentpoint.istGleich(samePoint)) {

                    int index = feld.getPunkte().indexOf(currentpoint);
                    MCoordinate currentPointRel = feld.getPunkteNeu().get(index);
                    currentPointRel.setZ(currentPointRel.getZ() + factor);

                }
            }
        }
    }

    public void erhöheSchnittpunktemitZwei(MTile feld, int factor){

        for (MCoordinate samePoint : this.intersection(feld)) {

            for (int indexi = 0; indexi < feld.getPunkteNeu().size(); indexi++) {

                MCoordinate currentpoint = feld.getPunkte().get(indexi);
                if (currentpoint.istGleich(samePoint)) {

                    int index = feld.getPunkte().indexOf(currentpoint);
                    MCoordinate currentPointRel = feld.getPunkteNeu().get(index);
                    currentPointRel.setZ(currentPointRel.getZ() + factor);

                }
            }
        }
    }


    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return Collections.min(this.höhen);
    }

    //-1 nach unten geneigt, 0 gerade,
    public boolean isSchraeg() {
        Integer i = Integer.MAX_VALUE;
        boolean isSchraeg = false;
        for (Integer h : höhen
        ) {
            if (i == Integer.MAX_VALUE) {
                i = h;
            }
            if (!i.equals(h)) {
                isSchraeg = true;
            }
        }
        return isSchraeg;
    }

    public void setHoch(boolean hoch) {
        this.hoch = hoch;
    }

    public boolean isHoch() {
        return hoch;
    }

    public boolean isStation() {
        return isStation;
    }

    public void setStation(boolean station) {
        isStation = station;
    }

    public void setStation(int halteStellenID) {
        isStation = true;
        this.haltestellenID = halteStellenID;
    }

    public void reset() {

        isFirstTile = true;
        knotenpunkteArray = new ArrayList<>();
        possibleConnections = EnumSet.noneOf(EDirections.class);
        state = EBuildType.free;
        connectedBuilding = null;
        buildingOnTile = null;
        isStation = false;
        this.haltestellenID = -1;

    }
}
